/*
    Import standard OpenOffice.org API classes. For more information on
    these classes and the OpenOffice.org API, see the OpenOffice.org
    Developers Guide at:

        http://api.openoffice.org/

    Information on the Groovy UNO Extension can be found at:

        https://github.com/cbmarcum/guno-extension
*/

import com.sun.star.frame.XModel
import com.sun.star.text.XTextDocument
import com.sun.star.text.XText
import com.sun.star.text.XTextRange

import org.openoffice.guno.UnoExtension // the Groovy UNO Extension

/*
    Import XScriptContext class. An instance of this class is available
    to all Groovy scripts in the global variable "XSCRIPTCONTEXT". This
    variable can be used to access the document for which this script
    was invoked.

    Methods available are:

        XSCRIPTCONTEXT.getDocument() returns XModel
        XSCRIPTCONTEXT.getInvocationContext() returns XScriptInvocationContext or NULL
        XSCRIPTCONTEXT.getDesktop() returns XDesktop
        XSCRIPTCONTEXT.getComponentContext() returns XComponentContext

    For more information on using this class see the scripting
    developer guides at:

        http://api.openoffice.org/docs/DevelopersGuide/ScriptingFramework/ScriptingFramework.xhtml
*/

/*
    Apache Groovy is an agile and dynamic language for the Java Virtual Machine.
    It builds upon the strengths of Java but has additional power features
    inspired by languages like Python, Ruby and Smalltalk.
    Groovy makes modern programming features available to Java developers
    with almost-zero learning curve.

    Variables in Groovy can be dynamically typed and declared as:
    def x = 12
    Or statically typed and declared as:
    Integer x = 12


    For more information on the language see:

        http://groovy-lang.org/index.html

    To get involved with Apache Groovy project see:

        http://groovy.apache.org/
 */

String output = "Hello World (in Groovy)"

XModel oDoc = XSCRIPTCONTEXT.getDocument()

XTextDocument xTextDoc = oDoc.guno(XTextDocument.class)
XText xText = xTextDoc.getText()
XTextRange xTextRange = xText.getEnd()

// xTextRange.setString( "Hello World (in Groovy)" )
xTextRange.string = output

// Groovy OpenOffice scripts should always return 0
return 0
