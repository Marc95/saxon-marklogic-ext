/*
 * The MIT License
 *
 * Copyright 2018 ext-acourt.
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

import fr.askjadev.xml.extfunctions.marklogic.result.MarkLogicSequenceIterator;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import fr.askjadev.xml.extfunctions.marklogic.config.QueryConfiguration;
import java.util.Iterator;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.ma.map.HashTrieMap;
import net.sf.saxon.ma.map.KeyValuePair;
import net.sf.saxon.ma.map.MapType;
import net.sf.saxon.om.*;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.IntegerValue;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

/**
 * Abstract class representing a Saxon MarkLogic extension function
 * @author Axel Court + Emmanuel Tourdot
 */
public abstract class AbstractMLExtensionFunction extends ExtensionFunctionDefinition {

    enum ExtentionType {
        XQUERY_URI, XQUERY_STRING, MODULE;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] {
            SequenceType.SINGLE_STRING,
            MapType.OPTIONAL_MAP_ITEM,
            MapType.OPTIONAL_MAP_ITEM
        };
    }

    @Override
    public int getMinimumNumberOfArguments() {
        return 2;
    }

    @Override
    public int getMaximumNumberOfArguments() {
        return 3;
    }

    @Override
    public SequenceType getResultType(net.sf.saxon.value.SequenceType[] sts) {
        return SequenceType.ANY_SEQUENCE;
    }

    ExtensionFunctionCall constructExtensionFunctionCall(final ExtentionType type) {

        return new ExtensionFunctionCall() {

            @Override
            public Sequence call(XPathContext xpc, Sequence[] sqncs) throws XPathException {
                // Check and get the configuration
                QueryConfiguration config = getConfig(sqncs);
                // Get the XQuery or the module to invoke
                String moduleOrQuery = getXQueryOrModule(sqncs);
                // Launch
                Processor proc = new Processor(xpc.getConfiguration());
                DatabaseClient session = null;
                try {
                    session = createMarkLogicClient(config);
                    // Eval query and get result
                    DocumentBuilder builder = proc.newDocumentBuilder();
                    ServerEvaluationCall call = session.newServerEval();
                    switch (type) {
                        case MODULE:
                            call.modulePath(moduleOrQuery);
                            break;
                        case XQUERY_STRING:
                            call.xquery(moduleOrQuery);
                            break;
                        case XQUERY_URI:
                            // Read the XQuery and send it as an InputStreamHandle
                            InputStreamHandle xquery = getXQueryFromURI(moduleOrQuery);
                            call.xquery(xquery);
                            xquery.close();
                            break;
                    }
                    EvalResultIterator result = call.eval();
                    MarkLogicSequenceIterator it = new MarkLogicSequenceIterator(result, builder, session);
                    return new LazySequence(it);
                }
                catch (FailedRequestException | ForbiddenUserException ex) {
                    throw new XPathException(ex);
                }
            }
            
        };
    
    }

    private DatabaseClient createMarkLogicClient(QueryConfiguration config) {
        DatabaseClientFactory.SecurityContext authContext;
        switch (config.getAuthentication()) {
            case "digest":
                authContext = new DatabaseClientFactory.DigestAuthContext(config.getUser(), config.getPassword());
                break;
            default:
                authContext = new DatabaseClientFactory.BasicAuthContext(config.getUser(), config.getPassword());
        }
        // Init session
        final DatabaseClient session;
        if (!(config.getDatabase() == null)) {
            session = DatabaseClientFactory.newClient(config.getServer(), config.getPort(), config.getDatabase(), authContext);
        } else {
            session = DatabaseClientFactory.newClient(config.getServer(), config.getPort(), authContext);
        }
        return session;
    }

    private QueryConfiguration getConfig(Sequence[] sqncs) throws XPathException {
        QueryConfiguration config = new QueryConfiguration();
        try {
            HashTrieMap configMap = (HashTrieMap) sqncs[1].head();
            Iterator<KeyValuePair> iterator = configMap.iterator();
            while (iterator.hasNext()) {
                KeyValuePair kv = iterator.next();
                String k = kv.key.getStringValue();
                try {
                    switch (k) {
                        case "server":
                        case "user":
                        case "password":
                        case "database":
                        case "authentication":
                            config.set(k, kv.value.head().getStringValue());
                            break;
                        case "port":
                            config.set(k, ((IntegerValue) kv.value.head()).asBigInteger().intValue());
                            break;
                    }
                }
                catch (XPathException | ClassCastException ex) {
                    throw new XPathException("Some configuration entries are not in the required type; see: " + ex.getMessage());
                }
            }
            if (config.getServer() == null ||
                config.getUser() == null ||
                config.getPassword() == null ||
                config.getPort() == null) {
                throw new XPathException("Some mandatory configuration values are missing. 'server', 'port', 'user' and 'password' must be specified.");
            }
            return config;
        }
        catch (ClassCastException ex) {
            throw new XPathException("The 2nd argument must be an XPath 3.0 map defining the server and query configuration.");
        }
    }
    
    private String getXQueryOrModule(Sequence [] sqncs) throws XPathException {
        // May be a reference to a module, the URI to a local XQuery or a string containing the XQuery
        String xqueryOrModule = ((StringValue) sqncs[0].head()).getStringValue();
        return xqueryOrModule;
    }
    
    private InputStreamHandle getXQueryFromURI(String moduleOrQuery) throws XPathException {
        // TO DO : tester différentes façon de récupérer la ressource
        try {
            InputStreamHandle xquery = new InputStreamHandle(this.getClass().getClassLoader().getResourceAsStream(moduleOrQuery));
            xquery.setFormat(Format.TEXT);
            return xquery;
        }
        catch (Exception ex) {
            throw new XPathException("Error while trying to load the XQuery file: " + moduleOrQuery + "; see: " + ex.getMessage());
        }
    }
    
}
