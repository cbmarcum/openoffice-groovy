# Groovy Scripting for OpenOffice
An Apache OpenOffice extension to add the Apache Groovy language to the Scripting Framework

The build artifact of this project is an OXT file that is an OpenOffice extension that is installed into the office using 
the built-in Extension Manager. You can get the latest version on [Bintray](https://bintray.com/cmarcum/openoffice-extensions/openoffice-groovy#files).

This extension includes the [Apache Groovy](http://groovy.apache.org/) language and the [Groovy UNO Extension](https://github.com/cbmarcum/guno-extension)

The Groovy UNO Extension extends the Java UNO API's with convenience methods for Groovy Extensions and Client apps.
It uses Groovy's Extension mechanism to extend the UNO API's and more information and it's usage can be found in the [documentation](https://cbmarcum.github.io/guno-extension/).

This extension was influenced by the Beanshell scripting built into Apache OpenOffice. 
The big difference is script evaluation. In this case we're using [GroovyShell](http://www.groovy-lang.org/integrating.html#integ-groovyshell) 
to do the script evaluation. You can find out more about how all this works in the OpenOffice Developer Guide 
[Scripting Framework](https://wiki.openoffice.org/wiki/Documentation/DevGuide/Scripting/Scripting_Framework) 
section.

There is a companion extension project [OpenOffice-Groovy-Macros](https://github.com/cbmarcum/openoffice-groovy-macros) that 
is a collection of sample Beanshell and Javascript macros rewritten in Groovy. When installed in OpenOffice they can be run 
but not edited. You can however paste the code into macros you create to learn more about Groovy 
macros in OpenOffice. You can also get it on [Bintray](https://bintray.com/cmarcum/openoffice-extensions/openoffice-groovy-macros#files).

Happy Groovy Scripting :)
