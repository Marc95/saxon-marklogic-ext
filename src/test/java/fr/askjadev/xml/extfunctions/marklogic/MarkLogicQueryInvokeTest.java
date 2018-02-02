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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionLibraryDescriptor;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import net.sf.saxon.Configuration;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.s9api.*;
import net.sf.saxon.trans.XPathException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Query invoke test NB: user needs to be rest-admin in MarkLogic
 *
 * @author Emmanuel Tourdot
 */
public class MarkLogicQueryInvokeTest {

    private HashMap<XdmAtomicValue, XdmAtomicValue> CONNECT;
    private DatabaseClient client;
    private ExtensionLibrariesManager librariesManager;

    @Before
    public void setup() {
        String server = System.getProperty("testServer") == null ? "localhost" : System.getProperty("testServer");
        Integer port = System.getProperty("testPort") == null ? 8004 : Integer.parseInt(System.getProperty("testPort"));
        String user = System.getProperty("testUser") == null ? "admin" : System.getProperty("testUser");
        String password = System.getProperty("testPassword") == null ? "admin" : System.getProperty("testPassword");
        CONNECT = new HashMap<>();
        CONNECT.put(new XdmAtomicValue("server"), new XdmAtomicValue(server));
        CONNECT.put(new XdmAtomicValue("port"), new XdmAtomicValue(port));
        CONNECT.put(new XdmAtomicValue("user"), new XdmAtomicValue(user));
        CONNECT.put(new XdmAtomicValue("password"), new XdmAtomicValue(password));
        ExtensionLibraryDescriptor moduleDescriptor = new ExtensionLibraryDescriptor();
        moduleDescriptor.setPath("/ext/test/evaltest.xqy");
        client = DatabaseClientFactory.newClient(server, port, new DatabaseClientFactory.BasicAuthContext(user, password));
        librariesManager = client.newServerConfigManager().newExtensionLibrariesManager();
        InputStreamHandle xquery = new InputStreamHandle(this.getClass().getClassLoader().getResourceAsStream("evaltest.xqy"));
        xquery.setFormat(Format.TEXT);
        librariesManager.write(moduleDescriptor, xquery);
    }

    @After
    public void tearDown() {
        if (client != null) {
            librariesManager.delete("/ext/test/evaltest.xqy");
            client.release();
        }
    }

    @Test
    public void testInvokeModule() throws Exception {
        Configuration config = new Configuration();
        config.registerExtensionFunction(new MarkLogicQueryInvoke());
        Processor proc = new Processor(config);
        XPathCompiler xpc = proc.newXPathCompiler();
        try {
            xpc.declareNamespace(MarkLogicQueryInvoke.EXT_NS_COMMON_PREFIX, MarkLogicQueryInvoke.EXT_NAMESPACE_URI);
            QName var = new QName("config");
            xpc.declareVariable(var);
            XPathSelector xp = xpc.compile(MarkLogicQueryInvoke.EXT_NS_COMMON_PREFIX + ":" + MarkLogicQueryInvoke.FUNCTION_NAME + "('/ext/test/evaltest.xqy', $config)").load();
            XdmMap xqConfig = new XdmMap(CONNECT);
            xp.setVariable(var, xqConfig);
            XdmValue result = xp.evaluate();
            SequenceIterator it = result.getUnderlyingValue().iterate();
            Item item = it.next();
            assertEquals("test", item.getStringValue());
            it.close();
        }
        catch (SaxonApiException | XPathException ex) {
            throw ex;
        }
    }

}
