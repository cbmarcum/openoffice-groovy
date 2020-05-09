/* ************************************************************
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *********************************************************** */


package net.codebuilders.star.script.framework.provider.groovy;

import java.io.InputStream;
import java.io.IOException;

import java.net.URL;

import com.sun.star.script.provider.XScriptContext;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class ScriptSourceModel {

    private int currentPosition = -1;
    private URL file = null;
    private ScriptSourceView view = null;

    public ScriptSourceModel(URL file) {
        this.file = file;
    }

    private String load() throws IOException {
        StringBuffer buf = new StringBuffer();
        InputStream in = file.openStream();

        byte[] contents = new byte[1024];
        int len = 0;

        while ((len = in.read(contents, 0, 1024)) != -1) {
            buf.append(new String(contents, 0, len));
        }

        try {
            in.close();
        } catch (IOException ignore) {
        }

        return buf.toString();
    }

    public String getText() {
        String result = "";

        try {
            result = load();
        } catch (IOException ioe) {
            // do nothing, empty string will be returned
        }

        return result;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void setView(ScriptSourceView view) {
        this.view = view;
    }

    public Object execute(final XScriptContext context, ClassLoader cl)
            throws Exception {
        Object result = null;
        // Thread execThread = new Thread() {
        // public void run() {
        if (cl != null) {
            /* sets this threads class loader hopefully any threads spawned by
            this will inherit this cl this enables any class files imported
            from the interpreter to be loaded.
            note: setting the classloader on the interpreter has a slightly
            different meaning in that the classloader for the interpreter seems
            only to look for source files ( bla.java ) in the classpath */

            Thread.currentThread().setContextClassLoader(cl);
        }

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);

        // reset position
        currentPosition = -1;
        view.update();

        binding.setProperty("XSCRIPTCONTEXT", context);
        binding.setProperty("ARGUMENTS", new Object[0]);

        if (view.isModified()) {
            result = shell.evaluate(view.getText());
        } else {
            result = shell.evaluate(getText());
        }
        // }
        // };    
        // execThread.start();
        return result;
    }

    public void indicateErrorLine(int lineNum) {
        System.out.println("Groovy indicateErrorLine " + lineNum);
        currentPosition = lineNum - 1;
        view.update();
    }
}
