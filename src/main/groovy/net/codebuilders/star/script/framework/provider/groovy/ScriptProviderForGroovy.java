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

import com.sun.star.comp.loader.FactoryHelper;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.script.framework.container.ScriptMetaData;
import com.sun.star.script.framework.provider.ScriptEditor;
import com.sun.star.script.framework.provider.ScriptProvider;
import com.sun.star.script.provider.XScript;
import com.sun.star.script.provider.XScriptContext;
import com.sun.star.uno.XComponentContext;

public class ScriptProviderForGroovy {
    public static class _ScriptProviderForGroovy extends ScriptProvider {
        public _ScriptProviderForGroovy(XComponentContext ctx) {
            super(ctx, "Groovy");
        }

        public XScript getScript(String scriptURI)
                throws com.sun.star.uno.RuntimeException,
                com.sun.star.script.provider.ScriptFrameworkErrorException {
            GroovyScript script = null;

            try {
                ScriptMetaData scriptMetaData = getScriptData(scriptURI);
                XScriptContext xScriptContext = getScriptingContext();
                script = new GroovyScript(xScriptContext, scriptMetaData);
            } catch (com.sun.star.uno.Exception e) {
                System.err.println("Failed to get script: " + scriptURI);
            }
            return script;
        }

        public boolean hasScriptEditor() {
            return true;
        }

        public ScriptEditor getScriptEditor() {
            // return new ScriptEditorForGroovy();
            return ScriptEditorForGroovy.getEditor();
        }
    }

    // code to register and create a service factory for ScriptProviderForYourLanguage
    // this code is the standard code for registering classes which implement UNO services
    public static XSingleServiceFactory __getServiceFactory(String implName,
                                                            XMultiServiceFactory multiFactory,
                                                            XRegistryKey regKey) {
        XSingleServiceFactory xSingleServiceFactory = null;

        if (implName.equals(ScriptProviderForGroovy._ScriptProviderForGroovy.class.getName())) {
            xSingleServiceFactory = FactoryHelper.getServiceFactory(
                    ScriptProviderForGroovy._ScriptProviderForGroovy.class,
                    "com.sun.star.script.provider.ScriptProviderForGroovy",
                    multiFactory,
                    regKey);
        }

        return xSingleServiceFactory;
    }

    // NOTE: dev guide was unclear here. The implementation string needs the full java package included.
    // also note the services follow a naming convention not the package names.
    // TODO: we may need to add a com.sun.star.script.browse.BrowseNode service later.
    public static boolean __writeRegistryServiceInfo(XRegistryKey regKey) {
        String impl =
                "net.codebuilders.star.script.framework.provider.groovy.ScriptProviderForGroovy$_ScriptProviderForGroovy";

        String service1 =
                "com.sun.star.script.browse.BrowseNode";

        String service2 =
                "com.sun.star.script.provider.ScriptProvider";

        String service3 =
                "com.sun.star.script.provider.LanguageScriptProvider";

        String service4 =
                "com.sun.star.script.provider.ScriptProviderForGroovy";

        FactoryHelper.writeRegistryServiceInfo(impl, service1, regKey);
        FactoryHelper.writeRegistryServiceInfo(impl, service2, regKey);
        FactoryHelper.writeRegistryServiceInfo(impl, service3, regKey);
        FactoryHelper.writeRegistryServiceInfo(impl, service4, regKey);

        return true;
    }
}