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
package fr.askjadev.xml.extfunctions.marklogic.var;

import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.saxon.dom.DocumentOverNodeInfo;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.ma.map.HashTrieMap;
import net.sf.saxon.ma.map.KeyValuePair;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.Base64BinaryValue;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.HexBinaryValue;
import net.sf.saxon.value.IntegerValue;
import net.sf.saxon.value.NumericValue;
import net.sf.saxon.value.QualifiedNameValue;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utility class QueryExternalVarFactory / Get the QueryExternalVar objects from the function call arguments
 * See test class EvalTest in MarkLogic Java client API for data conversion examples.
 * @author ext-acourt
 */
public class QueryExternalVarFactory {
    
    private final BuiltInAtomicType[] ATOMIC_AS_STRING = {
        BuiltInAtomicType.STRING,
        BuiltInAtomicType.NORMALIZED_STRING,
        BuiltInAtomicType.DATE,
        BuiltInAtomicType.DATE_TIME,
        BuiltInAtomicType.DATE_TIME_STAMP,
        BuiltInAtomicType.DAY_TIME_DURATION,
        BuiltInAtomicType.YEAR_MONTH_DURATION,
        BuiltInAtomicType.DURATION,
        BuiltInAtomicType.TIME,
        BuiltInAtomicType.G_DAY,
        BuiltInAtomicType.G_MONTH,
        BuiltInAtomicType.G_MONTH_DAY,
        BuiltInAtomicType.G_YEAR,
        BuiltInAtomicType.G_YEAR_MONTH,
        BuiltInAtomicType.DECIMAL,
        BuiltInAtomicType.ANY_URI,
        BuiltInAtomicType.QNAME
    };
    
    private final BuiltInAtomicType[] ATOMIC_AS_INT = {
        BuiltInAtomicType.INT,
        BuiltInAtomicType.INTEGER,
        BuiltInAtomicType.NEGATIVE_INTEGER,
        BuiltInAtomicType.NON_NEGATIVE_INTEGER,
        BuiltInAtomicType.NON_POSITIVE_INTEGER
    };
    
    private final BuiltInAtomicType[] ATOMIC_AS_NB = {
        BuiltInAtomicType.DOUBLE,
        BuiltInAtomicType.FLOAT
    };
    
    private final BuiltInAtomicType[] ATOMIC_AS_BIN = {
        BuiltInAtomicType.BASE64_BINARY,
        BuiltInAtomicType.HEX_BINARY
    };
    
    private final BuiltInAtomicType[] ATOMIC_AS_BOOL = {
        BuiltInAtomicType.BOOLEAN
    };
    
    public ArrayList<QueryExternalVar> getExternalVariables(XPathContext xpc, Sequence[] args) throws XPathException {
        ArrayList<QueryExternalVar> externalVariables = new ArrayList<>();
        try {
            HashTrieMap variableMap = (HashTrieMap) args[2].head();
            Iterator<KeyValuePair> iterator = variableMap.iterator();
            while (iterator.hasNext()) {
                KeyValuePair kv = iterator.next();
                try {
                    QualifiedNameValue variableQName = (QualifiedNameValue) kv.key;
                    QueryExternalVar variable = new QueryExternalVar(
                            variableQName.getNamespaceURI(),
                            variableQName.getPrefix(),
                            variableQName.getLocalName()
                    );
                    SequenceIterator sequenceValue = kv.value.iterate();
                    Item initialValue = sequenceValue.next();
                    if (sequenceValue.next() != null) {
                        throw new XPathException("The external variables supplied to the query can not be multivalued: see variable " + variableQName.getPrimitiveStringValue() + ".");
                    }
                    sequenceValue.close();
                    variable.setSaxonValue(initialValue);
                    // Cast the value in a MarkLogic Server compatible format
                    try {
                        variable.setValue(getVariableValue(initialValue, xpc));
                        externalVariables.add(variable);
                    }
                    catch (XPathException ex) {
                        throw new XPathException("Error while trying to cast external variable " + variableQName.getPrimitiveStringValue() + " : " + ex.getMessage());
                    }
                }
                catch (ClassCastException ex) {
                    throw new XPathException("The external variables map keys must be of type xs:QName.");
                }
            }
            return externalVariables;
        }
        catch (ClassCastException ex) {
            throw new XPathException("The 3d argument must be an XPath 3.0 map defining the query external variables.");
        }
    }

