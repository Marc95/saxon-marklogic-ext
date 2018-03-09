/*
 * The MIT License
 *
 * Copyright 2017 EXT-acourt.
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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.ma.map.MapType;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmMap;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author EXT-acourt
 */
public class MarkLogicQueryURITest {
    
    private final HashMap<XdmAtomicValue, XdmAtomicValue> CONNECT;
    
    public MarkLogicQueryURITest() {
        super();
        this.CONNECT = new HashMap<>();
        this.CONNECT.put(new XdmAtomicValue("server"), new XdmAtomicValue(System.getProperty("testServer") == null ? "localhost" : System.getProperty("testServer")));
        this.CONNECT.put(new XdmAtomicValue("port"), new XdmAtomicValue(System.getProperty("testPort") == null ? 8004 : Integer.parseInt(System.getProperty("testPort"))));
        this.CONNECT.put(new XdmAtomicValue("user"), new XdmAtomicValue(System.getProperty("testUser") == null ? "admin" : System.getProperty("testUser")));
        this.CONNECT.put(new XdmAtomicValue("password"), new XdmAtomicValue(System.getProperty("testPassword") == null ? "admin" : System.getProperty("testPassword")));
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getFunctionQName method, of class MarkLogicQueryURI.
     */
    @Test
    public void testGetFunctionQName() {
        MarkLogicQueryURI instance = new MarkLogicQueryURI();
        StructuredQName expResult = new StructuredQName("mkl-ext", "fr:askjadev:xml:extfunctions", "marklogic-query-uri");
        StructuredQName result = instance.getFunctionQName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getArgumentTypes method, of class MarkLogicQueryURI.
     */
    @Test
    public void testGetArgumentTypes() {
        MarkLogicQueryURI instance = new MarkLogicQueryURI();
        SequenceType[] expResult = new SequenceType[] { SequenceType.SINGLE_STRING, MapType.OPTIONAL_MAP_ITEM, MapType.OPTIONAL_MAP_ITEM };
        SequenceType[] result = instance.getArgumentTypes();
        assertEquals(expResult.length, result.length);
        for (int i=0; i<expResult.length; i++) {
            assertEquals("Entry " + i + " differs from expected: ", expResult[i], result[i]);
        }
    }

    /**
     * Test of getMinimumNumberOfArguments method, of class MarkLogicQueryURI.
     */
    @Test
    public void testGetMinimumNumberOfArguments() {
        MarkLogicQueryURI instance = new MarkLogicQueryURI();
        int expResult = 2;
        int result = instance.getMinimumNumberOfArguments();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaximumNumberOfArguments method, of class MarkLogicQueryURI.
     */
    @Test
    public void testGetMaximumNumberOfArguments() {
        MarkLogicQueryURI instance = new MarkLogicQueryURI();
        int expResult = 3;
        int result = instance.getMaximumNumberOfArguments();
        assertEquals(expResult, result);
    }

    /**
     * Test of getResultType method, of class MarkLogicQueryURI.
     */
    @Test
    public void testGetResultType() {
        SequenceType[] sts = null;
        MarkLogicQueryURI instance = new MarkLogicQueryURI();
        SequenceType expResult = SequenceType.ANY_SEQUENCE;
        SequenceType result = instance.getResultType(sts);
        assertEquals(expResult, result);
    }

    /**
     * Test of makeCallExpression method.
     * @throws net.sf.saxon.trans.XPathException
     * @throws net.sf.saxon.s9api.SaxonApiException
     */
    @Test
    public void testQueryModule2Args() throws XPathException, SaxonApiException {
        Configuration config = new Configuration();
        config.registerExtensionFunction(new MarkLogicQueryURI());
        Processor proc = new Processor(config);
        XPathCompiler xpc = proc.newXPathCompiler();
        try {
            xpc.declareNamespace(MarkLogicQueryURI.EXT_NS_COMMON_PREFIX, MarkLogicQueryURI.EXT_NAMESPACE_URI);
            QName varConf = new QName("config");
            QName varUri = new QName("xqueryUri");
            xpc.declareVariable(varConf);
            xpc.declareVariable(varUri);
            XPathSelector xp = xpc.compile(MarkLogicQueryURI.EXT_NS_COMMON_PREFIX + ":" + MarkLogicQueryURI.FUNCTION_NAME + "($xqueryUri, $config)").load();
            XdmValue xqConfig = new XdmMap(CONNECT);
            xp.setVariable(varConf, xqConfig);
            String xqueryUri = this.getClass().getClassLoader().getResource("MarkLogicQueryURITest.xqy").toString();
            XdmAtomicValue xqUri = new XdmAtomicValue(xqueryUri);
            xp.setVariable(varUri, xqUri);
            XdmValue result = xp.evaluate();
            SequenceIterator it = result.getUnderlyingValue().iterate();
            Item item = it.next();
            int count = 1;
            while (item != null) {
                assertEquals(Integer.toString(count++), item.getStringValue());
                item = it.next();
            }
            it.close();
        }
        catch (XPathException | SaxonApiException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Test KO / Argument with wrong type inside config map
     * @throws SaxonApiException
     */
    @Test(expected = SaxonApiException.class)
    public void testQueryModule2Args_WrongParamType() throws SaxonApiException {
        Configuration config = new Configuration();
        config.registerExtensionFunction(new MarkLogicQueryURI());
        Processor proc = new Processor(config);
        XPathCompiler xpc = proc.newXPathCompiler();
        try {
            xpc.declareNamespace(MarkLogicQueryURI.EXT_NS_COMMON_PREFIX, MarkLogicQueryURI.EXT_NAMESPACE_URI);
            QName varConf = new QName("config");
            QName varUri = new QName("xqueryUri");
            xpc.declareVariable(varConf);
            xpc.declareVariable(varUri);
            XPathSelector xp = xpc.compile(MarkLogicQueryURI.EXT_NS_COMMON_PREFIX + ":" + MarkLogicQueryURI.FUNCTION_NAME + "($xqueryUri, $config)").load();
            CONNECT.put(new XdmAtomicValue("port"), new XdmAtomicValue("string"));
            XdmMap xqConfig = new XdmMap(CONNECT);
            xp.setVariable(varConf, xqConfig);
            String xqueryUri = this.getClass().getClassLoader().getResource("MarkLogicQueryURITest.xqy").toString();
            XdmAtomicValue xqUri = new XdmAtomicValue(xqueryUri);
            xp.setVariable(varUri, xqUri);
            xp.evaluate();
        }
        catch (SaxonApiException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Test KO / Missing mandatory argument inside config map
     * @throws SaxonApiException
     */
    @Test(expected = SaxonApiException.class)
    public void testQueryModule2Args_MissingParam() throws SaxonApiException {
        Configuration config = new Configuration();
        config.registerExtensionFunction(new MarkLogicQueryURI());
        Processor proc = new Processor(config);
        XPathCompiler xpc = proc.newXPathCompiler();
        try {
            xpc.declareNamespace(MarkLogicQueryURI.EXT_NS_COMMON_PREFIX, MarkLogicQueryURI.EXT_NAMESPACE_URI);
            QName varConf = new QName("config");
            QName varUri = new QName("xqueryUri");
            xpc.declareVariable(varConf);
            xpc.declareVariable(varUri);
            XPathSelector xp = xpc.compile(MarkLogicQueryURI.EXT_NS_COMMON_PREFIX + ":" + MarkLogicQueryURI.FUNCTION_NAME + "($xqueryUri, $config)").load();
            CONNECT.remove(new XdmAtomicValue("server"));
            XdmMap xqConfig = new XdmMap(CONNECT);
            xp.setVariable(varConf, xqConfig);
            String xqueryUri = this.getClass().getClassLoader().getResource("MarkLogicQueryURITest.xqy").toString();
            XdmAtomicValue xqUri = new XdmAtomicValue(xqueryUri);
            xp.setVariable(varUri, xqUri);
            xp.evaluate();
        }
        catch (SaxonApiException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Test KO / 2nd argument wrong type
     * @throws SaxonApiException
     */
    @Test(expected = SaxonApiException.class)
    public void testQueryModule2Args_BadArgument() throws SaxonApiException {
        Configuration config = new Configuration();
        config.registerExtensionFunction(new MarkLogicQueryURI());
        Processor proc = new Processor(config);
        XPathCompiler xpc = proc.newXPathCompiler();
        try {
            xpc.declareNamespace(MarkLogicQueryURI.EXT_NS_COMMON_PREFIX, MarkLogicQueryURI.EXT_NAMESPACE_URI);
            QName varConf = new QName("config");
            QName varUri = new QName("xqueryUri");
            xpc.declareVariable(varConf);
            xpc.declareVariable(varUri);
            XPathSelector xp = xpc.compile(MarkLogicQueryURI.EXT_NS_COMMON_PREFIX + ":" + MarkLogicQueryURI.FUNCTION_NAME + "($xqueryUri, $config)").load();
            XdmAtomicValue xqConfig = new XdmAtomicValue("string");
            xp.setVariable(varConf, xqConfig);
            String xqueryUri = this.getClass().getClassLoader().getResource("MarkLogicQueryURITest.xqy").toString();
            XdmAtomicValue xqUri = new XdmAtomicValue(xqueryUri);
            xp.setVariable(varUri, xqUri);
            xp.evaluate();
        }
        catch (SaxonApiException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Test OK with XSL
     * @throws XPathException
     * @throws TransformerConfigurationException
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testXSL_QueryOK() throws XPathException, TransformerConfigurationException, URISyntaxException {
        TransformerFactory factory = TransformerFactory.newInstance();
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) factory;
        Configuration config = new Configuration();
        config.registerExtensionFunction(new MarkLogicQueryURI());
        tFactoryImpl.setConfiguration(config);
        try {
            Source xslt = new StreamSource(this.getClass().getClassLoader().getResource("MarkLogicQueryURITest_OK.xsl").toURI().toString());
            TransformerImpl transformer = (TransformerImpl) factory.newTransformer(xslt);
            transformer.setParameter("config", new XdmMap(CONNECT));
            Source text = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("MarkLogicQuery_DummySource.xml"));
            StringWriter result = new StringWriter();
            transformer.transform(text, new StreamResult(result));
        }
        catch (XPathException | TransformerConfigurationException | URISyntaxException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Test KO with XSL - XQuery file not found
     * @throws XPathException
     * @throws TransformerConfigurationException
     * @throws java.net.URISyntaxException
     */
    @Test(expected = XPathException.class)
    public void testXSL_QueryKO_FileNotFound() throws XPathException, TransformerConfigurationException, URISyntaxException {
        TransformerFactory factory = TransformerFactory.newInstance();
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) factory;
        Configuration config = new Configuration();
        config.registerExtensionFunction(new MarkLogicQueryURI());
        tFactoryImpl.setConfiguration(config);
        try {
            Source xslt = new StreamSource(this.getClass().getClassLoader().getResource("MarkLogicQueryURITest_FileNotFound.xsl").toURI().toString());
            TransformerImpl transformer = (TransformerImpl) factory.newTransformer(xslt);
            transformer.setParameter("config", new XdmMap(CONNECT));
            Source text = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("MarkLogicQuery_DummySource.xml"));
            StringWriter result = new StringWriter();
            transformer.transform(text, new StreamResult(result));
        }
        catch (XPathException | TransformerConfigurationException | URISyntaxException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Test OK with XSL + external variables
     * @throws XPathException
     * @throws TransformerConfigurationException
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws net.sf.saxon.s9api.SaxonApiException
     */
    @Test
    public void testXSL_ExternalVar_QueryOK() throws XPathException, TransformerConfigurationException, URISyntaxException, IOException, SaxonApiException {
        TransformerFactory factory = TransformerFactory.newInstance();
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) factory;
        Configuration config = new Configuration();
        config.registerExtensionFunction(new MarkLogicQueryURI());
        tFactoryImpl.setConfiguration(config);
        Processor processor = new Processor(config);
        try {
            Source xslt = new StreamSource(this.getClass().getClassLoader().getResource("MarkLogicQueryURITest_ExternalVariables_OK.xsl").toURI().toString());
            TransformerImpl transformer = (TransformerImpl) factory.newTransformer(xslt);
            transformer.setParameter("config", new XdmMap(CONNECT));
            Source text = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("MarkLogicQuery_DummySource.xml"));
            StringWriter result = new StringWriter();
            transformer.transform(text, new StreamResult(result));
            // System.out.println(result.toString());
            DocumentBuilder builder = processor.newDocumentBuilder();
            XdmNode resultNode = (XdmNode) builder.build(new StreamSource(IOUtils.toInputStream(result.toString(), "UTF-8")));
            XdmSequenceIterator it = resultNode.axisIterator(Axis.DESCENDANT, new QName("external-variable"));
            while (it.hasNext()) {
                XdmNode element = (XdmNode) it.next();
                assertEquals("true", element.getAttributeValue(new QName("isTypeAsExpected")));
            }
            it.close();
        }
        catch (XPathException | TransformerConfigurationException | URISyntaxException | IOException | SaxonApiException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }

}
