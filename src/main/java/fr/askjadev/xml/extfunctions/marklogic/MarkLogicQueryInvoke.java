/*
 * The MIT License
 *
 * Copyright 2017 Axel Court.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.askjadev.xml.extfunctions.marklogic;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.StructuredQName;

/**
 * This class is an extension function for Saxon. It must be declared by
 * <tt>configuration.registerExtensionFunction(new MarkLogicQueryInvoke());</tt>, or
 * via Saxon Configuration file
 * (<a href=http://www.saxonica.com/documentation9.7/index.html#!configuration/configuration-file>Saxon
 * documentation</a>). In gaulois-pipe, it just has to be in the classpath.
 *
 * The first argument (xs:string) is the path to an XQuery module already deployed on the MarkLogic Server instance.
 * The second argument is an XPath 3.0 map containing the server and database configuration.
 * The third argument is an XPath 3.0 map containing the external variables values.
 * 
 * Use as :
 * <tt>&lt;xsl:sequence xmlns:mkl-ext="fr:askjadev:xml:extfunctions" select="
 *   mkl-ext:marklogic-query-invoke(
 *     'for $i in 1 to 10 return {$i}',
 *     map{
 *       'server':'localhost',
 *       'port':8004,
 *       'user':'admin',
 *       'password':'admin'
 *     },
 *     map{
 *       QName('http://namespace','pre:string'):'string value'
 *     }
 *   )"
 * /&gt;</tt>
 *
 * @author Emmanuel Tourdot
 */
public class MarkLogicQueryInvoke extends AbstractMLExtensionFunction {

    public static final String EXT_NAMESPACE_URI = "fr:askjadev:xml:extfunctions";
    public static final String FUNCTION_NAME = "marklogic-query-invoke";
    public static final String EXT_NS_COMMON_PREFIX = "mkl-ext";

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName(EXT_NS_COMMON_PREFIX, EXT_NAMESPACE_URI, FUNCTION_NAME);
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return constructExtensionFunctionCall(ExtentionType.MODULE);
    }

}
