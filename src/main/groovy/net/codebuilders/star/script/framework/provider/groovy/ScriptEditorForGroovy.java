/* ************************************************************************
 *
 * Copyright 2020 Code Builders, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *********************************************************************** */

package net.codebuilders.star.script.framework.provider.groovy;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;

import com.sun.star.script.provider.XScriptContext;
import com.sun.star.script.framework.provider.ScriptEditor;
import com.sun.star.script.framework.provider.SwingInvocation;
import com.sun.star.script.framework.container.ScriptMetaData;
import com.sun.star.script.framework.provider.ClassLoaderFactory;

public class ScriptEditorForGroovy implements ScriptEditor, ActionListener {
    private JFrame frame;
    private String filename;

    private ScriptSourceModel model;
    private ScriptSourceView view;

    private XScriptContext context;
    private URL scriptURL = null;
    private ClassLoader cl = null;

    // global ScriptEditorForGroovy returned for getEditor() calls
    private static ScriptEditorForGroovy theScriptEditorForGroovy;

    // global list of ScriptEditors, key is URL of file being edited
    private static Map BEING_EDITED = new HashMap();

    // template for new Groovy scripts
    private static String GROOVYTEMPLATE;

    // try to load the template for Groovy scripts
    static {
        try {
            URL url =
                    ScriptEditorForGroovy.class.getResource("template.groovy");

            InputStream in = url.openStream();
            StringBuffer buf = new StringBuffer();
            byte[] b = new byte[1024];
            int len = 0;

            while ((len = in.read(b)) != -1) {
                buf.append(new String(b, 0, len));
            }

            in.close();

            GROOVYTEMPLATE = buf.toString();
        } catch (IOException ioe) {
            GROOVYTEMPLATE = "// Groovy script";
        } catch (Exception e) {
            GROOVYTEMPLATE = "// Groovy script";
        }
    }

    /**
     * Returns the global ScriptEditorForGroovy instance.
     */
    public static ScriptEditorForGroovy getEditor() {
        if (theScriptEditorForGroovy == null) {
            synchronized (ScriptEditorForGroovy.class) {
                if (theScriptEditorForGroovy == null) {
                    theScriptEditorForGroovy =
                            new ScriptEditorForGroovy();
                }
            }
        }
        return theScriptEditorForGroovy;
    }

    /**
     * Get the ScriptEditorForGroovy instance for this URL
     *
     * @param url The URL of the script source file
     * @return The ScriptEditorForGroovy associated with
     * the given URL if one exists, otherwise null.
     */
    public static ScriptEditorForGroovy getEditor(URL url) {
        synchronized (BEING_EDITED) {
            return (ScriptEditorForGroovy) BEING_EDITED.get(url);
        }
    }

    /**
     * Returns whether or not the script source being edited in this
     * ScriptEditorForGroovy has been modified
     */
    public boolean isModified() {
        return view.isModified();
    }

    /**
     * Returns the text being displayed in this ScriptEditorForGroovy
     *
     * @return The text displayed in this ScriptEditorForGroovy
     */
    public String getText() {
        return view.getText();
    }

    /**
     * Returns the template text for Groovy scripts
     *
     * @return The template text for Groovy scripts
     */
    public String getTemplate() {
        return GROOVYTEMPLATE;
    }

    /**
     * Returns the default extension for Groovy scripts
     *
     * @return The default extension for Groovy scripts
     */
    public String getExtension() {
        return "groovy";
    }


    /**
     * Indicates the line where error occurred
     */
    public void indicateErrorLine(int lineNum) {
        model.indicateErrorLine(lineNum);
    }

    /**
     * Executes the script edited by the editor
     */
    public Object execute() throws Exception {
        frame.toFront();
        return model.execute(context, cl);
    }

