//@formatter:off
/*
 * TokenTemplateInstruction
 * Copyright 2024 Karl Eilebrecht
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
//@formatter:on

package de.calamanari.adl.util.sgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import de.calamanari.adl.util.TriFunction;

/**
 * This enumeration covers the majority of all token-based stateless instructions to extend sample expressions.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public enum StatelessInstruction implements TemplateInstruction {

    /**
     * appends mandatory whitespace or a comment
     */
    MANDATORY_WHITESPACE_OR_COMMENT(BasicPatterns.TPL_MANDATORY_WHITESPACE_OR_COMMENT, StatelessInstruction::augmentWithWhitespaceOrComment, 1),

    /**
     * appends nothing, whitespace or a comment
     */
    OPTIONAL_WHITESPACE_OR_COMMENT(BasicPatterns.TPL_OPTIONAL_WHITESPACE_OR_COMMENT, StatelessInstruction::augmentWithWhitespaceOrComment, 1),

    /**
     * appends the &lt;ALL&gt; expression, varies spelling
     */
    ALL(BasicPatterns.TPL_ALL, StatelessInstruction::augmentWithTokenMultiSelect, 1),

    /**
     * appends the &lt;NONE&gt; expression, varies spelling
     */
    NONE(BasicPatterns.TPL_NONE, StatelessInstruction::augmentWithTokenMultiSelect, 1),

    /**
     * appends an argument name
     */
    ARG_NAME(BasicPatterns.TPL_ARG_NAME, StatelessInstruction::augmentWithTokenMultiSelect, 1),

    /**
     * appends an argument value
     */
    ARG_VALUE(BasicPatterns.TPL_ARG_VALUE, StatelessInstruction::augmentWithTokenMultiSelect, 1),

    /**
     * appends IS, varies spelling
     */
    IS(BasicPatterns.TPL_IS, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends NOT, varies spelling
     */
    NOT(BasicPatterns.TPL_NOT, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends UNKNOWN, varies spelling
     */
    UNKNOWN(BasicPatterns.TPL_UNKNOWN, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends any of the availabe operators {@link SampleExpressionOperator}
     */
    OP(BasicPatterns.TPL_OP, StatelessInstruction::augmentWithTokenMultiSelect, 1),

    /**
     * appends an argument reference
     */
    ARG_REF(BasicPatterns.TPL_ARG_REF, StatelessInstruction::augmentWithTokenMultiSelect, 1),

    /**
     * appends STRICT, varies spelling
     */
    STRICT(BasicPatterns.TPL_STRICT, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends ANY, varies spelling
     */
    ANY(BasicPatterns.TPL_ANY, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends OF, varies spelling
     */
    OF(BasicPatterns.TPL_OF, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends a comma-separated list of mixed values and references
     */
    MIXED_LIST(BasicPatterns.TPL_MIXED, StatelessInstruction::augmentWithTokenCSV, 1),

    /**
     * appends CONTAINS, varies spelling
     */
    CONTAINS(BasicPatterns.TPL_CONTAINS, StatelessInstruction::augmentWithTokenMultiSelect, 1),

    /**
     * appends a text snippet (for contains)
     */
    SNIPPET(BasicPatterns.TPL_SNIPPET, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends a comma-separated text-snippet list (for contains)
     */
    SNIPPET_LIST(BasicPatterns.TPL_SNIPPET, StatelessInstruction::augmentWithTokenCSV, 1),

    /**
     * appends BETWEEN, varies spelling
     */
    BETWEEN(BasicPatterns.TPL_BETWEEN, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends a numeric bound value
     */
    BOUND(BasicPatterns.TPL_BOUND, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends a comment
     */
    COMMENT(BasicPatterns.TPL_COMMENT, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends AND, varies spelling
     */
    AND(BasicPatterns.TPL_AND, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends OR, varies spelling
     */
    OR(BasicPatterns.TPL_OR, StatelessInstruction::augmentWithTokenSingleSelect, 1),

    /**
     * appends CURB, varies spelling
     */
    CURB(BasicPatterns.TPL_CURB, StatelessInstruction::augmentWithTokenSingleSelect, 1);

    /**
     * function to apply for this token
     */
    private final TriFunction<StatelessInstruction, SampleExpression, Integer, List<SampleExpression>> instruction;

    /**
     * Patterns the token's function can select from randomly
     */
    private final String[] patterns;

    /**
     * default output limit for this instruction's variations
     */
    private final int defaultOutputLimit;

    @Override
    public int getDefaultOutputLimit() {
        return defaultOutputLimit;
    }

    private StatelessInstruction(String[] patterns, TriFunction<StatelessInstruction, SampleExpression, Integer, List<SampleExpression>> instruction,
            int defaultOutputLimit) {
        this.patterns = patterns;
        this.instruction = instruction;
        this.defaultOutputLimit = defaultOutputLimit;
    }

    /**
     * Picks one of the patterns randomly
     * 
     * @param patterns not null, not empty
     * @param rand for making a random decising
     * @return one of the patterns
     */
    private static String pickValue(String[] patterns, Random rand) {
        int idx = 0;
        if (patterns.length > 1) {
            idx = rand.nextInt(patterns.length - 1) + 1;
        }
        return patterns[idx];
    }

    /**
     * Creates a list of values, each item appearing only once.
     * 
     * @param patterns not null, not empty
     * @param rand for making a random decising
     * @param len number of items requested in the generated list
     * @return list of patterns (may be shorter than requested if there are not enough patterns)
     */
    private static List<String> pickValuesNoDuplicates(String[] patterns, Random rand, int len) {

        if (patterns.length >= len) {
            return Arrays.asList(patterns);
        }

        List<String> res = new ArrayList<>();
        List<String> availablePatterns = new ArrayList<>(Arrays.asList(patterns));

        for (int i = 0; i < len; i++) {
            if (availablePatterns.size() == 1) {
                res.add(availablePatterns.get(0));
                break;
            }
            else {
                String value = pickValue(availablePatterns.toArray(new String[0]), rand);
                res.add(value);
                availablePatterns.remove(value);
            }
        }
        return res;

    }

    /**
     * Creates a list of values, duplicates are allowed.
     * 
     * @param patterns not null, not empty
     * @param rand for making a random decision
     * @param max limits the maximum number of items in the generated list
     * @return list of patterns
     */
    private static List<String> pickValues(String[] patterns, Random rand, int max) {

        List<String> res = new ArrayList<>();

        int limit = rand.nextInt(max) + 1;

        for (int i = 0; i < limit; i++) {
            String value = pickValue(patterns, rand);
            res.add(value);
        }
        return res;

    }

    /**
     * Updates the expressions generation info with the meta data of the current token's operation
     * 
     * @param info to be updated
     * @param pattern piece of text that was appended
     */
    void updateGenInfo(SampleGenInfo info, String pattern) {
        switch (this) {
        case ALL:
            info.incrementCntAll();
            break;
        case NONE:
            info.incrementCntNone();
            break;
        case IS:
            info.incrementCntIs();
            break;
        case NOT:
            info.incrementCntNot();
            break;
        case UNKNOWN:
            info.incrementCntUnknown();
            break;
        case STRICT:
            info.incrementCntStrict();
            break;
        case ANY:
            info.incrementCntAny();
            break;
        case OF:
            info.incrementCntOf();
            break;
        case CONTAINS:
            info.incrementCntContains();
            break;
        case BETWEEN:
            info.incrementCntBetween();
            break;
        case CURB:
            info.incrementCntCurb();
            break;
        case OP:
            createOrUpdateList(info.getOperators(), SampleExpressionOperator.resolve(pattern), info::setOperators);
            break;
        case ARG_NAME:
            createOrUpdateList(info.getArgNames(), pattern, info::setArgNames);
            break;
        case ARG_VALUE:
            createOrUpdateList(info.getArgValues(), pattern, info::setArgValues);
            break;
        case ARG_REF:
            createOrUpdateList(info.getArgRefs(), pattern.substring(1), info::setArgRefs);
            break;
        case COMMENT:
            createOrUpdateList(info.getComments(), trimComment(pattern), info::setComments);
            break;
        case AND:
            info.incrementCntAnd();
            break;
        case OR:
            info.incrementCntOr();
            break;
        case SNIPPET:
            createOrUpdateList(info.getSnippets(), pattern, info::setSnippets);
            break;
        case BOUND:
            createOrUpdateList(info.getBoundValues(), Integer.valueOf(pattern), info::setBoundValues);
            break;
        // $CASES-OMITTED$
        default:
        }
    }

    /**
     * Removes whitespace surrounding a comment before logging to generation info
     * 
     * @param comment
     * @return comment only
     */
    private String trimComment(String comment) {

        int start = 0;
        while (comment.charAt(start) != '/') {
            start++;
        }

        int end = comment.length();
        while (comment.charAt(end - 1) != '/') {
            end--;
        }
        return comment.substring(start, end);
    }

    /**
     * For technical reasons we don't want empty lists in the generation info, so we need to deal with nulls
     * 
     * @param <T>
     * @param list current list to be updated
     * @param element element to be added
     * @param setter setter to replace null with list if required
     */
    private <T> void createOrUpdateList(List<T> list, T element, Consumer<List<T>> setter) {
        if (list == null) {
            list = new ArrayList<>();
            setter.accept(list);
        }
        list.add(element);

    }

    /**
     * take the first pattern only (convention is that the first one is the nicest one)
     * 
     * @param instance instruction to run
     * @param inputExpression expression built so far
     * @return list with created expression
     */
    private static List<SampleExpression> augmentWithFirstPattern(StatelessInstruction instance, SampleExpression inputExpression) {
        String pattern = instance.patterns[0];
        List<SampleExpression> res = new ArrayList<>();
        augmentWithPattern(instance, inputExpression, pattern, res);
        return res;
    }

    /**
     * Applies the given pattern with the given instruction to create an extended expression from the inputExpression
     * 
     * @param instance instruction
     * @param inputExpression source
     * @param pattern to be applied
     * @param res to put the result
     */
    private static void augmentWithPattern(StatelessInstruction instance, SampleExpression inputExpression, String pattern, List<SampleExpression> res) {
        if (!pattern.isEmpty()) {
            String newExpression = inputExpression.expression() + pattern;
            SampleExpression outputExpression = new SampleExpression(inputExpression.id(), inputExpression.label(), newExpression, inputExpression.invalid(),
                    inputExpression.composite(), inputExpression.skip(), inputExpression.generationInfo().copy());
            instance.updateGenInfo(outputExpression.generationInfo(), pattern);
            res.add(outputExpression);
        }
        else {
            res.add(inputExpression);
        }
    }

    /**
     * Randomly picks a pattern to apply it with the given instruction
     * 
     * @param instance instruction
     * @param inputExpression source
     * @param patterns to pick pattern from
     * @param outputLimit limit the number of variations, &lt;= 0 means one and skip the invalids
     * @return list of generated expressions
     */
    private static List<SampleExpression> augmentWithTokenSingleSelect(StatelessInstruction instance, SampleExpression inputExpression, String[] patterns,
            int outputLimit) {
        Random rand = GenDataUtils.createRandomWithSeed(inputExpression);
        String pattern = outputLimit < 1 ? patterns[0] : pickValue(patterns, rand);
        List<SampleExpression> res = new ArrayList<>();
        augmentWithPattern(instance, inputExpression, pattern, res);
        return instance.addInvalidExpressionIfAppropriate(inputExpression, rand, res, outputLimit < 1);
    }

    /**
     * Randomly picks a pattern from the instance's patterns to apply it with the given instruction
     * 
     * @param instance instruction
     * @param inputExpression source
     * @param outputLimit limit the number of variations, &lt;= 0 means one and skip the invalids
     * @return list of generated expressions
     */
    private static List<SampleExpression> augmentWithTokenSingleSelect(StatelessInstruction instance, SampleExpression inputExpression, int outputLimit) {
        return augmentWithTokenSingleSelect(instance, inputExpression, instance.patterns, outputLimit);
    }

    /**
     * Randomly picks some pattern(s) from the instance's patterns to apply them with the given instruction
     * 
     * @param instance instruction
     * @param inputExpression source
     * @param outputLimit limit the number of variations, &gt;=1
     * @return list of generated expressions
     */
    private static List<SampleExpression> augmentWithTokenMultiSelect(StatelessInstruction instance, SampleExpression inputExpression, int outputLimit) {
        Random rand = GenDataUtils.createRandomWithSeed(inputExpression);
        List<SampleExpression> res = new ArrayList<>();

        int multiplier = (inputExpression.invalid() || outputLimit <= 1) ? 1 : outputLimit;

        List<String> multiplierPatterns = new ArrayList<>();
        multiplierPatterns.add(instance.patterns[0]);
        if (multiplier > 1) {
            multiplierPatterns.addAll(pickValuesNoDuplicates(instance.patterns, rand, multiplier - 1));
        }

        for (String pattern : multiplierPatterns) {
            augmentWithPattern(instance, inputExpression, pattern, res);
        }
        return instance.addInvalidExpressionIfAppropriate(inputExpression, rand, res, outputLimit < 1);
    }

    /**
     * Picks whitespace or comments from the available patterns
     * 
     * @param instance instruction
     * @param inputExpression source
     * @param outputLimit limit the number of variations, &gt;=1
     * @return list of generated expressions
     */
    private static List<SampleExpression> augmentWithWhitespaceOrComment(StatelessInstruction instance, SampleExpression inputExpression, int outputLimit) {
        Random rand = GenDataUtils.createRandomWithSeed(inputExpression);

        boolean skipVariations = (outputLimit < 1);

        // default augmentation
        List<SampleExpression> res = augmentWithFirstPattern(instance, inputExpression);

        // round about every 10th occurrence shall be a comment
        if (rand.nextInt(100) < 10) {
            List<SampleExpression> subRes = COMMENT.apply(inputExpression);
            // approx. every 5th comment shall be a double comment
            if (!skipVariations && rand.nextInt(100) < 5) {
                subRes = COMMENT.apply(subRes.get(0));
            }
            res.addAll(subRes);
        }
        else if (!skipVariations) {
            // whitespace variation, excluding first pattern
            String[] patterns = Arrays.copyOfRange(instance.patterns, 1, instance.patterns.length);
            res.addAll(augmentWithTokenSingleSelect(instance, inputExpression, patterns, outputLimit));
        }
        return res;

    }

    /**
     * Appends a list with one or more elements (CSV) from the instance's samples
     * 
     * @param instance instruction
     * @param inputExpression source
     * @param outputLimit limit the number of variations, &gt;=1
     * @return list of generated expressions
     */
    private static List<SampleExpression> augmentWithTokenCSV(StatelessInstruction instance, SampleExpression inputExpression, int outputLimit) {
        Random rand = GenDataUtils.createRandomWithSeed(inputExpression);

        // list with <= 5 elements for CSV-creation
        List<String> listValues = pickValues(instance.patterns, rand, 5);

        StringBuilder sbNewExpression = new StringBuilder(inputExpression.expression());

        SampleGenInfo newInfo = inputExpression.generationInfo().copy();

        for (int i = 0; i < listValues.size(); i++) {
            if (i > 0) {
                occasionallyAppendWhitespaceOrComment(rand, sbNewExpression, newInfo);
                sbNewExpression.append(",");
                occasionallyAppendWhitespaceOrComment(rand, sbNewExpression, newInfo);
            }
            String value = listValues.get(i);
            sbNewExpression.append(value);
            instance.updateGenInfo(newInfo, value);
            if (instance == MIXED_LIST) {
                if (value.startsWith("@")) {
                    ARG_REF.updateGenInfo(newInfo, value);
                }
                else {
                    ARG_VALUE.updateGenInfo(newInfo, value);
                }
            }
            else {
                SNIPPET.updateGenInfo(newInfo, value);
            }
        }

        List<SampleExpression> res = Arrays.asList(new SampleExpression(inputExpression.id(), inputExpression.label(), sbNewExpression.toString(),
                inputExpression.invalid(), inputExpression.composite(), inputExpression.skip(), newInfo));

        return instance.addInvalidExpressionIfAppropriate(inputExpression, rand, res, outputLimit < 1);

    }

    /**
     * Randomly appends extra whitespace or a comment
     * 
     * @param rand for making a choice
     * @param sbNewExpression expression text to be extended
     * @param newInfo info to be updated in case we generate a comment
     */
    private static void occasionallyAppendWhitespaceOrComment(Random rand, StringBuilder sbNewExpression, SampleGenInfo newInfo) {
        if (rand.nextInt(100) < 10) {
            sbNewExpression.append("\n");
        }
        else if (rand.nextBoolean()) {
            sbNewExpression.append(" ");
        }
        if (rand.nextInt(100) < 10) {
            String comment = COMMENT.patterns[0];
            sbNewExpression.append(comment);
            COMMENT.updateGenInfo(newInfo, comment);
        }
    }

    /**
     * Add some invalid examples for negative testing based on the current expression if it is not already invalid
     * 
     * @param inputExpression source
     * @param rand for making choices
     * @param results list generated so fare
     * @param skipVariations if true skip the invalid examples
     * @return generated samples
     */
    private List<SampleExpression> addInvalidExpressionIfAppropriate(SampleExpression inputExpression, Random rand, List<SampleExpression> results,
            boolean skipVariations) {

        if (inputExpression.invalid() || skipVariations) {
            // don't add more invalid parts to already invalid expression or if there are otherwise too many variations
            return results;
        }

        List<String> invalidExpressions = new ArrayList<>();
        if (this == ARG_NAME) {
            invalidExpressions.add(inputExpression.expression());

            addBadPatterns(inputExpression, rand, invalidExpressions, new String[] { " ", "!", "\"", "\n", "\t", "\r", "\"", "\"\"", "\"\n\"" }, 2);
        }
        else if (this == ARG_VALUE || this == SNIPPET) {
            invalidExpressions.add(inputExpression.expression());
            addBadPatterns(inputExpression, rand, invalidExpressions, new String[] { " ", "!", "\"", "\n", "\t", "\r", "\"", "\"\n\"" }, 2);
        }
        else if (this == MIXED_LIST) {
            invalidExpressions.add(inputExpression.expression());
            addBadPatterns(inputExpression, rand, invalidExpressions, new String[] { " ", "a,,b", "a,", "a,b,", "a,,", ",", "\"a\",", ",\"a\"", ",a" }, 3);
        }
        else if (this == SNIPPET_LIST) {
            invalidExpressions.add(inputExpression.expression());
            addBadPatterns(inputExpression, rand, invalidExpressions, new String[] { " ", "a,,b", "a,", "a,@b,", "a,,", ",", "@\"a\",", ",@\"a\"", ",@a, b" },
                    3);
        }

        if (!invalidExpressions.isEmpty()) {
            results = new ArrayList<>(results);
            for (String invalidExpression : invalidExpressions) {
                SampleExpression newExpr = new SampleExpression(inputExpression.id(), inputExpression.label(), invalidExpression, true,
                        inputExpression.composite(), inputExpression.skip(), inputExpression.generationInfo().copy());
                results.add(newExpr);
            }
        }
        return results;
    }

    /**
     * Creates invalid variations of the inputExpression
     * 
     * @param inputExpression source
     * @param rand for making choices
     * @param invalidExpressions invalid expression texts (variations)
     * @param badPatternTemplates templates to pick bad patterns from
     * @param numberOfVariations
     */
    private void addBadPatterns(SampleExpression inputExpression, Random rand, List<String> invalidExpressions, String[] badPatternTemplates,
            int numberOfVariations) {

        List<String> badPatterns = pickValuesNoDuplicates(badPatternTemplates, rand, numberOfVariations);
        for (String badPattern : badPatterns) {
            invalidExpressions.add(inputExpression.expression() + badPattern);
        }

    }

    @Override
    public List<SampleExpression> apply(SampleExpression inputExpression, int outputLimit) {
        return instruction.apply(this, inputExpression, outputLimit);
    }

    /**
     * Internal constants class with all the available standard patterns
     */
    private static final class BasicPatterns {
        private static final String[] TPL_MANDATORY_WHITESPACE_OR_COMMENT = new String[] { " ", "\n", "\r\n", "\t", "\r", "   ", "\t\t\n\t" };
        private static final String[] TPL_OPTIONAL_WHITESPACE_OR_COMMENT = new String[] { "", " ", "\n", "\r\n", "\t", "\r", "   ", "\t\t\n\t" };
        private static final String[] TPL_COMMENT = new String[] { "/* comment */", "/* hugo */", "/* NOT */", "\n/**/\n", "\n/*AND (b=3)*/\n",
                "/*\\nsome comment\\n\\n\\n*/" };

        private static final String[] TPL_OR = new String[] { "OR", "or", "Or", "oR" };
        private static final String[] TPL_AND = new String[] { "AND", "and", "And", "AnD", "aND" };

        private static final String[] TPL_ALL = new String[] { "<ALL>", "<all>", "<All>", "<aLl>", "<AlL>" };
        private static final String[] TPL_NONE = new String[] { "<NONE>", "<none>", "<None>", "<nOnE>", "<NoNe>" };
        private static final String[] TPL_IS = new String[] { "IS", "is", "Is", "iS" };
        private static final String[] TPL_NOT = new String[] { "NOT", "not", "Not", "nOt", "nOT" };
        private static final String[] TPL_UNKNOWN = new String[] { "UNKNOWN", "unknown", "Unknown", "unKnown", "uNkNoWn" };
        private static final String[] TPL_OP = new String[] { "=", "<", "<=", "!=", ">=", ">" };
        private static final String[] TPL_STRICT = new String[] { "STRICT", "strict", "Strict", "sTriCt" };
        private static final String[] TPL_ANY = new String[] { "ANY", "any", "Any", "AnY", "aNY" };
        private static final String[] TPL_OF = new String[] { "OF", "of", "Of", "oF" };
        private static final String[] TPL_CONTAINS = new String[] { "CONTAINS", "contains", "Contains", "conTains", "cONTAInS" };
        private static final String[] TPL_BETWEEN = new String[] { "BETWEEN", "between", "Between", "betWeen", "bETWEEn" };
        private static final String[] TPL_CURB = new String[] { "CURB", "curb", "Curb", "curB", "cUrB" };

        private static final String[] TPL_ARG_NAME = new String[] { "argName", "a", "\"a\"", "\"argName\"", "\"argument name\"", "arg1", "arg2", "arg3", "arg4",
                "arg5", "arg6", "arg7", "arg8", "arg9", "arg10", "arg11", "arg12", "arg13", "arg14", "arg15", "arg16", "arg17", "arg18", "arg19", "arg20",
                "\"<ALL>\"", "\"<NONE>\"", "\"IS\"", "\"NOT\"", "\"BETWEEN\"", "\"STRICT\"", "\"UNKNOWN\"", "\"OF\"", "\"CONTAINS\"", "\"CURB\"",
                "\"name with quote (\"\")\"" };
        private static final String[] TPL_ARG_VALUE = new String[] { "value", "\"\"", "v", "\"v\"", "\"value\"", "val1", "val2", "val3", "val4", "val5", "val6",
                "val7", "val8", "val9", "val10", "val11", "val12", "val13", "val14", "val15", "val16", "val17", "val18", "val19", "val20", "\"some value\"",
                "\"<ALL>\"", "\"<NONE>\"", "\"IS\"", "\"NOT\"", "\"BETWEEN\"", "\"STRICT\"", "\"UNKNOWN\"", "\"OF\"", "\"CONTAINS\"", "\"CURB\"",
                "\"value with quote (\"\")\"" };
        private static final String[] TPL_ARG_REF = new String[] { "@argName", "@a", "@\"a\"", "@\"argName\"", "@\"argument name\"", "@arg1", "@arg2", "@arg3",
                "@arg4", "@arg5", "@arg6", "@arg7", "@arg8", "@arg9", "@arg10", "@arg11", "@arg12", "@arg13", "@arg14", "@arg15", "@arg16", "@arg17", "@arg18",
                "@arg19", "@arg20", "@\"<ALL>\"", "@\"<NONE>\"", "@\"IS\"", "@\"NOT\"", "@\"BETWEEN\"", "@\"STRICT\"", "@\"UNKNOWN\"", "@\"OF\"",
                "@\"CONTAINS\"", "@\"CURB\"", "@\"argref with quote (\"\")\"" };

        private static final String[] TPL_SNIPPET = new String[] { "foo", "f", "bar", "\"foo bar\"", "\\", "\"a=b\"", "\"OR\"", "brand\\sports", "C:\\programs",
                "\"!\"", "yeti", "Hugo", "Eliza", "blue", "red", "green", "toast", "\"snippet with quote (\"\")\"" };

        private static final String[] TPL_MIXED;
        static {
            List<String> all = new ArrayList<>(Arrays.asList(TPL_ARG_VALUE));
            all.addAll(Arrays.asList(TPL_ARG_REF));
            TPL_MIXED = all.toArray(new String[0]);
        }

        private static final String[] TPL_BOUND = new String[] { "1", "0", "10", "2", "3", "4", "5", "6", "7", "8", "9", "999" };

    }

}
