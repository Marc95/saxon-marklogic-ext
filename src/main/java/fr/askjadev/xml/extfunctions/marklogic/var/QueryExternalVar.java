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

import com.marklogic.client.eval.ServerEvaluationCall;
import fr.askjadev.xml.extfunctions.marklogic.AbstractMLExtensionFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.saxon.om.Item;

/**
 * Utility class QueryExternalVar / External variable that must be provided to the XQuery
 * @author ext-acourt
 */
public final class QueryExternalVar {
    
    private String namespace;
    private String prefix;
    private String name;
    private String qualifiedName;
    private Object value;
    private Item saxonValue;

    public QueryExternalVar() { }
    
    public QueryExternalVar(String namespace, String prefix, String name, String qualifiedName) {
        this.namespace = namespace;
        this.prefix = prefix;
        this.name = name;
        this.qualifiedName = qualifiedName;
    }

    public QueryExternalVar(String namespace, String prefix, String name) {
        this.namespace = namespace;
        this.prefix = prefix;
        this.name = name;
        setQualifiedName();
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
    
    public void setQualifiedName() {
        this.qualifiedName = getPrefix() + ":" + getName();
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setSaxonValue(Item saxonValue) {
        this.saxonValue = saxonValue;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPrefix() {
        return prefix;
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
    
    public String getQualifiedName() {
        return qualifiedName;
    }

    public Item getSaxonValue() {
        return saxonValue;
    }
    
    public ServerEvaluationCall addToCall(ServerEvaluationCall call) {
        Logger.getLogger(AbstractMLExtensionFunction.class.getName()).log(Level.INFO, qualifiedName);
        // Register the NS
        call.addNamespace(prefix, namespace);
        // Add the variable to the call
        if (value instanceof Integer)       { call.addVariable(qualifiedName, (Number) value); }
        else if (value instanceof Boolean)  { call.addVariable(qualifiedName, (Boolean) value); }
        else if (value instanceof String)   { call.addVariable(qualifiedName, (String) value); }
        else                                { call.addVariableAs(qualifiedName, value); }
        return call;
    }
    
}
