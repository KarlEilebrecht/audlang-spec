#### [Project Overview](../README.md)
----

# Audience Definition Language Specification

***Version 1.1*** *([August 2024](#document-history))*

The Audience Definition Language (Audlang) is a common expression language for defining audiences based on criteria (attributes and their values) *independent* from any concrete storage layer or data model (see also [:information_source: **Main Goals**](#main-goals)).

This specification is meant for implementers and users of the language. The document is organized in chapters covering the different language features. To keep the core specification concise, side notes (motivation, explanation) have been moved into the appendix.

Audlang is defined as an [ANTLR](https://www.antlr.org/)-grammar ([Audlang.g4](../src/main/antlr4/Audlang.g4)) with additional definitions and conventions explained in this specification.

Throughout this document we will use the following notation:

* **Audlang** will be used as an abbreviation for *Audience Definition Language*.
* `argName` will be used to represent any **argument name** (attribute), e.g., "country" ([§1.2.1](#121-argument-names)).
* `argValue` will be used to represent any **argument value**, e.g., "Denmark" ([§1.2.2](#122-argument-values)).
* `snippet` stands for any piece of text ([§1.2.4](#124-snippets)).
* `expr` stands for any other Audlang-expression ([§3](#3-basic-expressions), [§4](#4-composite-expressions)).
* The term **base audience** refers to the entirety of available records in a system we can make selections on with certain criteria.
* The term **population** refers to virtual set of people in the real world a system's base audience is meant to reflect.
* **:bulb: Hint** Advice or clue in the current context.
* **:warning: Warning** Information about potential problems.
* :information_source: **Notes** cover common explanation and comments. 
* :twisted_rightwards_arrows: **Implementor Notes** provide additional information primarily of interest for language implementors.

## Table of Contents

- [§1 General Definitions](#1-general-definitions)
- [§2 Type Conventions](#2-type-conventions)
- [§3 Basic Expressions](#3-basic-expressions)
     - [§3.1 Equals](#31-equals)
     - [§3.2 Not Equals](#32-not-equals)
     - [§3.3 Less Than and Greater Than](#33-less-than-and-greater-than)
     - [§3.4 Between](#34-between)
     - [§3.5 Any of](#35-any-of)
     - [§3.6 Contains text snippet](#36-contains-text-snippet)
     - [§3.7 Contains Any Of text snippet list](#37-contains-any-of-text-snippet-list)
     - [§3.8 Is Not Unknown](#38-is-not-unknown)
     - [§3.9 All and None](#39-all-and-none)
- [§4 Composite Expressions](#4-composite-expressions)
     - [§4.1 Logical And](#41-logical-and)
     - [§4.2 Logical Or](#42-logical-or)
     - [§4.3 Curbed Or](#43-curbed-or)
- [§5 Negation](#5-negation)
     - [§5.1 Default Negation](#51-default-negation)
     - [§5.2 Strict Negation](#52-strict-negation)
- [§6 Reference Value Matching](#6-reference-value-matching)
- [§7 Audlang and Collection Attributes](#7-audlang-and-collection-attributes)
- [Appendix](#appendix)
    - [Common Notes](#common-notes)
    - [Implementor Notes](#implementor-notes)
- [Document History](#document-history)


## §1 General Definitions

 * An **Expression** is a single Audlang-instruction or a composite of Audlang-instructions that forms a filter to select an audience from the base audience of a system.
 * Expressions can be composed of any other expressions with **unlimited nesting**. 
 * Audlang **syntax** is **case-insensitive**.
 * Audlang is **type-agnostic**. On the language level all [argument values](#122-argument-values) are **strings** (see [§1.1](#11-strings)).
   * Audlang defines **type conventions** (see [§2](#2-type-conventions)) to encourage *best practice* for typed values.
 
### §1.1 Strings

A string (argument names, values and snippets) is a potentially empty sequence of characters. Audlang distinguishes plain strings and double-quoted strings.
 * Strings require surrounding double quotes if they don't comply with the following rules:
   * The empty string is not a valid plain string (e.g., >< :x: vs. `""` :white_check_mark:).
   * First character must not be the `@`-symbol (e.g., `@home` :x: vs. `"@home"` :white_check_mark:).
   * Plain strings must not contain the space character 
     * e.g., `red wine` :x: vs. `"red wine"` :white_check_mark:
   * Plain strings must not contain any of following symbols: `(`, `)`, `<`, `>`, `=`, `,`, `!`, `/`, `"`, `*` 
     * e.g., `http://foo.com` :x: vs. `"http://foo.com"` :white_check_mark:
* Otherwise double-quotes surrounding a string are optional
   * e.g., `green` :white_check_mark: vs. `"green"` :white_check_mark:
 * To represent a double-quote character inside a double-quoted string, it must be doubled 
   * e.g., `"<">"` :x: vs. `"<"">"` :white_check_mark:
   * **[:information_source: Common Notes](#about-plain-and-double-quoted-strings)**
   * **[:twisted_rightwards_arrows: Implementor Notes](#handling-double-quoted-strings)**
 * Audlang does not allow ASCII control characters in any strings. This affects any characters with codes `0..31` as well as `127`.
 * Audlang specifies the following escape sequences and rules to represent these characters anyway **inside double-quoted strings**:
   * Overview
     * `<NUL>`(0), `<SOH>`(1), `<STX>`(2), `<ETX>`(3), `<EOT>`(4), `<ENQ>`(5), `<ACK>`(6), `<BEL>`(7), `<BS>`(8)
     * `<HT>`(9) :bulb: **horitontal tab**
     * `<LF>`(10) :bulb: **line break**
     * `<VT>`(11), `<FF>`(12), `<CR>`(13), `<SO>`(14), `<SI>`(15), `<DLE>`(16), `<DC1>`(17), `<DC2>`(18), `<DC3>`(19), `<DC4>`(20), `<NAK>`(21), `<SYN>`(22), `<ETB>`(23), `<CAN>`(24), `<EM>`(25), `<SUB>`(26), `<ESC>`(27), `<FS>`(28), `<GS>`(29), `<RS>`(30), `<US>`(31), `<DEL>`(127);
   * Should any of the escape sequences above appear in plain text, then it can be escaped using the **backslash character** `\`.
     * If in a source text any of the escape sequences is already preceded by one or multiple backslash character(s) then these - 
     and only these - backslash character(s) must be doubled.
     * Here are a few examples:
       * `"a<HT>b"` represents a tabulator character between `a` and `b`.
       * `"a\<HT>b"` represents the text `a<HT>b`.
       * `"a\\<HT>b"` represents a tabulator character between `a\` and `b`.
       * `"a\\\<HT>b"` represents the text `a\<HT>b`.
   * **[:twisted_rightwards_arrows: Implementor Notes](#handling-escape-sequences)**

### §1.2 Names, values, and snippets

#### §1.2.1 Argument Names

An argument name (`argName`) is the name of any attribute of a system's base audience.
 * Argument names are technically **strings** (see [§1.1](#11-strings)).
 * Argument names **must not be empty**.
 * By default, argument names are **case-sensitive**.

**[:information_source: Common Notes](#no-empty-argument-names)**, **[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-argument-names)**

#### §1.2.2 Argument Values

An argument value (`argValue`) is the value of any attribute of a system's base audience.
 * Argument values are technically **strings** (see [§1.1](#11-strings)).
 * The empty string (`""`) is a valid argument value.
 * By default, argument values are **case-sensitive**.

**[:twisted_rightwards_arrows: Implementor Notes](inArgumentValues.md)**

#### §1.2.3 Argument Reference

Certain Audlang-features allow referencing another argument name (i.g., for comparison). An argument reference (`argRef`) is an `@`-symbol immediately followed by an `argName` (e.g., `@color`, `@"favorite color"`).

See also [§6 Reference Value Matching](#6-reference-value-matching)

#### §1.2.4 Snippets

A text snippet (`snippet`) is a short piece of text.
 * Snippets are technically **strings** (see [§1.1](#11-strings)).
 * The empty string (`""`) is a valid snippet.
 * By default, text snippets are **case-sensitive**.

### §1.3 Braces

Round braces **`(` `)`** are used in Audlang expressions to group a combination of expressions which would otherwise become ambiguous.
   * Unnecessary extra pairs of braces around any expression (e.g., `( expr )` or `( ( expr ) )`) will be ignored.

### §1.4 Whitespace

Whitespace (' ', tabulator, carriage-return, line-break) is allowed anywhere outside strings to format the structure of an Audlang-expression. Occasionally, whitespace is required (see for example around [AND](#41-logical-and) / [OR](#42-logical-or)).

### §1.5 Comments

Audlang allows placing comments before, inside and after an expression. Every comment section or commented piece of an expression starts with `/*` and ends with `*/`.

*Examples:* 
 * ```
   /* some comment */
   color = red
   ```
 * ```
   /* comment line 1
      comment line 2
   */
   color = red
   ```
 * ```
   /* comment line 1*/ /* comment 2 */
   color = red
   ```
 * ```
   color = /*green*/ red
   ```
 * ```
   color = red /* green */
   ```
* ```
   color = red 
   /* footer */
   ```

:bulb: Each of the expressions above is **logically identical** to `color = red`.

:warning: **Important Note:**

 * Comments are primarily meant for debugging and temporary use. They allow excluding and including parts of an expression without deleting the content.
 * Comments are **not part of the logical expression** and **not involved in the execution** of an expression in any way.
 
**[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-comments)**

## §2 Type Conventions

On language level all [argument values](#122-argument-values) are of type **string** (see [§1.1](#11-strings)).

As a norm for consistent type handling across implementations and to encourage best practice for writing expressions, Audlang defines a set of conventions.

### §2.1 Numbers (numeric values)

#### §2.1.1 Integer values

 * Range: $-2^{63}$ ($-9,223,372,036,854,775,808$) to $+2^{63}-1$ ($9,223,372,036,854,775,807$)
 * No grouping: `1,123,647` :x: , `1123647` :white_check_mark:
 * No leading zeros `051` :x: , `51` :white_check_mark:

*Examples:*
 * `children > 2`

**[:twisted_rightwards_arrows: Implementor Notes](#handling-integer-conversion)**

#### §2.1.2 Decimal values (floating-point)

 * Precision: IEEE 754 floating-point double format, 64 bits
 * Decimal separator: `.` **dot**
 * 7 decimal digits *max*: `3.12345678` :x: , `3.1234567` :white_check_mark:
 * No grouping: `1,123.458` :x: , `1123.458` :white_check_mark:
 * No leading zeros `05.1` :x: , `5.1` :white_check_mark:
 
*Examples:*
 * `priceUSD > 12.5`

**[:twisted_rightwards_arrows: Implementor Notes](#handling-decimal-conversion)**

#### §2.2 Logical Values

Audlang defines a single representation for logical values like *true/false, yes/no, etc.*):

 * **`0`** shall be used to represent *false, no, etc.*.
 * **`1`** shall be used to represent *true, yes, etc.*.

*Examples:*
 * `interested_in_sports = 1`

**[:information_source: Common Notes](#logical-value-convention)**

#### §2.3 Date Values

Audlang language defines the date format `yyyy-MM-dd` with 

 * `yyyy` as the year (4 digits, `0001`-`2199`)
 * `MM` as the month (2 digits, `01`-`12`)
 * `dd` as the day (2 digits, `01`-`31`)

**[:information_source: Common Notes](#about-date-values)**, **[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-date-values)**

*Examples:*
 * `last_contact > 2024-08-01`

## §3 Basic Expressions

### §3.1 Equals

`argName` **`=`** `argValue` matches if the value of the attribute "argName" is equal to the value "argValue".

*Examples:*
 * `color = blue`
 * `"favorite color" = green`
 * `"preferred drink" = "red wine"`

### §3.2 Not Equals

`argName` **`!=`** `argValue` matches if the value of the attribute "argName" is not equal to the value "argValue".

:bulb: `argName != argValue` is the shortform of `NOT argName = argValue`, see also [§5 Negation](#5-negation)

*Examples:*
 * `color != red`

### §3.3 Less Than and Greater Than

`argName` **`<`** `argValue` matches if the value of the attribute "argName" is less than the value "argValue".

*Examples:*
 * `children < 3`
 * `shirt_size < XL`

`argName` **`<=`** `argValue` matches if the value of the attribute "argName" is less than or equal to the value "argValue".

:bulb: `argName <= argValue` is the shortform of `argName < argValue OR argName = argValue`.

*Examples:*
 * `children <= 2`
 * `shirt_size <= L`

`argName` **`>`** `argValue` matches if the value of the attribute "argName" is greater than the value "argValue".

*Examples:*
 * `children > 1`
 * `shirt_size > XS`

`argName` **`>=`** `argValue` matches if the value of the attribute "argName" is greater than or equal to the value "argValue".

:bulb: `argName >= argValue` is the shortform of `argName > argValue OR argName = argValue`.

*Examples:*
 * `children <= 2`
 * `shirt_size <= L`

### §3.4 Between

`argName` **`BETWEEN`** `(argValue1, argValue2)` matches if the value of the attribute "argName" is greater than or equal to the value "argValue1" *and* less than or equal to the value "argValue2".

:bulb: `argName BETWEEN (argValue1, argValue2)` is the shortform of `argName >= argValue1 AND argName <= argValue2`.

*Examples:*
 * `children BETWEEN (2, 5)`
 * `shirt_size BETWEEN (L, XXL)`

### §3.5 Any of

`argName` **`ANY OF`** `(argValue1, argValue2, argValue3)` matches if the value of the attribute "argName" is equal to the value "argValue1" *or* "argValue2" or "argValue3". The list inside round braces must contain at least one element.

:bulb: `argName ANY OF (argValue1, argValue2)` is the shortform of `argName = argValue1 OR argName = argValue2`.

:bulb: `argName ANY OF (argValue)` is the same as `argName = argValue`.

*Examples:*
 * `color ANY OF (red, blue, green, silver, gold)`
 * `shirt_size ANY OF (L, XL, XXL)`

### §3.6 Contains text snippet

`argName` **`CONTAINS`** `snippet` matches if the **textual value** of the attribute "argName" contains the text `snippet`.

*Examples:*
 * `answer CONTAINS diving`
 * `answer CONTAINS "bull riding"`

### §3.7 Contains Any Of text snippet list

`argName` **`CONTAINS ANY OF`** `(snippet1, snippet2)` matches if the **textual value** of the attribute "argName" contains the text `snippet1` or `snippet2`. The list inside round braces must contain at least one element.

:bulb: `argName CONTAINS ANY OF (argValue1, argValue2)` is the shortform of `argName CONTAINS argValue1 OR argName CONTAINS argValue2`.

:bulb: `argName CONTAINS ANY OF (argValue)` is the same as `argName CONTAINS argValue`.

*Examples:*
 * `answer CONTAINS ANY OF (diving, riding)`
 * `answer CONTAINS ANY OF (diving, "bull riding")`

### §3.8 Is (Not) Unknown

`argName` **`IS UNKNOWN`** matches if the **value** of the attribute "argName" is **unavailable** for a given record.

Accordingly, `argName` **`IS NOT UNKNOWN`** matches all records which have *any value*.

:bulb: `argName IS NOT UNKNOWN` is the better readable form of `NOT argName IS UNKNOWN`.

**[:information_source: Common Notes](#about-unknown-values)**, **[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-unknown-values)**

*Examples:*
 * `pq127 IS UNKNOWN` means: *"Panel question 127 not answered."*
 * `pq127 IS NOT UNKNOWN` means: *"Panel question 127 has been answered."*

### §3.9 All and None

There are two **technically motivated expressions**:
 * `<ALL>` is a valid Audlang expression that stands for *matches all records in the base audience*.
 * `<NONE>` is a valid Audlang expression that stands for *matches none of the records in the base audience*.

These expressions are not meant to be entered by users. Instead, a concrete implementation may display them to inform a user that after validation and optimization a given expression ran into one of these extremes and is thus not worth executing.

**[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-all-and-none)**

## §4 Composite Expressions

### §4.1 Logical And

You can combine any two or more expressions with the `AND`-operator to define that *both* conditions must be fulfilled: `expr1` **`AND`** `expr2`.

:bulb: Braces around a group of expressions combined with `AND` are only required if the result shall be member of another composite expression.

**[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-nesting)**

*Examples:*
 * `color = red AND children > 2` 
 * `color is unknown AND brand != B17 AND favorite_food = pizza`
 * `(cars > 2 AND children > 1) OR job = freelancer`

### §4.2 Logical Or

You can combine any two or more expressions with the `OR`-operator to define that *at least one* of these conditions must be fulfilled: `expr1` **`OR`** `expr2`.

:bulb: Braces around a group of expressions combined with `OR` are only required if the result shall be member of another composite expression.

**[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-nesting)**

*Examples:*
 * `color != red OR children <= 2` 
 * `color is not unknown OR brand = B17 OR favorite_food != pizza`
 * `(cars <= 2 OR children < 2) AND job != freelancer`

### §4.3 Curbed Or

A `CURB` expression allows to define a **fulfilment limit** on a set of expressions (e.g., *"two of five"*). 

`CURB (expr1 OR expr2 OR expr3 ...) =|!=|<=|>=|<|> bound` counts the number of matching expressions inside the curb *for each record* to compare it against the given bound.

:bulb: Any Audlang expression (including composite expressions) can be member of a `CURB`-expression.

**[:information_source: Common Notes](#about-curbed-or)**, **[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-curbed-or)**

*Examples:*
* `CURB (car.fuel=Diesel, car.color=red, car.brand=Toyota) > 1` would return all records with *red Diesel cars*, *red Toyotas*, *Toyotas with a Diesel engine*, or *red Toyotas with a Diesel engine*.
* `CURB (color = red OR fabric = 17 OR size = XL) = 2`: *"Exactly two of the given conditions must be fulfilled for a given record."* 
 * `CURB (color = red OR fabric = 17 OR size = XL) > 1`: *"At least two of the given conditions must be fulfilled for a given record."*
 * `CURB (color = red OR fabric = 17 OR size = XL) < 3`: *"Exclude all records where all of the three conditions are fulfilled."*
 * `CURB (color = red OR fabric = 17 OR size = XL) > 0` is the same as `color = red OR fabric = 17 OR size = XL`
 * `CURB (color = red OR fabric = 17 OR size = XL) > 3` is `<NONE>` (can't be fulfilled)
 * `CURB (color = red OR fabric = 17 OR size = XL) <= 3 ` is `<ALL>` (always true)

## §5 Negation

Audlang distinguishes two forms of negation, the *intuitive* **default** negation and the *scientific* **strict** negation.

**[:information_source: Common Notes](#about-negation)**, **[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-negation)**

### §5.1 Default Negation

Every expression can be preceded with `NOT` to express its negation. For better readability some basic expressions support an inline operator (see examples below).

On attribute level (basic expressions) a default `NOT` **includes** the unknowns:
 * `NOT color = red` resp. `color != red` technically means: *"color is not red or we don't know the color"*.

*Examples:*
 * `color != red` 
 * `NOT color = red`
 * `color NOT ANY OF (red, blue, green)`
 * `NOT color = red`
 * `NOT NOT color = red` resp. `NOT color != red` means `color = red`

### §5.2 Strict Negation

Every expression can be preceded with `STRICT NOT` to express its strict negation. For better readability some basic expressions support an inline operator (see examples below).

On attribute level (basic expressions) a `STRICT NOT` **excludes** the unknowns:
 * `STRICT NOT color = red` resp. `STRICT color != red` technically means: *"we know the color and it is not red"*.

*Examples:*
 * `STRICT color != red` 
 * `STRICT NOT color = red`
 * `color STRICT NOT ANY OF (red, blue, green)`
 * `STRICT NOT color = red`
 * `STRICT NOT color IS UNKNOWN` $:=$ `color IS NOT UNKNOWN`
 * :warning: `STRICT NOT color IS NOT UNKNOWN` $:=$ `<NONE>`

### §6 Reference Value Matching

Sometimes it is desirable to compare two attributes of the same record against eachother.

For this purpose Audlang defines [argument references](#123-argument-reference). Starting with an `@`-symbol followed by the attribute name they can be used in most places where otherwise values must be specified.

For example `address.country=@home_country` would match if the address' country field has the same value as the *home_country* field from the same record.

The following operations support argument references:

 * [§3.1 Equals](#31-equals) /  [§3.2 Not Equals](#32-not-equals)
 * [§3.3 Less Than and Greater Than](#33-less-than-and-greater-than)
 * [§3.5 Any Of](#35-any-of)

**[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-reference-values)**

*Examples:*
 * `address.country=@home_country` 
 * `spending > @"personal income"`

### §7 Audlang and Collection Attributes

Some systems collect multiple values for certain attributes from a stream or merge this data from various sources. In this case there is not *one single value* for an attribute in a record; the **argument value is a collection**.

**[:twisted_rightwards_arrows: Implementor Notes](#dealing-with-collection-attributes)**

:bulb: The semantics for [§5.2 Strict Negation](#52-strict-negation) will be applied so that the `UNKNOWN`s will be *excluded* if - and only if - the negation is `STRICT`.

Below we specify the behavior of Audlang expressions on collection attributes:

#### §7.1 Equals on Collection Attributes

 * If the left side of the equals-comparison is a collection attribute and the right side is an argument value, then every record matches if *any* of the collection members equals the given value.
 * If the left side of the equals-comparison is a collection attribute and the right side is an argument reference, then every record matches if *any* of the collection members equals the value of the referenced attribute.
 * If the left side of the equals-comparison is a standard (single-value) attribute and the right side is an argument reference to a collection attribute, then record matches if the record's value on the left equals *any* of the collection members of the referenced attribute on the right.
 * If the left side of the equals-comparison is a collection attribute and the right side is an argument reference to another collection attribute, then a record matches if *any* of the collection members on the left equals *any* of the values of the referenced attribute's collection on the right (*"overlap is not empty"*).

 :bulb: In case of collection attributes the following otherwise impossible expression can be plausible: `multiSelectColor=red AND multiSelectColor=green`
 
*See also:*
 * [§3.1 Equals](#31-equals)
 * [§1.2.2 Argument Values](#122-argument-values)
 * [§1.2.3 Agument References](#123-argument-reference)

#### §7.2 Not Equals on Collection Attributes

 * If the left side of a not-equals-comparison is a collection attribute and the right side is an argument value, then a record matches if *none* of the collection members equals the given value.
 * If the left side of a not-equals-comparison is a collection attribute and the right side is an argument reference, then a record matches if *none* of the collection members equals the value of the referenced attribute.
 * If the left side of a not-equals-comparison is a standard (single-value) attribute and the right side is an argument reference to a collection attribute, then a record matches if the argument value on the left matches *none* of the collection members of the referenced attribute on the right.
 * If the left side of a not-equals-comparison is a collection attribute and the right side is an argument reference to another collection attribute, then a record matches if *none* of the collection members equals *any* of the values of the referenced attribute's collection (*"overlap is empty"*).

*See also:*
 * [§3.2 Not Equals](#32-not-equals)
 * [§1.2.2 Argument Values](#122-argument-values)
 * [§1.2.3 Agument References](#123-argument-reference)

#### §7.3 Less Than and Greater Than on Collection Attributes

Below explanation is applicable to any of the operations less than, less than or equals, greater than etc.:

 * If the left side of a less-than-comparison is a collection attribute and the right side is an argument value, then a record matches if *any* of the collection members is less than the given value.
 * If the left side of a less-than-comparison is a collection attribute and the right side is an argument reference, then a record matches if *any* of the collection members is less than the value of the referenced attribute.
 * If the left side of a less-than-comparison is a standard (single-value) attribute and the right side is an argument reference to a collection attribute, then a record matches if the attribute's value on the left is less than *any* of the collection members on the right side.
 * If the left side of a less-than-comparison is a collection attribute and the right side is an argument reference to another collection attribute, then a record matches if *any* of the collection members on the left is less than *any* of the values of the referenced attribute's collection on the right.

Accordingly, in case of a negation:

 * If the left side of a negated less-than-comparison is a collection attribute and the right side is an argument value, then a record matches if *none* of the collection members is less than the given value.
 * If the left side of a negated less-than-comparison is a collection attribute and the right side is an argument reference, then a record matches if *none* of the collection members is less than the value of the referenced attribute.
 * If the left side of a negated less-than-comparison is a standard (single-value) attribute and the right side is an argument reference to a collection attribute, then a record matches if the attribute's value on the left is *not* less than *any* of the collection members on the right side.
 * If the left side of a negated less-than-comparison is a collection attribute and the right side is an argument reference to another collection attribute, then a record matches if *any* of the collection members on the left is *not* less than *any* of the values of the referenced attribute's collection on the right.

##### Avoid range queries on collection attributes

> :warning: It is not possible to create a condition that *all values* of a collection attribute must meet a condition. Consequently, range queries of the form `argName > 6 AND argName < 9` may lead to unexpected results if `argName` is a collection attribute. In the example, any record with the `argName`-collection values **(2,9,11)** would surprisingly **match** the expression because 2 is *less than 9*, and 11 is *greater than 6*.

:bulb: To adress the above problem you can instead write: `NOT argName <= 6 AND NOT argName >= 9`.

*See also:*
 * [§3.3 Less Than and Greater Than](#33-less-than-and-greater-than)
 * [§1.2.2 Argument Values](#122-argument-values)
 * [§1.2.3 Agument References](#123-argument-reference)

#### §7.4 Between on Collection Attributes

:warning: The `BETWEEN` operator (see [§3.4](#34-between)) should not be used on collection attributes.

`argName BETWEEN (7, 8)` is the same as `argName > 6 AND argName < 9` which leads to unexpected results in conjunction with collection attributes (see [§7.3](#avoid-range-queries-on-collection-attributes)).

#### §7.5 Any Of on Collection Attributes

 * If the left side of an any-of is a collection attribute and the right side is list of values, then a record matches if *any* of the collection members is contained in the given list.
 * If the left side of an any-of is a collection attribute and a list element on the right side is an argument reference, then a record matches if *any* of the collection members matches the value of the referenced attribute.
 * If the left side of an any-of is a standard (single-value) attribute and the list on the right contains an argument reference to a collection attribute, then a record matches if the attribute's value on the left matches *any* of the collection members related to the attribute referenced in the list on the right side.
 * If the left side of an any-of is a collection attribute and the list on the right side contains an argument reference to another collection attribute, then a record matches if *any* of the collection members on the left matches *any* of the values of the referenced attribute's collection on the right (*"overlap not empty"*).

 Accordingly, in case of a negation:

 * If the left side of a negated any-of is a collection attribute and the right side is list of values, then a record matches if *none* of the collection members of the attribute on the left is also contained in the given list.
 * If the left side of a negated any-of is a collection attribute and the list on the right side contains an argument reference, then a record matches if *none* of the collection members matches the value of the referenced attribute.
 * If the left side of a negated any-of is a standard (single-value) attribute and the list on the right contains an argument reference to a collection attribute, then a record matches if the attribute's value on the left matches *none* of the collection members related to the attribute referenced in the list on the right side.
 * If the left side of a negated any-of is a collection attribute and the list on the right side contains an argument reference to another collection attribute, then a record matches if *none* of the collection members on the left side matches *any* of the values of the referenced attribute's collection on the right side (*"overlap empty"*).

*See also:*
 * [§3.5 Any Of](#35-any-of)
 * [§1.2.2 Argument Values](#122-argument-values)
 * [§1.2.3 Agument References](#123-argument-reference)

#### §7.6 Contains and Contains Any

If the argument on the left side is a collection attribute, then a record matches if *any* collection member fulfils the contains / contains any text snippet condition on the right side.

*See also:*
 * [§3.6 Contains Text Snippet](#36-contains-text-snippet)
 * [§3.7 Contains Text Snippet List](#37-contains-any-of-text-snippet-list)
 * [§1.2.4 Snippets](#124-snippets)

# Appendix

## Common Notes

### Main Goals

The Audlang specification is based on a couple of guiding principles:

 * **Precision**: Provide a concise set of language features for defining and combining conditions to define an audience.
 * **Technology Independency**: Provide a feature set we can expect any underlying storage layer to fulfill.
 * **Readability**: Avoid any cryptic features, give features natural names.
 * **End-user focus**: Rather be a DSL than a programming language. Users shall be able to read audience definitions fluently.
 * **Openness**: Don't create any unnecessary restrictions (e.g., level of nesting, argument name or value constraints).
 * **Completeness**: The feature set shall be consistent and complete (e.g., if `<` (less than) is allowed in a certain context, then `>` (greater than) must be allowed well). 

 [:arrow_right: Introduction](#audience-definition-language-specification)

### About plain and double-quoted Strings

There are many ways to implement strings with escaping. After some experiments and comparisons, it was decided to define a hybrid model with plain texts and double-quoted text to simplify the end users' life.
 * Double-quotes should be optional in general and only mandatory if the string otherwise breaks apart or text collides with Audlang-features.
 * There must be an escape mechanism for the double-quote character within a double-quoted string. 
   * The backslash-escaping known from most programming languages has two disadvantages:
     * Typing the backslash feels strange for non-technical users.
     * You must escape all backslashes, so the escaping gets more complicated.
   * Doubling the double-quote characters on the other hand is well-known to most users from tools like Microsoft Excel. This escaping logic is also technically much easer to implement.
 * Escaping in plain-text has no benefits, it only decreases readability. Thus it was decided to distinguish plain strings (restricted set of characters, but suitable for the majority of names resp. values) and double-quoted strings which may include all the allowed characters where any double-quote character needs to be doubled.
 * Control characters in strings are not allowed at all.
   * Most control characters will never appear anyway in audience definitions.
   * Tabulator is painful as it is an invisible character, sometimes hard to distinguish from a single space.
   * Line-breaks are problematic as they break the layout of a given expression string, no matter if you print it single-line or formatted.
   * Thus, all the plain control characters are banned. Special escape-sequences were introduced for rare edge cases.

[:arrow_right: §1.1 Strings](#11-strings)

### No empty argument names

There is no valid case for an empty attribute name or any magic *default attribute name*. Thus, it is part of the grammar not to allow the empty string (`""`) as an argument name or argument reference.

[:arrow_right: §1.2.1 Argument names](#121-argument-names)

### Logical value convention

In Audlang any *logical value* shall be expressed as **`1` (true)** or **`0` (false)**

**Motivation**

The restriction to work with `0` and `1` in expressions endorses clarity.

One could argue that there are many different ways to express **true** and **false**, for example: `y`, `yes`, `T`, `true`, etc. 
At first glance it looks convenient for users if the system would *auto-guess*.

However, besides being confusing for users, this kind of *ambiguity* leads to severe issues under the bonnet. Imagine a user expects `on` to mean *true* but the underlying "guessing function" does not know `on`, yet. Such mistakes can be hard to diagnose. 

[:arrow_right: §2.2 Logical Values](#22-logical-values)

### About Date Values

*Do we need date values?!*

Indeed, it might be better practice to decompose a date into *year*, *month* and *day* (maybe additionally *day of week*) and make these available as three numeric attributes.

However, attributes like *last contact* in the system's base audience could be date fields, and it should be possible to create a condition based on such a field.

The Audlang-convention increases readability and avoids ambiguity when writing expressions.

[:arrow_right: §2.3 Date Values](#23-date-values)

### About unknown values

Ideally, in an underlying base audience all missing values have been eliminated by applying statistical methods to set average values. 

However, this kind of preparation is not trivial, so we may see missing values for certain attributes of our base audience.

This cannot be ignored (see also [§5 Negation](#5-negation)).

`IS [NOT] UNKNOWN` makes it possible to address related records in the base audience *explicitly*.

[:arrow_right: §3.8 Is (Not) Unknown](#38-is-not-unknown)

### About curbed or

Creating a simple *two-of-five* condition becomes extremely tedious based on `AND` and `OR`, even if the base conditions are all simple.

*Example:* 

In a panel people were asked to answer 5 questions (q1 .. q5, each yes/no), and you want to filter all records where at least two of the questions has been answered with yes.

```sql
(q1=yes AND q2=yes)
OR (q1=yes AND q3=yes) 
OR (q1=yes AND q4=yes)
OR (q1=yes AND q5=yes)
OR (q2=yes AND q3=yes) 
OR (q2=yes AND q4=yes)
OR (q2=yes AND q5=yes)
OR (q3=yes AND q4=yes)
OR (q3=yes AND q5=yes)
OR (q4=yes AND q5=yes)
```

Obviously, this is lengthy and prone to errors.

Audlang's `CURB`-expression gives you a way to express the same condition in a short and readable form: 

```sql
CURB ( q1=yes OR q2=yes OR q3=yes OR q4=yes OR q5=yes ) >= 2
```

[:arrow_right: §4.3 Curbed Or](#43-curbed-or)

### About Negation

Negation (`NOT`) expresses the requirement that a certain condition *is not fulfilled* for a certain record. In its simplest form we request a certain attribute *not* to match a given value.

As long as your database contains values for each attribute in every record there is no problem with negation. 

Unfortunatly, often in the underlying data store there are *missing values*. For example, a certain panel question was not answered or data has been merged from multiple sources. This leads to *unknowns*. Audlang allows to explicitly deal with this situation using the `IS UNKNOWN` operator but we cannot magically solve a principle issue with unknown values in conjunction with negation.

To get a better understanding of the core problem, please look at the data store below:

| record | car.brand  | car.color |
| :------|:----------:| ---------:|
| 1578   | VW         | red       |
| 1599   | Toyota     |           |
| 6712   | BMW        | red       |
| 8127   | Ford       | green     |

Obviously, for record **1599** we don't know the color of the car. A query with the Audlang expression `car.color IS UNKNOWN` would return this record.

The query `car.color != red` will return **2** records (**1599** and **8127**). While this is *intuitively* what we expect, it is *scientifically incorrect*.

The problem is that we don't know the color of **1599**'s car, so we also don't know that it is *not red*. Maybe Toyotas are typically red or 80% are white, *who knows?*

Ideally, preprocessing of the underlying data stores deals with this category of problems *beforehand*. All the missing values would be replaced with meaningful averages, and biases would have been detected and addresses appropriately.

Thus, Audlang defines the intuitive complement negation (aka *"any except for"*) as the default behavior. Most of the time this will work well when defining audiences.

:bulb: Use the default negation `NOT expr` if you want to express *"Take all records except for the ones that match `expr`"*. Consequently, `expr1 AND NOT expr2` means: *"Take all records that match expr1 **minus** the ones that match `expr2`"*

For special cases were a user intentionally wants to **exclude** the unknown values from a negation, Audlang defines the `STRICT NOT` operator.

In the little example above the query `STRICT car.color != red` **only returns record 8127**. For this record we *know* that the car-color is not red.

#### Behavior of NOT

 * `NOT car.color=red` returns all cars with a different color than red plus the ones where we don't know the color.
 * `argName != argValue` $\Leftrightarrow$ `NOT argName = argValue` $\Leftrightarrow$ `argName != argValue OR argName IS UNKNOWN`
 * `NOT argName IS UNKNOWN` $\Leftrightarrow$ `argName IS NOT UNKNOWN` returns all records where we know the value of the attribute `argName`.
 * `NOT argName IS NOT UNKNOWN` $:=$ `argName IS UNKNOWN` returns all records were we don't have any value for the attribute `argName`.
 * `NOT <ALL>` $:=$ `<NONE>`
 * `NOT <NONE>` $:=$ `<ALL>`
 * `NOT NOT argName = argValue` $\Leftrightarrow$ `argName = argValue`
 * `NOT STRICT NOT argName = argValue ` $\Leftrightarrow$ `argName = argValue OR argName IS UNKNOWN`
 * `NOT ( expr1 AND expr2 )` $\Leftrightarrow$ `NOT expr1 OR NOT expr2`
 * `NOT ( expr1 OR expr2 )` $\Leftrightarrow$ `NOT expr1 AND NOT expr2`
 * `NOT CURB (...) = n` $\Leftrightarrow$ `CURB (...) != n`
 * `NOT CURB (...) < n` $\Leftrightarrow$ `CURB (...) >= n`
 * `NOT CURB (...) <= n` $\Leftrightarrow$ `CURB (...) > n`
 * `NOT CURB (...) > n` $\Leftrightarrow$ `CURB (...) <= n`
 * `NOT CURB (...) >= n` $\Leftrightarrow$ `CURB (...) < n`

#### Behavior of STRICT NOT

 * `STRICT NOT car.color=red` returns all records with a different car color than red ignoring all records where we don't know the car color.
 * `STRICT argName != argValue` $\Leftrightarrow$ `STRICT NOT argName = argValue` ignores any records where the attribute `argName` is unknown.
 * `STRICT NOT argName IS UNKNOWN` $:=$ `argName IS NOT UNKNOWN` returns all records where we know the value of the attribute `argName`.
 * `STRICT NOT argName IS NOT UNKNOWN` $:=$ `<NONE>` does not return *any* records because *strict not* always **excludes** the unknowns.
 * `STRICT NOT <ALL>` $:=$ `<NONE>`
 * `STRICT NOT <NONE>` $:=$ `<ALL>`
 * `STRICT NOT STRICT NOT argName = argValue ` $\Leftrightarrow$ `argName = argValue`
 * `STRICT NOT NOT argName = argValue ` $\Leftrightarrow$ `argName = argValue`
 * `STRICT NOT ( expr1 AND expr2 )` $\Leftrightarrow$ `STRICT NOT expr1 OR STRICT NOT expr2`
 * `STRICT NOT ( expr1 OR expr2 )` $\Leftrightarrow$ `STRICT NOT expr1 AND STRICT NOT expr2`
 * `STRICT NOT CURB (...) = n` $\Leftrightarrow$ `NOT CURB (...) = n` $\Leftrightarrow$ `CURB (...) != n`
 * `STRICT NOT CURB (...) < n` $\Leftrightarrow$ `NOT CURB (...) < n` $\Leftrightarrow$ `CURB (...) >= n`
 * `STRICT NOT CURB (...) <= n` $\Leftrightarrow$ `NOT CURB (...) <= n` $\Leftrightarrow$ `CURB (...) > n`
 * `STRICT NOT CURB (...) > n` $\Leftrightarrow$ `NOT CURB (...) > n` $\Leftrightarrow$ `CURB (...) <= n`
 * `STRICT NOT CURB (...) >= n` $\Leftrightarrow$ `NOT CURB (...) >= n` $\Leftrightarrow$ `CURB (...) < n`

[:arrow_right: §5 Negation](#5-negation)

## Implementor Notes

### Handling double-quoted strings

The removal of the extra double-quotes is not part of the ANTLR-grammar. At runtime your individual ANTLR-parser implementation will receive the string still enclosed in double-quotes potentially containing *doubled* double-quote characters.
  * This solution keeps the grammar easy to read and free from scripting.
  * The later removal is rather easy to implement explicitly with a few lines of code in any programming language.

:bulb: The **audlang-spec project** contains a reference implementation written in Java.

[:arrow_right: §1.1 Strings](#11-strings)

### Handling escape sequences

Mapping the escape-sequences in a string back to their corresponding control characters is **not** defined as part of the ANTLR-grammar. Thus, your individual parser implementation needs to do that after reading and [removal of the extra double-quotes](#handling-double-quoted-strings)).

  * Although, it might be possible to create a grammar covering all cases properly, it is *way easier* to explicitly code and properly test this logic independently.

:bulb: The **audlang-spec project** contains a reference implementation written in Java.

[:arrow_right: §1.1 Strings](#11-strings)

### Dealing with argument names

As we don't have any constraints on argument names besides not to be empty, application implementors can apply any syntax or naming convention here. However, you should avoid the double-quote '"' as this leads to more escaping effort which negatively impacts readability.

[:arrow_right: §1.2.1 Argument names](#121-argument-names)

### Empty argument values

Usually, *empty string* is a discouraged attribute value and should be handled like *unknown*. But if we prohibit the usage of the empty string as an argument value *entirely*, we could no longer deal with the edge case that in some database *empty string* means something different than *unknown*. Thus, it was decided to allow the empty string as an argument value and let the underlying implementation decide what to do.

[:arrow_right: §1.2.2 Argument values](#122-argument-values)

### Dealing with comments

Because comments are not part of the expression nor influence the execution, implementors are free to either discard all comments during a parse run or treat them as first-class citizens. The latter is recommended if you intend to provide auto-formatting of expressions while preserving any comments.

[:arrow_right: §1.5 Comments](#15-comments)

### Dealing with different types

Embedding types (e.g. integer, float, boolean, etc.) into an expression language is a two-edged sword. While it eliminates a whole category of *invalid* expressions early, it comes with severe disadvantages:
 * Considerably higher grammar complexity.
 * Increased initial parsing and validation effort. 
   * :bulb: It is much easier to provide meaningful error messages if you have the name and the value to complain about than dealing with an ANTLR parse result that tells that there is *no viable alternative*. :smirk:
 * Data model coupling: If an attribute is currently defined as numeric, and for some reason you later need to switch to alpha-numeric values, you must *rewrite* all expressions referencing this attribute.

Audlang is *type-agnostic*, it is not aware of any value's type on language level. Consequently, solely based on an Audlang-expression one can only tell if the syntax is correct but not whether it is *valid* in the sense that it could be executed.

E.g., `number_of_children > "Harry"` is a valid Audlang-expression. But if `number_of_children` is numeric, this expression cannot be executed by the underlying technical infrastructure.

It is left to subsequent parts of the application to deal with type resolution and validation as well as further plausibility checks.

The Audlang type conventions shall help implementing a plausible consistent solution for dealing with typed values.

:bulb: Any meta-data driven type-aware UI-component should apply the conventions whenever a user enters/selects a value of a known type when updating an expression.

[:arrow_right: §2 Type Conventions](#2-type-conventions)

### Handling integer conversion

:bulb: Besides the problem with different international format standards, the main reason why we don't encourage grouping digits in numbers is avoiding ambiguity. On language level all values are strings, and whenever possible no two different strings should express the same value.

[:arrow_right: §2.1.1 Integer values](#211-integer-values)

### Handling decimal conversion

The reduction to 7 decimal digits reduces the effective precision but increases readability. In practical examples 7 digits is already more than to be expected. For example, if we deal with longitude and latitude values (user bought something in a shop we have the coordinates for), then 7 decimal digits would narrow the position down to millimeters!

:bulb: The reason why we don't encourage grouping is avoiding ambiguity. On language level all values are strings, and whenever possible no two different strings should express the same value.

[:arrow_right: §2.1.2 Decimal values](#212-decimal-values)

### Dealing with date values

:bulb: The reason why we encourage a single date format is avoiding ambiguity. On language level all values are strings, and whenever possible no two different strings should express the same value.

*Why is there no **time** type convention?*

Well, time (of the day) is tricky, especially if you are collecting data across time zones. It is not a good idea to let a user query by time of the day on raw data. For plausible expressions based on the time of the day *preprocessing* of the base audience is crucial. If there is anyway a preprocessing/cleansing step, then it is a good practice to categorize the time values (e.g., just the hour portion or something like "morning", "noon", "afternoon", etc.) to support users in writing meaningful expressions.

[:arrow_right: §2.3 Date values](#23-date-values)

### Dealing with unknown values

`IS [NOT] UNKNOWN` is nothing else than a more user-friendly name for `IS [NOT] NULL` known from SQL.

:bulb: Implementations that don't have any missing values should gracefully handle this operation (`IS UNKNOWN` can never be true and `IS NOT UNKNOWN` always holds true). 

[:arrow_right: §3.8 Is (Not) Unknown](#38-is-not-unknown)

### Dealing with All and None

`<ALL>` and `<NONE>` may seem unnecessary and awkward at first glance, but they are technically vital. If you deal with expression optimization you will quickly feel the need for both of them.

*Example 1:*

`color = blue or color != blue or color is unknown`

If you optimize this expression, the only possible outcome is `<ALL>` because there is no way for a record of the base audience *not* to fulfill this condition.

*Example 2:*

`color = blue and color != blue`

If you optimize this expression, the only possible answer is `<NONE>` because there is no way for a record of the base audience to fulfill this condition.

Concrete implementations should inform the users about this problem rather than wasting time with execution.

[:arrow_right: §3.3 All and None](#39-all-and-none)

### Dealing with nesting

The operators `AND` and `OR` allow combining basic expressions as well as other composite expressions. The level of nesting is not limited.

[:arrow_right: §4 Composite Expressions](#4-composite-expressions)

### Dealing with Curbed Or

A `CURB` is nothing but *syntactic sugar* introduced to simplify expression writing. 
Consequently. the preferred implementation strategy is resolving the curb before query execution.

*Resolving* a `CURB` means finding all possible combinations based on the member expressions and the bound to create a (potentially large) expression only using `AND` and `OR`.

```sql
CURB (color = red OR fabric = cotton OR look = fancy) < 3
```
turns into

```sql
(color = red AND fabric = cotton AND look != fancy)
OR (color = red AND fabric != cotton AND look = fancy)
OR (color = red AND fabric != cotton AND look != fancy)
OR (color != red AND fabric = cotton AND look = fancy)
OR (color != red AND fabric != cotton AND look = fancy)
OR (color != red AND fabric = cotton AND look != fancy)
OR (color != red AND fabric != cotton AND look != fancy)
```

This is of course the same as:
`NOT (color = red AND fabric = cotton AND look = fancy)` which turns into `(color != red OR fabric != cotton OR look != fancy)`

**:warning: Important:** An enclosing **negation** (`[STRICT] NOT`) **always applies to the bound condition**. 
```sql
NOT CURB (color = red OR fabric = cotton OR look = fancy) < 3
```
turns into 
```sql
CURB (color = red OR fabric = cotton OR look = fancy) >= 3
```
This might be relevant during the execution of the expression which could otherwise potentially interfere with the strictness rules (see [§5 Negation](#5-negation)). CURB *never inherits STRICT* from an enclosing negation, and all negations implied by boolean CURB-resolution (see example above) are *default negations* and thus never strict.

:bulb: Technically, the curb's bound value is a 64 bit value (see [§2.1.1 Integer Values](#211-integer-values)), way larger than any expected bound value. Implementors must provide clear and helpful error messages in case a user misinterprets the bound and enters a very large value. This should be detected and reported early in the parsing process rather than causing mysterious issues down the execution chain.

[:arrow_right: §4.3 Curbed Or](#43-curbed-or)

### Dealing with negation

Audlang specifies that the semantics of negation (and strictness) fall down to the attribute level because a single expression stands for one set of filtered records to be selected from the base audience. Consequently, the negations must be technically applied on the attributes themselves.

When creating the final expression for execution on any target system all negations from higher levels must *trickle down* to the leave conditions.

```
NOT ( (a=1 AND b=2) OR (c=2 AND d!=5) )
<=> NOT (a=1 AND b=2) AND NOT (c=2 AND d!=5)
<=> (NOT a=1 OR NOT b=2) AND (NOT c=2 OR NOT d!=5)
<=> ((a!=1 OR a IS UNKNOWN) OR (b!=2 OR b IS UNKNOWN)
      AND ((c!=2 OR c IS UNKNOWN) OR d=5)
```

```
STRICT NOT ( (a=1 AND b=2) OR (c=2 AND d!=5) )
<=> STRICT NOT (a=1 AND b=2) AND STRICT NOT (c=2 AND d!=5)
<=> (STRICT NOT a=1 OR STRICT NOT b=2) AND (STRICT NOT c=2 OR STRICT NOT d!=5)
<=> ((a IS NOT UNKNOWN AND a!=1) OR (b IS NOT UNKNOWN AND b!=2)
      AND ((c IS NOT UNKNOWN AND c!=2) OR d=5)
```

:warning: After the last transformation step the *strictness* is still preserved but no longer a *descriptive part* of the expression.

#### Special Cases

The following examples show plausibility and consistency.

:bulb: Reminder: `STRICT NOT (argName IS NOT UNKNOWN)` $:=$ `<NONE>` because *strict not* always excludes the unknowns.

 * `NOT NOT argName = argValue ` $\Leftrightarrow$ `argName = argValue` :white_check_mark:
   ```
   NOT NOT a=1 <=> NOT(a!= 1 OR a IS UNKNOWN) 
   <=> NOT(a!=1) AND a IS NOT UNKNOWN
   <=> (a=1 OR a IS UNKNOWN) AND a IS NOT UNKNOWN 
   <=> a=1 AND a is NOT UNKNOWN
   <=> a=1
   ```
 * `NOT STRICT NOT argName = argValue ` $\Leftrightarrow$ `argName = argValue OR argName IS UNKNOWN` :white_check_mark:
   ```
   NOT STRICT NOT a=1 <=> NOT(a!= 1 AND a IS NOT UNKNOWN) 
   <=> NOT(a!=1) OR NOT(a IS NOT UNKNOWN)
   <=> (a=1 OR a IS UNKNOWN) OR a IS UNKNOWN
   <=> a=1 OR a is UNKNOWN
   ```
    * The result above might look surprising but makes sense if you think about the idea of the default negation as an *intuitive complement*. Consequently, the result must include the unknowns.
 * `STRICT NOT NOT argName = argValue ` $\Leftrightarrow$ `argName = argValue` :white_check_mark:
   ```
   STRICT NOT NOT a=1 <=> STRICT NOT(a!=1 OR a IS UNKNOWN) 
   <=> STRICT NOT(a!=1) AND STRICT NOT (a IS UNKNOWN)
   <=> (a=1 AND a IS NOT UNKNOWN) AND a IS NOT UNKNOWN 
   <=> (a=1 AND a IS NOT UNKNOWN)
   <=> a=1
   ```
 * `STRICT NOT STRICT NOT argName = argValue ` $:=$ `argName = argValue`
   :white_check_mark:
   ```
   STRICT NOT STRICT NOT a=1 <=> STRICT NOT(a!= 1 AND a IS NOT UNKNOWN) 
   <=> STRICT NOT(a!=1) OR STRICT NOT (a IS NOT UNKNOWN)
   <=> (a=1 AND a IS NOT UNKNOWN) OR <NONE> 
   <=> (a=1 AND a IS NOT UNKNOWN)
   <=> a=1
   ```

[:arrow_right: §5 Negation](#5-negation)

### Dealing with reference values

> **This part of the Audlang specification is optional.**

Even if the feature is not available in an environment, any Audlang-implementation must still parse expressions containing argument references to return a meaningful explanatory error message.

[:arrow_right: §6 Reference Value Matching](#6-reference-value-matching)


### Dealing with collection attributes

> **This part of the Audlang specification is optional.**

Apart from a few edge-cases (e.g., [BETWEEN](#74-between-on-collection-attributes)), for users of the Audlang it should not matter whether any attribute carries only a single value or stands for a collection of values.

However, implementors are encouraged to help users understand the implications (tooltips, icons, etc.). It is recommended to warn users if problematic operators are applied on collection attributes (see [§7.3](#73-less-than-and-greater-than-on-collection-attributes), [§7.4](#74-between-on-collection-attributes)).

[:arrow_right: §7 Audlang and Collection Attributes](#7-audlang-and-collection-attributes)

# Document History


| Version | Date | Changes |
| :-----|:-----|:-----|
| 1.1   | August 2024    | Clarification regarding CURB |
| 1.0   | August 2024    | First specification release |
