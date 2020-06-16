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

import com.sun.star.reflection.InvocationTargetException;
import com.sun.star.script.framework.container.ScriptMetaData;
import com.sun.star.script.framework.provider.ClassLoaderFactory;
import com.sun.star.script.framework.provider.NoSuitableClassLoaderException;
import com.sun.star.script.provider.ScriptFrameworkErrorException;
import com.sun.star.script.provider.ScriptFrameworkErrorType;
import com.sun.star.script.provider.XScript;
import com.sun.star.script.provider.XScriptContext;
import com.sun.star.uno.Any;
import com.sun.star.uno.Type;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;
import java.net.URL;


public class GroovyScript implements XScript {

    private XScriptContext xScriptContext;
    private ScriptMetaData scriptMetaData;

    // this constructor matches dev guide
    public GroovyScript(XScriptContext xsc, ScriptMetaData smd) {
        this.xScriptContext = xsc;
        this.scriptMetaData = smd;
    }


    /**
     * documentStorageID and document reference
     * for use in script name resolving
     *
     * @param aParams        All parameters; pure, out params are
     *                       undefined in sequence, i.e., the value
     *                       has to be ignored by the callee
     * @param aOutParamIndex Out indices
     * @param aOutParam      Out parameters
     * @throws ScriptFrameworkErrorException  If there is MalformedURLException or a
     *                                        NoSuitableClassLoaderException caught then
     *                                        this information is captured and rethrown as
     *                                        this exception type.
     * @throws InvocationTargetException If the running script throws
     *                                   an exception this information
     *                                   is captured and rethrown as
     *                                   this exception type.
     * @returns The value returned from the function
     * being invoked
     */
    public Object invoke( /*IN*/Object[] aParams,
            /*OUT*/short[][] aOutParamIndex,
            /*OUT*/Object[][] aOutParam)
            throws ScriptFrameworkErrorException,
            InvocationTargetException {

        // Initialise the out parameters - not used at the moment
        aOutParamIndex[0] = new short[0];
        aOutParam[0] = new Object[0];


        ClassLoader cl = null;
        URL sourceUrl = null;

        try {
            cl = ClassLoaderFactory.getURLClassLoader(scriptMetaData);
            sourceUrl = scriptMetaData.getSourceURL();
        } catch (java.net.MalformedURLException mfu) {
            System.err.println("Caught java.net.MalformedURLException");
            // Framework error
            throw new ScriptFrameworkErrorException(
                    mfu.getMessage(), null,
                    scriptMetaData.getLanguageName(), scriptMetaData.getLanguage(),
                    ScriptFrameworkErrorType.MALFORMED_URL);
        } catch (NoSuitableClassLoaderException nsc) {
            System.err.println("Caught NoSuitableClassLoaderException");

            // Framework error
            throw new ScriptFrameworkErrorException(
                    nsc.getMessage(), null,
                    scriptMetaData.getLanguageName(), scriptMetaData.getLanguage(),
                    ScriptFrameworkErrorType.UNKNOWN);
        }
        // Set class loader to be used for class files
        // and jar files
        Thread.currentThread().setContextClassLoader(cl);

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);

        binding.setProperty("XSCRIPTCONTEXT", xScriptContext);
        binding.setProperty("ARGUMENTS", aParams);

        try {
            String source = null;
            Object result = null;

            ScriptEditorForGroovy editor =
                    ScriptEditorForGroovy.getEditor(
                            sourceUrl);

            if (editor != null) {
                result = editor.execute();

                if (result == null) {

                    return new Any(new Type(), null);
                }

                return result;
            }

            scriptMetaData.loadSource();
            source = scriptMetaData.getSource();

            if (source == null || source.length() == 0) {
                System.err.println("Failed to read script. Script not found or empty");
                throw new ScriptFrameworkErrorException(
                        "Failed to read script. Script not found or empty", null,
                        scriptMetaData.getLanguageName(), scriptMetaData.getLanguage(),
                        ScriptFrameworkErrorType.NO_SUCH_SCRIPT);
            }

            try {
                result = shell.evaluate(source);
            } catch (CompilationFailedException e) {
                System.err.println("Caught a CompilationFailedException");
                throw new ScriptFrameworkErrorException(
                        e.getMessage(), null,
                        scriptMetaData.getLanguageName(), scriptMetaData.getLanguage(),
                        ScriptFrameworkErrorType.UNKNOWN);
            }


            if (result == null) {

                return new Any(new Type(), null);
            }

            return result;
        } catch (Exception e) {
            // DEBUG
            System.out.println("Failed to read script. Unknown Error");

            throw new ScriptFrameworkErrorException(
                    "Failed to read script. Unknown Error", null,
                    scriptMetaData.getLanguageName(), scriptMetaData.getLanguage(),
                    ScriptFrameworkErrorType.UNKNOWN);
        }
    }

    private void raiseEditor(int lineNum) {
        ScriptEditorForGroovy editor = null;
        try {
            URL sourceUrl = scriptMetaData.getSourceURL();
            editor = ScriptEditorForGroovy.getEditor(sourceUrl);
            if (editor == null) {
                editor = ScriptEditorForGroovy.getEditor();
                editor.edit(xScriptContext, scriptMetaData);
                editor = ScriptEditorForGroovy.getEditor(sourceUrl);
            }
            if (editor != null) {
                editor.indicateErrorLine(lineNum);
            }
        } catch (Exception ignore) {
        }
    }



}