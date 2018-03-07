<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:mkl-ext="fr:askjadev:xml:extfunctions"
                exclude-result-prefixes="#all"
                version="3.0">
  
  <xsl:param name="config" as="map(xs:string,item())" required="yes"/>
  
  <xsl:template match="/">
    <xsl:variable name="query" select="'for $i in 1 to 10 return &lt;test&gt;{$i}&lt;/test&gt;'" as="xs:string"/>
    <xsl:sequence select="mkl-ext:marklogic-query($query, $config)"/>
  </xsl:template>
  
</xsl:stylesheet>