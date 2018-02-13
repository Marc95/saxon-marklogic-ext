# saxon-marklogic-ext


Extension functions for Saxon allowing XQueries to be sent to MarkLogic Server.


The extensions may be registered:
* in a Java program, by calling Saxon method <tt>registerExtensionFunction()</tt> and supplying the extension classes - for instance: <tt>configuration.registerExtensionFunction(new MarkLogicQuery());</tt>
* through a Saxon Configuration file (<a href=http://www.saxonica.com/documentation9.7/index.html#!configuration/configuration-file>Saxon documentation</a>).


They are also <a href=https://github.com/cmarchand/gaulois-pipe>gaulois-pipe</a> services. The jar just has to be in the classpath for the functions to be used with gaulois-pipe.


## Usage


### Send the XQuery as a string

Java class: <tt>fr.askjadev.xml.extfunctions.marklogic.MarkLogicQuery</tt>

<pre>declare namespace mkl-ext = 'fr:askjadev:xml:extfunctions';
mkl-ext:marklogic-query(
  "for $i in 1 to 10 return&lt;test&gt;{$i}&lt;/test&gt;",
  map{
    "server":"localhost",
    "port":8004,
    "user":"admin",
    "password":"admin"
  }
);</pre>


### Invoke an XQuery module already deployed on MarkLogic Server

Java class: <tt>fr.askjadev.xml.extfunctions.marklogic.MarkLogicQueryInvoke</tt>

<pre>declare namespace mkl-ext = 'fr:askjadev:xml:extfunctions';
mkl-ext:marklogic-query-invoke(
  "/path/to/module.xqy",
  map{
    "server":"localhost",
    "port":8004,
    "user":"admin",
    "password":"admin"
  }
);</pre>


### Read an XQuery file from an URI and run it

Java class: <tt>fr.askjadev.xml.extfunctions.marklogic.MarkLogicQueryInvoke</tt>

<pre>declare namespace mkl-ext = 'fr:askjadev:xml:extfunctions';
mkl-ext:marklogic-query-invoke(
  "file:/path/to/file.xqy",
  map{
    "server":"localhost",
    "port":8004,
    "user":"admin",
    "password":"admin"
  }
);</pre>


### Additional information

The second parameter is an XPath 3.0 map containing the server and database configuration.

The options "server" (xs:string), "port" (xs:integer), "user" (xs:string) and "password" (xs:string) are mandatory.

You can supply 2 additional options:

- <tt>"database"</tt> (xs:string) : alternative database name, if not using the one associated with the HTTP server.
- <tt>"authentication"</tt> (xs:string) : authentication method. Authorized values: "digest", "basic" (default).

/!\ The query must return a valid XML document (or a sequence of XML documents). If you need to return an atomic value, wrap it in a dummy XML element.


## Current version: 1.0.4

Maven support:

<pre>
&lt;dependency&gt;
  &lt;groupId&gt;fr.askjadev.xml.extfunctions&lt;/groupId&gt;
  &lt;artifactId&gt;marklogic&lt;/artifactId&gt;
  &lt;version&gt;1.0.4&lt;/version&gt;
&lt;/dependency&gt;
</pre>


## Build

To build the project from the sources, follow these steps:

<pre>
$ git clone https://github.com/AxelCourt/saxon-marklogic-ext.git
$ cd saxon-marklogic-ext
$ mvn clean package -DskipTests=true
</pre>

Please note that you need to deactivate the tests using the parameter `-DskipTests=true` to be able to build the project, unless you have correctly configured your MarkLogic Server environment.


## Testing

The tests require a running MarkLogic Server instance. By default, they are run under the following MarkLogic Server configuration:

* MarkLogic Server runs on `localhost`.
* There is a `Test` database associated with a HTTP Server on port `8004`.
* Username/password are `admin`/`admin`.
* User needs to be rest-admin in MarkLogic.
* The HTTP Server authentication scheme is `basic`.

If you wish to change this behaviour, you can add additional parameters to the test command-line which values will be used instead of the default ones.

|Parameter|Default values|Usage|Description|
|----|----|----|----|
|testServer|localhost|`-DtestServer=10.11.12.90`|The server on which to run the tests.|
|testPort|8004|`-DtestPort=8999`|The port to use to talk to the HTTP Server.|
|testUser|admin|`-DtestUser=myUser`|An authorised user.|
|testPassword|admin|`-DtestPassword=myPassword`|The user password.|
|testDatabase|Test|`-DtestDatabase=myDb`|The HTTP Server default database name.|
|testAuthentication|basic|`-DtestAuthentication=digest`|The HTTP Server authentication scheme.<br>Authorized values: `basic` or `digest`.|


## Thanks

Many thanks to Christophe Marchand for the base code, and Emmanuel Tourdot for the improvements!

Go there for a BaseX similar extension function: <a href="https://github.com/cmarchand/xpath-basex-ext">https://github.com/cmarchand/xpath-basex-ext</a>.
