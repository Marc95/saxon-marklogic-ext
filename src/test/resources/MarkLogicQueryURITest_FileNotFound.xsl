<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:mkl-ext="fr:askjadev:xml:extfunctions"
                exclude-result-prefixes="#all"
                version="3.0">
  
  <xsl:param name="config" as="map(xs:string,item())" required="yes"/>
  
  <xsl:template match="/">
    <xsl:variable name="queryRelativeUri" select="'MarkLogicQueryURITest_dummy.xqy'" as="xs:string"/>
    <!-- File not found -->
    <xsl:sequence select="mkl-ext:marklogic-query-uri($queryRelativeUri, $config)"/>
  </xsl:template>
  
</xsl:stylesheet>