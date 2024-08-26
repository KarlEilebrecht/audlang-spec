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

/* 
   Important: The alias rules for "space" below are solely required to properly detect comment positions.
   If you are not interested in parsing comments, then you can safely replace all occurrences with the "space" rule
   and this way slightly improve the performance of the generated ANTLR-parser.
*/

spaceAfterNot           : space;

spaceAfterStrict        : space;

spaceAfterContains      : space;

spaceAfterBetween       : space;

spaceAfterIs            : space;

spaceAfterAny           : space;

spaceAfterOf            : space;

spaceAfterArgName       : space;

spaceAfterOperator      : space;

spaceBeforeListItem     : space;

spaceAfterListItem      : space;

spaceBeforeCombiner     : space;

spaceAfterCombiner      : space;

spaceAfterCurb          : space;

spaceAfterCurbedOr      : space;

spaceBeforeExpression   : space;

spaceAfterExpression    : space;

argName                 : TEXT_PLAIN | TEXT_IN_DOUBLE_QUOTES | INTEGER_GTE_0 ;

argValue                : TEXT_PLAIN | TEXT_IN_DOUBLE_QUOTES | TEXT_EMPTY | INTEGER_GTE_0 ;

snippet                 : argValue;

argRef                  : '@' argName;

argValueOrArgRef        : argValue | argRef ;

valueListItem           : spaceBeforeListItem? argValue spaceAfterListItem? ;

valueOrRefListItem      : spaceBeforeListItem? argValueOrArgRef spaceAfterListItem? ;

snippetListItem         : spaceBeforeListItem? snippet spaceAfterListItem? ;

cmpEquals               : '=' spaceAfterOperator? argValueOrArgRef ;

cmpNotEquals            : '!' '=' spaceAfterOperator? argValueOrArgRef ;

cmpStrictNotEquals      : cmpNotEquals ;

cmpLessThan             : '<' spaceAfterOperator? argValueOrArgRef ;

cmpLessThanOrEquals     : '<' '=' spaceAfterOperator? argValueOrArgRef ;

cmpGreaterThan          : '>' spaceAfterOperator? argValueOrArgRef ;

cmpGreaterThanOrEquals  : '>' '=' spaceAfterOperator? argValueOrArgRef ;

cmpIsUnknown            : IS spaceAfterIs UNKNOWN;

cmpIsNotUnknown         : IS spaceAfterIs NOT spaceAfterNot UNKNOWN;

cmpAnyOf                : ANY spaceAfterAny OF spaceAfterOf? '(' valueOrRefListItem ( ',' valueOrRefListItem )* ')' ;

cmpBetween              : BETWEEN spaceAfterBetween? '(' valueListItem ',' valueListItem ')' ;

cmpContains             : CONTAINS spaceAfterContains snippet ;

cmpContainsAnyOf        : CONTAINS spaceAfterContains ANY spaceAfterAny OF spaceAfterOf? '(' snippetListItem ( ',' snippetListItem )* ')' ;

cmpInnerNot             : NOT spaceAfterNot (cmpAnyOf | cmpBetween | cmpContains | cmpContainsAnyOf) ;

cmpStrictInnerNot       : STRICT spaceAfterStrict cmpInnerNot;

curbBound               : INTEGER_GTE_0 ;

curbEquals              : '=' spaceAfterOperator? curbBound ;

curbNotEquals           : '!' '=' spaceAfterOperator? curbBound ;

curbLessThan            : '<' spaceAfterOperator? curbBound ;

curbLessThanOrEquals    : '<' '=' spaceAfterOperator? curbBound ;

curbGreaterThan         : '>' spaceAfterOperator? curbBound ;

curbGreaterThanOrEquals : '>' '=' spaceAfterOperator? curbBound ;

allExpression           : MATCH_ALL;

noneExpression          : MATCH_NONE;

anyExpression           : ( cmpExpression | bracedExpression | curbExpression | andExpression | orExpression | notExpression | strictNotExpression | allExpression | noneExpression ) ; 

andExpression           : monoExpression spaceBeforeCombiner AND spaceAfterCombiner monoExpression ( spaceBeforeCombiner AND spaceAfterCombiner monoExpression )* ;

orExpression            : monoExpression spaceBeforeCombiner OR spaceAfterCombiner monoExpression ( spaceBeforeCombiner OR spaceAfterCombiner monoExpression )* ;

curbExpression          : CURB spaceAfterCurb? '(' spaceBeforeExpression? orExpression spaceAfterExpression? ')' spaceAfterCurbedOr? (curbEquals | curbNotEquals | curbLessThan | curbLessThanOrEquals | curbGreaterThan | curbGreaterThanOrEquals) ;

bracedExpression        : '(' spaceBeforeExpression? anyExpression spaceAfterExpression? ')' ;

monoExpression          : ( cmpExpression | bracedExpression | curbExpression | notExpression | strictNotExpression | allExpression | noneExpression ) ;

notExpression           : NOT spaceAfterNot monoExpression ;

strictNotExpression     : STRICT spaceAfterStrict notExpression ;

cmpExpression           : cmpExpressionPlain | ( '(' spaceBeforeExpression? cmpExpressionPlain spaceAfterExpression? ')' ) ;

cmpExpressionPlain      : (argName spaceAfterArgName ( cmpIsUnknown | cmpIsNotUnknown | cmpAnyOf | cmpBetween | cmpContains | cmpContainsAnyOf | cmpInnerNot | cmpStrictInnerNot)) | (argName spaceAfterArgName? ( cmpEquals | cmpNotEquals | cmpLessThan | cmpLessThanOrEquals | cmpGreaterThan | cmpGreaterThanOrEquals)) | (STRICT spaceAfterStrict argName spaceAfterArgName? cmpStrictNotEquals) ;

/* start rule */
query                   : spaceBeforeExpression? anyExpression spaceAfterExpression? EOF ;
