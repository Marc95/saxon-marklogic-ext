<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:mkl-ext="fr:askjadev:xml:extfunctions"
                xmlns:saxon="http://saxon.sf.net/"
                exclude-result-prefixes="#all"
                version="3.0">
  
  <xsl:variable name="element" as="element()">
    <element att="val">text</element>
  </xsl:variable>
  
  <xsl:variable name="attribute" as="attribute()">
    <xsl:attribute name="att">value</xsl:attribute>
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
                  QName('http://namespace','pre:boolean')   : true(),
                  QName('http://namespace','pre:int')       : 100,
                  QName('http://namespace','pre:double')    : number(100.1),
                  QName('http://namespace','pre:float')     : xs:float(100.2),
                  QName('http://namespace','pre:decimal')   : '100.3',
                  QName('http://namespace','pre:anyURI')    : xs:anyURI('http://any/uri'),
                  QName('http://namespace','pre:dateTime')  : current-dateTime(),
                  QName('http://namespace','pre:QName')     : QName('http://ns','toto:titi'),
                  QName('http://namespace','pre:base64')    : xs:base64Binary('dG90bw=='),
                  QName('http://namespace','pre:hex')       : xs:hexBinary('746f746f'),
                  QName('http://namespace','pre:element')   : $element,
                  QName('http://namespace','pre:attribute') : $attribute,
                  QName('http://namespace','pre:text')      : $text,
                  QName('http://namespace','pre:comment')   : $comment,
                  QName('http://namespace','pre:pi')        : $pi,
                  QName('http://namespace','pre:doc')       : $doc,
                  QName('http://namespace','pre:map')       : map{'toto':'titi','tata':$element},
                  QName('http://namespace','pre:array')     : [1,2,'toto',['titi',3]],
                  QName('http://namespace','pre:empty')     : ()
                }"/>

</xsl:stylesheet>