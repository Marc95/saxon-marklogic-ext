<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:mkl-ext="fr:askjadev:xml:extfunctions"
                exclude-result-prefixes="#all"
                version="3.0">
  
  <xsl:import href="MarkLogicQuery_ExternalVariables.xsl"/>
  
  <xsl:param name="config" as="map(xs:string,item())" required="yes"/>
  
  <xsl:variable name="query" select="unparsed-text('MarkLogicQuery_ExternalVariables.xqy')" as="xs:string"/>
  
  <xsl:template match="/">
    
    <xsl:variable name="result"
                  as="item()+"
                  select="mkl-ext:marklogic-query($query, $config, $extVar)"/>
    
    <external-variables count="{count($result)}">
      <external-variable name="pre:string" as="xs:string" isTypeAsExpected="{if ($result[1] instance of xs:string) then true() else false()}"><xsl:sequence select="$result[1]"/></external-variable>
      <external-variable name="pre:boolean" as="xs:boolean" isTypeAsExpected="{if ($result[2] instance of xs:boolean) then true() else false()}"><xsl:sequence select="$result[2]"/></external-variable>
      <external-variable name="pre:int" as="xs:integer" isTypeAsExpected="{if ($result[3] instance of xs:integer) then true() else false()}"><xsl:sequence select="$result[3]"/></external-variable>
      <external-variable name="pre:double" as="xs:double" isTypeAsExpected="{if ($result[4] instance of xs:double) then true() else false()}"><xsl:sequence select="$result[4]"/></external-variable>
      <external-variable name="pre:float" as="xs:float" isTypeAsExpected="{if ($result[5] instance of xs:float) then true() else false()}"><xsl:sequence select="$result[5]"/></external-variable>
      <external-variable name="pre:decimal" as="xs:decimal" isTypeAsExpected="{if ($result[6] instance of xs:decimal) then true() else false()}"><xsl:sequence select="$result[6]"/></external-variable>
      <external-variable name="pre:anyURI" as="xs:anyURI" isTypeAsExpected="{if ($result[7] instance of xs:anyURI) then true() else false()}"><xsl:sequence select="$result[7]"/></external-variable>
      <external-variable name="pre:dateTime" as="xs:dateTime" isTypeAsExpected="{if ($result[8] instance of xs:dateTime) then true() else false()}"><xsl:sequence select="$result[8]"/></external-variable>
      <external-variable name="pre:QName" as="xs:QName" isTypeAsExpected="{if ($result[9] instance of xs:QName) then true() else false()}"><xsl:sequence select="$result[9]"/></external-variable>
      <external-variable name="pre:base64" as="xs:base64Binary" isTypeAsExpected="{if ($result[10] instance of xs:base64Binary) then true() else false()}"><xsl:sequence select="$result[10]"/></external-variable>
      <external-variable name="pre:hex" as="xs:hexBinary" isTypeAsExpected="{if ($result[11] instance of xs:hexBinary) then true() else false()}"><xsl:sequence select="$result[11]"/></external-variable>
      <external-variable name="pre:element" as="element()" isTypeAsExpected="{if ($result[12] instance of element()) then true() else false()}"><xsl:sequence select="$result[12]"/></external-variable>
      <external-variable name="pre:text" as="text()" isTypeAsExpected="{if ($result[13] instance of text()) then true() else false()}"><xsl:sequence select="$result[13]"/></external-variable>
      <external-variable name="pre:comment" as="comment()" isTypeAsExpected="{if ($result[14] instance of comment()) then true() else false()}"><xsl:sequence select="$result[14]"/></external-variable>
      <external-variable name="pre:pi" as="processing-instruction()" isTypeAsExpected="{if ($result[15] instance of processing-instruction()) then true() else false()}"><xsl:sequence select="$result[15]"/></external-variable>
      <external-variable name="pre:doc" as="document-node()" isTypeAsExpected="{if ($result[16] instance of document-node()) then true() else false()}"><xsl:sequence select="$result[16]/*"/></external-variable>
      <external-variable name="pre:map" as="map(*)" isTypeAsExpected="{if ($result[17] instance of map(*)) then true() else false()}"><xsl:sequence select="serialize($result[17],map{'method':'json'})"/></external-variable>
      <external-variable name="pre:array" as="array(*)" isTypeAsExpected="{if ($result[18] instance of array(*)) then true() else false()}"><xsl:sequence select="serialize($result[18],map{'method':'json'})"/></external-variable>
      <external-variable name="pre:empty" as="empty-sequence()" isTypeAsExpected="{if ($result[19] instance of empty-sequence()) then true() else false()}"><xsl:sequence select="$result[19]"/></external-variable>
    </external-variables>
  
  </xsl:template>
  
</xsl:stylesheet>