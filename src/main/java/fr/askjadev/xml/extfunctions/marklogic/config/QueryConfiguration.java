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
package fr.askjadev.xml.extfunctions.marklogic.config;

/**
 * Utility class QueryConfiguration / Main configuration
 * @author Axel Court
 */
public class QueryConfiguration {
    
    public String server;
    public String user;
    public String password;
    public String database;
    public Integer port;
    public String authentication;

    public QueryConfiguration() {
        this.server = null;
        this.user = null;
        this.password = null;
        this.database = null;
        this.port = null;
        this.authentication = "basic";
    }
    
    public void set(String name, String value) {
        switch (name) {
            case "server":
                setServer(value);
                break;
            case "user":
                setUser(value);
                break;                
            case "password":
                setPassword(value);
                break;
            case "database":
                setDatabase(value);
                break;
            case "authentication":
                setAuthentication(value);
                break;
        }
    }
    
    public void set(String name, Integer value) {
        switch (name) {
            case "port":
                setPort(value);
                break;
        }
    }
    
    public void set(String name, Boolean value) {
        switch (name) {
            // No boolean configuration parameter at this point
        }
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }
    
    public String getServer() {
        return server;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public Integer getPort() {
        return port;
    }

    public String getAuthentication() {
        return authentication;
    }
    
}
