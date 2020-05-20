/*
    Import standard OpenOffice.org API classes. For more information on
    these classes and the OpenOffice.org API, see the OpenOffice.org
    Developers Guide at:

        https://wiki.openoffice.org/wiki/Documentation/DevGuide/OpenOffice.org_Developers_Guide

    The Groovy UNO Extension adds convenience methods to the Java UNO apis.
    Notably used here is adding the guno method to XInterface which replaces
    the static UnoRuntime.queryInterface() method and the need to do a cast.
    Information on the Groovy UNO Extension can be found at:

        https://github.com/cbmarcum/guno-extension
*/

// Hello World in Groovy

import com.sun.star.frame.XModel
import com.sun.star.text.XTextDocument
import com.sun.star.text.XText
import com.sun.star.text.XTextRange
import org.openoffice.guno.UnoExtension // the Groovy UNO Extension
// import com.sun.star.uno.UnoRuntime // not needed with Groovy UNO Extension


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

// set the output text string
String output = "Hello World (in Groovy)"

// get the document model from the scripting context which is made available to all scripts
XModel xModel = XSCRIPTCONTEXT.getDocument()

//get the XTextDocument interface
// XTextDocument xTextDoc = (XTextDocument) UnoRuntime.queryInterface(XTextDocument.class, xModel) // without the Groovy UNO Extension
XTextDocument xTextDoc = xModel.guno(XTextDocument.class) // with the Groovy UNO Extension

//get the XText interface
XText xText = xTextDoc.getText()

// get an (empty) XTextRange at the end of the document
XTextRange xTextRange = xText.getEnd()

// the next two lines are interchangable
// xTextRange.setString( "Hello World (in Groovy)" ) // getter method
xTextRange.string = output // looks like property access but uses the getter or setter transparently

// Groovy OpenOffice scripts should always return 0
return 0
