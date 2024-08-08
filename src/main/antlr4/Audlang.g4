/*
 * Audlang.g4 - Audience Definition Language ANTLR-Grammar
 * 
 * Author: Karl Eilebrecht (Karl.Eilebrecht(a/t)calamanari.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"):
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Online lab: http://lab.antlr.org/ */

grammar Audlang;


fragment A              : ('A'|'a') ;
fragment B              : ('B'|'b') ;
fragment C              : ('C'|'c') ;
fragment D              : ('D'|'d') ;
fragment E              : ('E'|'e') ;
fragment F              : ('F'|'f') ;
fragment I              : ('I'|'i') ;
fragment K              : ('K'|'k') ;
fragment L              : ('L'|'l') ;
fragment N              : ('N'|'n') ;
fragment O              : ('O'|'o') ;
fragment R              : ('R'|'r') ;
fragment S              : ('S'|'s') ;
fragment T              : ('T'|'t') ;
fragment U              : ('U'|'u') ;
fragment W              : ('W'|'w') ;
fragment Y              : ('Y'|'y') ;

fragment DOUBLE_QUOTE   : ('"') ;

WHITESPACE              : (' ' | '\t' | '\r' | '\n')+ ;

AND                     : A N D ;

OR                      : O R ;

STRICT                  : S T R I C T ;

NOT                     : N O T ;

IS                      : I S ;

ANY                     : A N Y ;

OF                      : O F ;

BETWEEN                 : B E T W E E N ;

CONTAINS                : C O N T A I N S ;

CURB                    : C U R B ;

UNKNOWN                 : U N K N O W N ;

INTEGER_GTE_0           : '0' | ([123456789] [0123456789]*) ;

/* 
   Within plain text (argument names and values):
   - Don't allow the standard invisible characters 0-31.
   - Don't allow DEL (7F) 
   - Don't allow any characters interfering with the grammar 
*/
TEXT_PLAIN              : ((~('\u0000'..'\u001F' | [\u007F ()<>=,!@/"*]))* (~('\u0000'..'\u001F' | [\u007F ()<>=0123456789,!@/"*])) (~('\u0000'..'\u001F' | [\u007F ()<>=,!/"*]))*) ;

/* 
    Within double-quoted text (argument names and values):
    - Don't allow the standard invisible characters 0-31, and DEL (7F), as well as the double-quote character '"'.
    - Allow the double quote character escaped by itself ('""').
    - Note: 
      The removal of surrounding double quotes, and the "unescaping" of any inner double-quotes '""'->'"'
      as well as special handling of any further escape sequences (e.g., for line-breaks or tabulator) is NOT part of the ANTLR4 grammar. 
      By intention we leave that to subsequent application code to reduce grammar complexity.
*/
TEXT_IN_DOUBLE_QUOTES   : DOUBLE_QUOTE (~('\u0000'..'\u001F' | [\u007F"]) | (DOUBLE_QUOTE DOUBLE_QUOTE))+ DOUBLE_QUOTE ;

TEXT_EMPTY              : DOUBLE_QUOTE DOUBLE_QUOTE ;

MATCH_ALL               : '<' A L L '>' ;

MATCH_NONE              : '<' N O N E '>' ;

/*
   It is more practical to keep the comment intact rather than extracting its content.
   In most scenarios the comments will be discarded anyway (not subject to any evaluation), 
   and this way we can keep the parser rules simple (see below).
 */
COMMENT                 : '/' '*' .*? '*' '/' ;


/*
  Parser rules
*/

comment                 : COMMENT ;

space                   : WHITESPACE | (WHITESPACE? comment WHITESPACE?)+ ;

argName                 : TEXT_PLAIN | TEXT_IN_DOUBLE_QUOTES | INTEGER_GTE_0 ;

argValue                : TEXT_PLAIN | TEXT_IN_DOUBLE_QUOTES | TEXT_EMPTY | INTEGER_GTE_0 ;

snippet                 : argValue;

argRef                  : '@' argName;

argValueOrArgRef        : argValue | argRef ;

cmpEquals               : '=' space? argValueOrArgRef ;

cmpNotEquals            : '!' '=' space? argValueOrArgRef ;

cmpStrictNotEquals      : cmpNotEquals ;

cmpLessThan             : '<' space? argValueOrArgRef ;

cmpLessThanOrEquals     : '<' '=' space? argValueOrArgRef ;

cmpGreaterThan          : '>' space? argValueOrArgRef ;

cmpGreaterThanOrEquals  : '>' '=' space? argValueOrArgRef ;

cmpIsUnknown            : IS space UNKNOWN;

cmpIsNotUnknown         : IS space NOT space UNKNOWN;

cmpAnyOf                : ANY space OF space? '(' space? argValueOrArgRef ( space? ',' space? argValueOrArgRef)* space? ')' ;

cmpBetween              : BETWEEN space? '(' argValue space? ',' space? argValue space? ')' ;

cmpContains             : CONTAINS space snippet ;

cmpContainsAnyOf        : CONTAINS space ANY space OF space? '(' space? snippet ( space? ',' space? snippet)* space? ')' ;

cmpInnerNot             : NOT space (cmpAnyOf | cmpBetween | cmpContains | cmpContainsAnyOf) ;

cmpStrictInnerNot       : STRICT space cmpInnerNot;

curbBound               : INTEGER_GTE_0 ;

curbEquals              : '=' space? curbBound ;

curbNotEquals           : '!' '=' space? curbBound ;

curbLessThan            : '<' space? curbBound ;

curbLessThanOrEquals    : '<' '=' space? curbBound ;

curbGreaterThan         : '>' space? curbBound ;

curbGreaterThanOrEquals : '>' '=' space? curbBound ;

allExpression           : MATCH_ALL;

noneExpression          : MATCH_NONE;

anyExpression           : ( cmpExpression | bracedExpression | curbExpression | andExpression | orExpression | notExpression | strictNotExpression | allExpression | noneExpression ) ; 

andExpression           : monoExpression space AND space monoExpression ( space AND space monoExpression )* ;

orExpression            : monoExpression space OR space monoExpression ( space OR space monoExpression )* ;

curbExpression          : CURB space? '(' space? orExpression space? ')' space? (curbEquals | curbNotEquals | curbLessThan | curbLessThanOrEquals | curbGreaterThan | curbGreaterThanOrEquals) ;

bracedExpression        : '(' space? anyExpression space? ')' ;

monoExpression          : ( cmpExpression | bracedExpression | curbExpression | notExpression | strictNotExpression | allExpression | noneExpression ) ;

notExpression           : NOT space monoExpression ;

strictNotExpression     : STRICT space notExpression ;

cmpExpression           : cmpExpressionPlain | ( '(' space? cmpExpressionPlain space? ')' ) ;

cmpExpressionPlain      : (argName space ( cmpIsUnknown | cmpIsNotUnknown | cmpAnyOf | cmpBetween | cmpContains | cmpContainsAnyOf | cmpInnerNot | cmpStrictInnerNot)) | (argName space? ( cmpEquals | cmpNotEquals | cmpLessThan | cmpLessThanOrEquals | cmpGreaterThan | cmpGreaterThanOrEquals)) | (STRICT space argName space? cmpStrictNotEquals) ;

/* start rule */
query                   : space? anyExpression space? EOF ;