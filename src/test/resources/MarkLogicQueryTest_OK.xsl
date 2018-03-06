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
    
    <xsl:variable name="element" as="element()">
      <element att="val">text</element>
    </xsl:variable>

    <xsl:variable name="text" as="text()">
      <xsl:text>Text node</xsl:text>
    </xsl:variable>
    
    <xsl:variable name="comment" as="comment()">
      <xsl:comment> Mon commentaire </xsl:comment>
    </xsl:variable>
    
    <xsl:variable name="pi" as="processing-instruction()">
      <xsl:processing-instruction name="pi">valeur</xsl:processing-instruction>
    </xsl:variable>
    
    <xsl:variable name="doc" as="document-node()">
      <xsl:document>
        <xsl:sequence select="$element"/>
      </xsl:document>
    </xsl:variable>
    
    <xsl:variable name="extVar"
                  as="map(xs:QName,item()*)"
                  select="map{
                    QName('http://namespace','pre:string')    : 'string value',
                    QName('http://namespace','pre:int')       : 12,
                    QName('http://namespace','pre:dateTime')  : current-dateTime(),
                    QName('http://namespace','pre:QName')     : QName('http://ns','toto:titi'),
                    QName('http://namespace','pre:element')   : $element,
                    QName('http://namespace','pre:text')      : $text,
                    QName('http://namespace','pre:comment')   : $comment,
                    QName('http://namespace','pre:pi')        : $pi,
                    QName('http://namespace','pre:doc')       : $doc
                  }"/><!-- QName('http://namespace','pre:emptySeq')  : () -->
    
    <xsl:sequence select="mkl-ext:marklogic-query($query, $config, $extVar)"/>
  </xsl:template>
  
</xsl:stylesheet>