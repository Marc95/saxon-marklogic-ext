xquery version "1.0-ml";

declare namespace pre="http://namespace";

declare variable $pre:string as xs:string external;
declare variable $pre:boolean as xs:boolean external;
declare variable $pre:int as xs:integer external;
declare variable $pre:double as xs:double external;
declare variable $pre:float as xs:float external;
declare variable $pre:decimal as xs:decimal external;
declare variable $pre:anyURI as xs:anyURI external;
declare variable $pre:dateTime as xs:dateTime external;
declare variable $pre:QName as xs:QName external;
declare variable $pre:base64 as xs:base64Binary external;
declare variable $pre:hex as xs:hexBinary external;
declare variable $pre:element as element() external;
declare variable $pre:attribute as document-node() external;
declare variable $pre:text as text() external;
declare variable $pre:comment as document-node() external;
declare variable $pre:pi as document-node() external;
declare variable $pre:doc as document-node() external;
declare variable $pre:map as map:map external;
declare variable $pre:array as json:array external;
declare variable $pre:empty as xs:anyURI? external;

(: Can't get an attribute back as a query result :)
let $pre:attribute  := $pre:attribute/*/attribute()
let $pre:comment    := $pre:comment/comment()
let $pre:pi         := $pre:pi/processing-instruction()
let $pre:empty      := $pre:element/null-node()

return (
  $pre:string, $pre:boolean, $pre:int, $pre:double, $pre:float, $pre:decimal,
  $pre:anyURI, $pre:dateTime, $pre:QName, $pre:base64, $pre:hex,
  $pre:element, $pre:text, $pre:comment, $pre:pi, $pre:doc,
  $pre:map, $pre:array, $pre:empty
)