# Audlang Sample Generation

This package contains supplementary code to generate sample expressions for testing the [Audlang.g4](../../../../../../antlr4/Audlang.g4).

## Motivation

This is a specification project. Originally, I just wanted to include the ANTLR-grammar file along with some markdown documentation.

However, in previous projects I learned (the hard way :smirk:) that it is really painful to find errors in the grammar *lately* as every grammar change causes a ripple effect with many subsequent changes down the line. 

I wanted to ensure that the ANTLR grammar provided in this project does what it is supposed to do *before* creating a full-fledged parser in any programming language.

Therefore it needs two things:
 * Infrastructure to parse expressions without providing a full parser implementation.
 * A reasonable amount of sample expressions for testing any kind of expression.

The ANTLR runtime helps with the first point. It is possible to validate an exception and collect parsed elements with a few lines of supplementary code. If you are interested, take a look into the `AntlrTestHelper` under `src/test/java`.

The bigger challenge was the test data. I started hand-crafting samples and quickly realized that this strategy would be exhausting, not to say infeasible.

Eventually, I gave up writing individual samples and decided to spent some time creating a sample generation component.

## Solution

### Sample expression templates

First, I defined a little *auxiliary language* to textually specify sample expression templates, each to be converted into one ore many sample expressions for the actual test execution.

[SampleExpression](SampleExpression.java) can either carry a template or an actual sample expression in its `expression()` field. This way we can use the corresponding JSON to maintain the sample templates as well to store the generated samples.

Templates and corresponding samples are organized in groups. All groups are part of one template file, the generated [SampleExpressionGroups](SampleExpressionGroup.java) groups each go to individual files.

I recommend reviewing [sample-expressions-template.json](../../../../../../resources/samples/sample-expressions-template.json) which defines all the sample templates used in the build-process to verify the grammar.

The macro language by intention uses a syntax that does not collide with the Audlang. You simply write an example how it should look (e.g., `color=red`), and then you replace the critical elements with macros, so the generation process and *vary* these tokens and include the data in the generation info.

In the example above:
 * The literal name `color` should be replaced with the macro `${ARG_NAME}`.
 * The literal operator `=` should be replaced with `${OP}`.
 * The literal value `red` should be replaced with `${ARG_VALUE}`

Now, your *expression template* looks like this: `${ARG_NAME}${OP}${ARG_VALUE}`

The generation process will now auto-generate a couple of names, operators and values to compose multiple sample expressions based on your template.

If you don't want any variety, you can set each macro to a fixed value: `${ARG_NAME:color}${OP:=}${ARG_VALUE:red}` would exactly recreate the original expression (`color=red`). But other than with the literal definition, the generator can attach generation information. This data can later be used for detailed test verification when parsing sample expressions. 