    /**
     * Opens an editor window for the specified ScriptMetaData.
     * If an editor window is already open for that data it will be
     * moved to the front.
     *
     * @param context The context in which to execute the script
     * @param entry   The metadata describing the script
     */
    public void edit(final XScriptContext context, ScriptMetaData entry) {
        if (entry != null) {
            try {
                ClassLoader cl = null;
                try {
                    cl = ClassLoaderFactory.getURLClassLoader(entry);
                } catch (Exception ignore) // TODO re-examine error handling
                {
                }
                final ClassLoader theCl = cl;
                String sUrl = entry.getParcelLocation();
                if (!sUrl.endsWith("/")) {
                    sUrl += "/";
                }
                sUrl += entry.getLanguageName();
                final URL url = entry.getSourceURL();
                SwingInvocation.invoke(
                        new Runnable() {
                            public void run() {
                                ScriptEditorForGroovy editor;
                                synchronized (BEING_EDITED) {
                                    editor = (ScriptEditorForGroovy)
                                            BEING_EDITED.get(url);
                                    if (editor == null) {
                                        editor = new ScriptEditorForGroovy(
                                                context, theCl, url);
                                        BEING_EDITED.put(url, editor);
                                    }
                                }
                                editor.frame.toFront();
                            }
                        });
            } catch (IOException ioe) {
                showErrorMessage("Error loading file: " + ioe.getMessage());
            }
        }
    }

    private ScriptEditorForGroovy() {
    }

    private ScriptEditorForGroovy(XScriptContext context, ClassLoader cl, URL url) {
        this.context = context;
        this.scriptURL = url;
        this.model = new ScriptSourceModel(url);
        this.filename = url.getFile();
        this.cl = cl;
        try {
            Class c = Class.forName(
                    "org.openoffice.netbeans.editor.NetBeansSourceView");

            Class[] types = new Class[]{ScriptSourceModel.class};

            java.lang.reflect.Constructor ctor = c.getConstructor(types);

            if (ctor != null) {
                Object[] args = new Object[]{this.model};
                this.view = (ScriptSourceView) ctor.newInstance(args);
            } else {
                this.view = new PlainSourceView(model);
            }
        } catch (java.lang.Error err) {
            this.view = new PlainSourceView(model);
        } catch (Exception e) {
            this.view = new PlainSourceView(model);
        }

        this.model.setView(this.view);
        initUI();
        // frame.show();
        frame.setVisible(true);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void initUI() {
        frame = new JFrame("Groovy Debug Window: " + filename);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        doClose();
                    }
                }
        );

        String[] labels = {"Run", "Clear", "Save", "Close"};
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());

        for (int i = 0; i < labels.length; i++) {
            JButton b = new JButton(labels[i]);
            b.addActionListener(this);
            p.add(b);

            if (labels[i].equals("Save") && filename == null) {
                b.setEnabled(false);
            }
        }

        frame.getContentPane().add((JComponent) view, "Center");
        frame.getContentPane().add(p, "South");
        frame.pack();
        frame.setSize(590, 480);
        frame.setLocation(300, 200);
    }

    private void doClose() {
        if (view.isModified()) {
            int result = JOptionPane.showConfirmDialog(frame,
                    "The script has been modified. " +
                            "Do you want to save the changes?");

            if (result == JOptionPane.CANCEL_OPTION) {
                // don't close the window, just return
                return;
            } else if (result == JOptionPane.YES_OPTION) {
                boolean saveSuccess = saveTextArea();
                if (saveSuccess == false) {
                    return;
                }
            }
        }
        frame.dispose();
        shutdown();
    }

    private boolean saveTextArea() {
        boolean result = true;

        if (!view.isModified()) {
            return true;
        }

        OutputStream fos = null;
        try {
            String s = view.getText();
            fos = scriptURL.openConnection().getOutputStream();
            if (fos != null) {
                fos.write(s.getBytes());
            } else {
                showErrorMessage(
                        "Error saving script: Could not open stream for file");
                result = false;
            }
            view.setModified(false);
        } catch (IOException ioe) {
            showErrorMessage("Error saving script: " + ioe.getMessage());
            result = false;
        } catch (Exception e) {
            showErrorMessage("Error saving script: " + e.getMessage());
            result = false;
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException ignore) {
                }
            }
        }
        return result;
    }

    private void shutdown() {
        synchronized (BEING_EDITED) {
            BEING_EDITED.remove(scriptURL);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Run")) {
            try {
                execute();
            } catch (Exception invokeException) {
                showErrorMessage(invokeException.getMessage());
            }
        } else if (e.getActionCommand().equals("Close")) {
            doClose();
        } else if (e.getActionCommand().equals("Save")) {
            saveTextArea();
        } else if (e.getActionCommand().equals("Clear")) {
            view.clear();
        }
    }
}