    private Object getVariableValue(Item initialValue, XPathContext xpc) throws XPathException {
        Object value = null;
        ItemType valueType = Type.getItemType(initialValue, xpc.getConfiguration().getTypeHierarchy());
        if (valueType.isAtomicType()) {
            value = castAtomicValue(initialValue, valueType);
        }
        if (Type.isNodeType(valueType)) {
            value = castNodeValue(initialValue, valueType);
        }
        if (value == null) {
            throw new XPathException("Incompatible type: " + valueType.toExportString()); 
        }
        return value;
    }
    
    private Object castAtomicValue(Item initialValue, ItemType initialValueType) throws XPathException {
        for (BuiltInAtomicType atomicAsStringType : ATOMIC_AS_STRING) {
            if (atomicAsStringType.equals(initialValueType)) {
                return initialValue.getStringValue();
            }
        }
        for (BuiltInAtomicType atomicAsIntType : ATOMIC_AS_INT) {
            if (atomicAsIntType.equals(initialValueType)) {
                IntegerValue intValue = (IntegerValue) initialValue.atomize();
                return intValue.asBigInteger().intValue();
            }
        }
        for (BuiltInAtomicType atomicAsNbType : ATOMIC_AS_NB) {
            if (atomicAsNbType.equals(initialValueType)) {
                NumericValue decValue = (NumericValue) initialValue.atomize();
                if (initialValueType.equals(BuiltInAtomicType.DOUBLE)) {
                    return decValue.getDoubleValue();
                }
                else if (initialValueType.equals(BuiltInAtomicType.FLOAT)) {
                    return decValue.getFloatValue();
                }
            }
        }
        for (BuiltInAtomicType atomicAsBoolType : ATOMIC_AS_BOOL) {
            if (atomicAsBoolType.equals(initialValueType)) {
                BooleanValue boolValue = (BooleanValue) initialValue.atomize();
                return boolValue.effectiveBooleanValue();
            }
        }
        for (BuiltInAtomicType atomicAsBinType : ATOMIC_AS_BIN) {
            if (atomicAsBinType.equals(initialValueType)) {
                if (initialValueType.equals(BuiltInAtomicType.BASE64_BINARY)) {
                    Base64BinaryValue base64Value = (Base64BinaryValue) initialValue.atomize();
                    return DatatypeConverter.printBase64Binary(base64Value.getBinaryValue());
                }
                else if (initialValueType.equals(BuiltInAtomicType.HEX_BINARY)) {
                    HexBinaryValue hexValue = (HexBinaryValue) initialValue.atomize();
                    return DatatypeConverter.printHexBinary(hexValue.getBinaryValue());
                }
            }
        }
        return null;
    }
    
    private Object castNodeValue(Item initialValue, ItemType initialValueType) throws XPathException {
        NodeInfo nodeValue = (NodeInfo) initialValue;
        XdmNode node = new XdmNode(nodeValue);
        if (NodeKindTest.ELEMENT.matchesNode(nodeValue)                 ||
            NodeKindTest.COMMENT.matchesNode(nodeValue)                 ||
            NodeKindTest.PROCESSING_INSTRUCTION.matchesNode(nodeValue)) {
            // Basic string serialization
            return node.toString();
        }
        if (NodeKindTest.TEXT.matchesNode(nodeValue)) {
            return new StringHandle(node.toString()).withFormat(Format.TEXT);
        }
        if (NodeKindTest.DOCUMENT.matchesNode(nodeValue)) {
            try {
                // FIXME: Can't seem to successfully pipe a Saxon DOM Object to the DOMHandle -> NPE
//                DocumentOverNodeInfo docWrapper = (DocumentOverNodeInfo) NodeOverNodeInfo.wrap(nodeValue);
//                return new DOMHandle(docWrapper.getOwnerDocument());         
                // Ugly fix, not optimized
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(IOUtils.toInputStream(node.toString(),"UTF-8"));
                return new DOMHandle(doc);
            }
            catch (ParserConfigurationException | IOException | SAXException ex) {
                throw new XPathException("Variable value cannot be converted to a DOM Document object.");
            }
        }
        return null;
    }
    
}