#### Feature overview

 * The literal underscore (`_`) forces a *mandatory* whitespace or comment, the generator will pick one.
 * The literal tilde (`~`) suggests an *optional* whitespace or comment, the generator will pick one or skip.
 * `${ARG_NAME}` generates variations of valid argument names, see [StatelessInstruction](StatelessInstruction.java)
   * `${ARG_NAME:color}` tells the generator to only create one variation with a fixed value (here: `color`).
   * `${ARG_NAME!}` tells the generator to only create one variation with the first pattern (best standard value).
   * `${ARG_NAME*}` tells the generator to create variations with all available patterns.
 * `${ARG_VALUE}` generates variations of valid argument values, see [StatelessInstruction](StatelessInstruction.java)
   * `${ARG_VALUE:red}` tells the generator to only create one variation with a fixed value (here: `red`).
   * `${ARG_VALUE!}` tells the generator to only create one variation with the first pattern (best standard value).
   * `${ARG_VALUE*}` tells the generator to create variations with all available patterns.
 * `${SNIPPET}` generates variations of valid snippet values (for contains comparison), see [StatelessInstruction](StatelessInstruction.java)
   * `${SNIPPET:old}` tells the generator to only create one variation with a fixed value (here: `old`).
   * `${SNIPPET!}` tells the generator to only create one variation with the first pattern (best standard value).
   * `${SNIPPET*}` tells the generator to create variations with all available patterns.
 * `${OP}` generates variations with Audlang operators, see [StatelessInstruction](StatelessInstruction.java)
   * `${OP:=}` tells the generator to only create one variation with a fixed value (here: `=`).
   * `${OP!}` tells the generator to only create one variation with the first pattern (equals).
   * `${OP*}` tells the generator to create variations with all available patterns.
 * `${ALL}` picks a spelling variation of the `<ALL>` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${ALL!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${ALL*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${NONE}` picks a spelling variation of the `<NONE>` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${NONE!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${NONE*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${IS}` picks a spelling variation of the `IS` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${IS!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${IS*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${NOT}` picks a spelling variation of the `NOT` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${NOT!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${NOT*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${UNKNOWN}` picks a spelling variation of the `UNKNOWN` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${UNKNOWN!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${UNKNOWN*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${STRICT}` picks a spelling variation of the `STRICT` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${STRICT!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${STRICT*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${ANY}` picks a spelling variation of the `ANY` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${ANY!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${ANY*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${OF}` picks a spelling variation of the `OF` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${OF!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${OF*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${CONTAINS}` picks a spelling variation of the `CONTAINS` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${CONTAINS!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${CONTAINS*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${BETWEEN}` picks a spelling variation of the `BETWEEN` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${BETWEEN!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${BETWEEN*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${CURB}` picks a spelling variation of the `CURB` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${CURB!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${CURB*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${BOUND}` picks a variation a valid bound value (for CURB), see [StatelessInstruction](StatelessInstruction.java)
   * `${BOUND!}` tells the generator to only create one variation with the first bound value pattern (best standard value).
   * `${BOUND*}` creates all available variations of a valid bound, see [StatelessInstruction](StatelessInstruction.java)
 * `${AND}` picks a spelling variation of the `AND` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${AND!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${AND*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${OR}` picks a spelling variation of the `OR` token, see [StatelessInstruction](StatelessInstruction.java)
   * `${OR!}` tells the generator to only create one spelling variation with the first pattern (best standard value).
   * `${OR*}` creates all available spelling variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * `${SNIPPET_LIST}` creates a comma-separated list of valid snippets (for a CONTAINS), see [StatelessInstruction](StatelessInstruction.java)
 * `${MIXED_LIST}` creates a comma-separated list (one or multiple elements) of valid argument values and argument references (for *any of*), see [StatelessInstruction](StatelessInstruction.java)
 * `${COMPOSITE_EXPRESSION}` takes a few of the previously (in the same run) generated sample expressions which are marked `composite()` and produces variations by appending the expression text.
   * `${COMPOSITE_EXPRESSION!}` only creates a single variation.
   * `${COMPOSITE_EXPRESSION*}` creates variations for each of the previously generated composite expressions.
 * `${OPAQUE_EXPRESSION}` takes a few of the previously (in the same run) generated sample expressions which are **not** marked `composite()` and produces variations by appending the expression text.
   * `${OPAQUE_EXPRESSION!}` only creates a single variation.
   * `${OPAQUE_EXPRESSION*}` creates variations for each of the previously generated composite expressions.
 * `${EXPRESSION:id}` includes the expression with the given id into the current expression. If the id cannot be found we try to find the group with that name. In case of a group match a few results will be created.
   * `${EXPRESSION!:id}` If `id` is a group match, only take the first sample expression out of that group.
   * `${EXPRESSION*:id}` If `id` is a group match, take all sample expressions out of that group to generate variations.
 * `${COMMENT}` picks a variation of a valid Audlang comment, see [StatelessInstruction](StatelessInstruction.java)
   * `${COMMENT!}` tells the generator to only create one variation with the first pattern (best standard value).
   * `${COMMENT*}` creates all available variations of the token, see [StatelessInstruction](StatelessInstruction.java)
 * Any text not covered by the macros above will be copied as is into the generated sample.
 
 :bulb: A built-in threshold of 50 generated expressions (variations) per template protects the system from overflow (combinatoric explosion).


#### Infos for validation

During the sample generation process we have a lot of useful information that could help with testing the created samples. So, I decided to collect this data in [SampleGenInfo](SampleGenInfo.java) and attach it to each generated sample expression.

Whenever required we can read the generated sample expressions, run a parser implementation, and use the generation information to set expectations and create assertions for result verification.

### Generation

The class [SampleExpressionUtils](SampleExpressionUtils.java) provides methods to run the process.

Besides `generateSamples(...)` you can find there methods related to template and sample persistence.

