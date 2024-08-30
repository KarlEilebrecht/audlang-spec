//@formatter:off
/*
 * AdlTextUtils
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

package de.calamanari.adl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is an implementation to deal with the Audlang language specific character escaping in argument names and values.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class AdlTextUtils {

    /**
     * List of characters that can only appear inside double-quoted text
     */
    private static final String RESERVED_AUDLANG_CHARS = " ()<>=,!/\"*";

    /**
     * Set with all spelling variations of the reserved Audlang language words
     */
    static final Set<String> RESERVED_LITERALS;
    static {
        String[] templates = new String[] { "AND", "OR", "STRICT", "NOT", "IS", "ANY", "OF", "BETWEEN", "CONTAINS", "CURB", "UNKNOWN" };

        List<String> literalVariations = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        for (String template : templates) {
            computeSpellingVariations(template, 0, sb, literalVariations);
        }
        RESERVED_LITERALS = Collections.unmodifiableSet(new HashSet<>(literalVariations));
    }

    /**
     * Takes the uppercase templates and computes all upper-case/lower-case variations for fast lookup
     * 
     * @param template
     * @param idx
     * @param sb
     * @param result
     */
    static void computeSpellingVariations(String template, int idx, StringBuilder sb, List<String> result) {

        if (idx == template.length()) {
            result.add(sb.toString());
        }
        else {
            sb.append(template.charAt(idx));
            computeSpellingVariations(template, idx + 1, sb, result);
            sb.setLength(sb.length() - 1);
            sb.append(Character.toLowerCase(template.charAt(idx)));
            computeSpellingVariations(template, idx + 1, sb, result);
            sb.setLength(sb.length() - 1);
        }

    }

    /**
     * Surrounds the given argument name, value or text snippet with double quotes and replaces any contained double quote (<code>&quot;</code>) by two
     * subsequent double-quotes (<code>&quot;</code>),<br/>
     * if required to conform with the Audlang specification for PLAIN_TEXT and QUOTED_TEXT.
     * 
     * @param input text to be quoted if necessary
     * @return input or input in double quotes
     * @throws IllegalArgumentException if called with null or unescaped special characters
     */
    public static String addDoubleQuotesIfRequired(String input) {
        if (input == null) {
            throw new IllegalArgumentException("null is not allowed, neither with nor without quotes");
        }

        ParserState state = new ParserState(input);

        if ((!input.isEmpty() && input.charAt(0) == '@') || RESERVED_LITERALS.contains(input)) {
            // Audlang TEXT_PLAIN must not start with the @-symbol
            // all Audlang literals must be printed in double quotes to avoid ambiguity
            state.modified = true;
        }

        while (state.inputIdx < input.length()) {
            char ch = input.charAt(state.inputIdx);
            if ((ch >= 0 && ch < 32) || ch == 127) {
                throw new IllegalArgumentException(
                        String.format("Special character %s detected (implementation error, escaping must happen beforehand), problematic text: %s",
                                Integer.toHexString(ch), input));
            }
            else if (ch == '"') {
                state.sb.append('"');
                state.sb.append('"');
                state.modified = true;
            }
            else {
                state.modified = state.modified || RESERVED_AUDLANG_CHARS.indexOf(ch) > -1;
                state.sb.append(ch);
            }
            state.inputIdx++;
        }
        if (state.modified) {
            state.sb.insert(0, '"');
            state.sb.append('"');
        }
        return state.getOutput();
    }

    /**
     * Surrounds the given argument name, value or text snippet with double quotes and replaces any contained double quote (<code>&quot;</code>) by two
     * subsequent double-quotes (<code>&quot;</code>),<br/>
     * if required to conform with the Audlang specification for PLAIN_TEXT and QUOTED_TEXT.
     * <p/>
     * <b>Notes:</b> This method returns immediately if the first character is not a double-quote.
     * 
     * @param input text to remove surrounding double-quotes and unescape internal double-quotes
     * @return input or input in double quotes
     * @throws IllegalArgumentException if called with null or unescaped special characters, or on the first non-escaped single double-quote character before
     *             the end of the String.
     */
    public static String removeDoubleQuotesIfRequired(String input) {
        if (input == null) {
            throw new IllegalArgumentException("null is not allowed, neither with nor without quotes");
        }

        if (input.isEmpty() || input.charAt(0) != '"') {
            return input;
        }

        if (input.length() == 1 || input.charAt(input.length() - 1) != '"') {
            throw new IllegalArgumentException(
                    String.format("Unexpected input, expecting '\"...\"', found '\"...' (implementation error), problematic text: '%s'", input));
        }

        ParserState state = new ParserState(input);
        state.modified = true;
        state.inputIdx = 1;

        while (state.inputIdx < input.length() - 1) {
            char ch = input.charAt(state.inputIdx);
            if ((ch >= 0 && ch < 32) || ch == 127) {
                throw new IllegalArgumentException(
                        String.format("Special character %s detected (implementation error, unescaped text is not expected here), problematic text: %s",
                                Integer.toHexString(ch), input));
            }
            else if (ch == '"') {
                if (state.inputIdx == input.length() - 2) {
                    throw new IllegalArgumentException(String
                            .format("Unescaped single '\"' detected (implementation error, unescaped text is not expected here), problematic text: %s", input));
                }
                state.sb.append('"');
                // skip the next character
                state.inputIdx++;
            }
            else {
                state.sb.append(ch);
            }
            state.inputIdx++;
        }

        return state.getOutput();
    }

    /**
     * Escapes all special characters which are not allowed in Audlang argument names or values.
     * <p/>
     * <b>Note:</b> This method is <i><b>not</b> idempotent</i>, means: <code>escapeSpecialCharacters(escapeSpecialCharacters(input))</code> may not always
     * equal <code>escapeSpecialCharacters(input)</code>.
     * 
     * @param input arbitray text, not null
     * @return escaped input
     */
    public static String escapeSpecialCharacters(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Cannot escape null string");
        }

        ParserState state = new ParserState(input);

        while (state.inputIdx < input.length()) {
            char ch = input.charAt(state.inputIdx);
            if (ch == '<') {
                state.candidateStartIdx = state.sb.length();
                state.sb.append(ch);
            }
            else if (ch == '>') {
                state.sb.append(ch);
                if (escapePotentialEscapeSequence(state)) {
                    state.candidateStartIdx = -1;
                    state.modified = true;
                }
            }
            else if (ch >= SpecialCharacter.MIN_CHARACTER_CODE && ch <= SpecialCharacter.MAX_CHARACTER_CODE) {
                processSpecialCharacterCandidate(state, ch);
            }
            else {
                state.sb.append(ch);
            }
            state.inputIdx++;
        }
        return state.getOutput();
    }

    /**
     * Unescapes special characters previously escaped with {@link #escapeSpecialCharacters(String)}
     * <p/>
     * <b>Note:</b> This method is <i><b>not</b> idempotent</i>, means: <code>unescapeSpecialCharacters(unescapeSpecialCharacters(input))</code> may not always
     * equal <code>unescapeSpecialCharacters(input)</code>.
     * 
     * @param input arbitrary text, not null
     * @return unescaped input
     */
    public static String unescapeSpecialCharacters(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Cannot unescape null string");
        }

        ParserState state = new ParserState(input);

        while (state.inputIdx < input.length()) {
            char ch = input.charAt(state.inputIdx);
            if (ch == '<') {
                state.candidateStartIdx = state.sb.length();
                state.sb.append(ch);
            }
            else if (ch == '>') {
                state.sb.append(ch);
                if (unescapePotentialEscapeSequence(state)) {
                    state.candidateStartIdx = -1;
                    state.modified = true;
                }
            }
            else {
                state.sb.append(ch);
            }
            state.inputIdx++;
        }
        return state.getOutput();
    }

    /**
     * Replaces a special character candidate with its escape sequence if applicable and takes care of preceding backslashes
     * 
     * @param state
     * @param ch the candidate character
     */
    private static void processSpecialCharacterCandidate(ParserState state, char ch) {
        SpecialCharacter spc = SpecialCharacter.resolve(ch);
        if (spc != null) {
            escapeTrailingBackspaceCharacters(state.sb);
            state.sb.append("<");
            state.sb.append(spc.name());
            state.sb.append(">");
            state.modified = true;
            state.candidateStartIdx = -1;
        }
        else {
            state.sb.append(ch);
        }
    }

    /**
     * The current output could end with an escape sequence that must be escaped itself
     * 
     * @param state
     * @return true if the output was modified
     */
    private static boolean escapePotentialEscapeSequence(ParserState state) {

        boolean modified = false;
        if (state.candidateStartIdx > -1) {

            int candidateNameLen = state.sb.length() - (state.candidateStartIdx + 2);

            if (candidateNameLen > 0 && candidateNameLen <= SpecialCharacter.MAX_NAME_LENGTH) {
                String candidateName = state.sb.substring(state.candidateStartIdx + 1, state.candidateStartIdx + 1 + candidateNameLen);
                if (SpecialCharacter.resolve(candidateName) != null) {
                    String esc = state.sb.substring(state.candidateStartIdx);
                    state.sb.setLength(state.candidateStartIdx);
                    escapeTrailingBackspaceCharacters(state.sb);
                    state.sb.append('\\');
                    state.sb.append(esc);
                    modified = true;
                }
            }
        }
        return modified;
    }

    /**
     * The current output could end with an an escape sequence to be unescaped
     * 
     * @param state
     * @return true if an escape sequence has been detected and replaced
     */
    private static boolean unescapePotentialEscapeSequence(ParserState state) {
        boolean modified = false;
        if (state.candidateStartIdx > -1) {

            int candidateNameLen = state.sb.length() - (state.candidateStartIdx + 2);

            if (candidateNameLen > 0 && candidateNameLen <= SpecialCharacter.MAX_NAME_LENGTH) {
                String candidateName = state.sb.substring(state.candidateStartIdx + 1, state.candidateStartIdx + 1 + candidateNameLen);

                SpecialCharacter candidate = SpecialCharacter.resolve(candidateName);

                if (candidate != null) {
                    processCandidate(state, candidate);
                    modified = true;
                }
            }
        }
        return modified;
    }

    /**
     * Replaces the sequence with the corresponding character if the sequence itself is not back-slash-escaped.
     * 
     * @param state
     * @param candidate
     */
    private static void processCandidate(ParserState state, SpecialCharacter candidate) {
        String esc = state.sb.substring(state.candidateStartIdx);
        state.sb.setLength(state.candidateStartIdx);
        int numberOfTrailingBackspaces = countTrailingBackspaceCharacters(state.sb);
        unescapeTrailingBackspaceCharacters(state.sb);
        if (numberOfTrailingBackspaces % 2 == 0) {
            // even number means that the backslashes are plain backslashes
            // and the escape sequence should be replaced by its character
            state.sb.append((char) candidate.code);
        }
        else {
            // escaped escape sequence, just copy the text
            state.sb.append(esc);
        }
    }

    /**
     * @param sb
     * @return number of trailing backspace characters in sb
     */
    private static int countTrailingBackspaceCharacters(StringBuilder sb) {
        int trailingBackslashCount = 0;

        for (int i = sb.length() - 1; i > -1; i--) {
            if (sb.charAt(i) == '\\') {
                trailingBackslashCount++;
            }
            else {
                break;
            }
        }
        return trailingBackslashCount;
    }

    /**
     * Doubles all trailing backslashes in the given String
     * 
     * @param sb to be modified
     */
    private static void escapeTrailingBackspaceCharacters(StringBuilder sb) {

        int trailingBackslashCount = countTrailingBackspaceCharacters(sb);

        if (trailingBackslashCount > 0) {
            sb.setLength(sb.length() - trailingBackslashCount);
            for (int i = 0; i < trailingBackslashCount; i++) {
                sb.append("\\\\");
            }
        }
    }

    /**
     * Reduces pairs of trailing backslashes to single backspaces in the given String
     * 
     * @param sb to be modified
     */
    private static void unescapeTrailingBackspaceCharacters(StringBuilder sb) {
        int trailingBackslashCount = countTrailingBackspaceCharacters(sb);

        if (trailingBackslashCount > 0) {
            // removing half of the (backslashes +1) from the end
            // 1 - (2/2) = 0
            // 2 - (3/2) = 1
            // 3 - (4/2) = 1
            // 4 - (5/2) = 2
            // etc.
            sb.setLength(sb.length() - ((trailingBackslashCount + 1) / 2));
        }

    }

    /**
     * supplementary class containing the runtime information of a parse run
     */
    private static class ParserState {

        final String input;

        /**
         * current position in the input
         */
        int inputIdx = 0;

        /**
         * potential escape sequence, this is the start index in {@link #sb}
         */
        int candidateStartIdx = -1;

        /**
         * true if output does not equal input
         */
        boolean modified = false;

        ParserState(String input) {
            this.input = input;
        }

        /**
         * output produced so far
         */
        StringBuilder sb = new StringBuilder();

        String getOutput() {
            return modified ? sb.toString() : input;
        }

    }

    private AdlTextUtils() {
        // utility
    }

    /**
     * Enumeration with all special characters not allowed in any Audlang argument name, value or text snippet
     */
    public enum SpecialCharacter {

        NUL(0),
        SOH(1),
        STX(2),
        ETX(3),
        EOT(4),
        ENQ(5),
        ACK(6),
        BEL(7),
        BS(8),
        HT(9),
        LF(10),
        VT(11),
        FF(12),
        CR(13),
        SO(14),
        SI(15),
        DLE(16),
        DC1(17),
        DC2(18),
        DC3(19),
        DC4(20),
        NAK(21),
        SYN(22),
        ETB(23),
        CAN(24),
        EM(25),
        SUB(26),
        ESC(27),
        FS(28),
        GS(29),
        RS(30),
        US(31),
        DEL(127);

        /**
         * decimal character code
         */
        public final int code;

        /**
         * fast lookup name to instance
         */
        private static final Map<String, SpecialCharacter> nameToInstanceMap;
        static {
            Map<String, SpecialCharacter> map = new HashMap<>();
            Stream.of(SpecialCharacter.values()).forEach(c -> map.put(c.name(), c));
            nameToInstanceMap = Collections.unmodifiableMap(map);
        }

        /**
         * An array for fast lookup of instances by code
         */
        private static final SpecialCharacter[] quickCodeLookup;
        static {
            int size = Stream.of(SpecialCharacter.values()).map(c -> c.code).collect(Collectors.summarizingInt(Integer::intValue)).getMax() + 1;
            SpecialCharacter[] arr = new SpecialCharacter[size];
            Stream.of(SpecialCharacter.values()).forEach(c -> arr[c.code] = c);
            quickCodeLookup = arr;
        }

        /**
         * Minimum unsupported character code
         */
        public static final int MIN_CHARACTER_CODE;

        /**
         * Maximum unsupported character code
         */
        public static final int MAX_CHARACTER_CODE;
        static {
            IntSummaryStatistics stats = Stream.of(SpecialCharacter.values()).map(c -> c.code).collect(Collectors.summarizingInt(Integer::intValue));

            MIN_CHARACTER_CODE = stats.getMin();
            MAX_CHARACTER_CODE = stats.getMax();

        }

        /**
         * maximum length of any if the names (for quickly filtering candidates)
         */
        public static final int MAX_NAME_LENGTH;
        static {
            MAX_NAME_LENGTH = Stream.of(SpecialCharacter.values()).map(c -> c.name().length()).collect(Collectors.summarizingInt(Integer::intValue)).getMax();

        }

        /**
         * @param code decimal character code
         */
        private SpecialCharacter(int code) {
            this.code = code;
        }

        /**
         * @param name
         * @return the {@link SpecialCharacter} for the name or null if unknown
         */
        public static SpecialCharacter resolve(String name) {
            return nameToInstanceMap.get(name);
        }

        /**
         * @param characterCode
         * @return the {@link SpecialCharacter} for the code or null if unknown
         */
        public static SpecialCharacter resolve(int characterCode) {
            return (characterCode > -1 && characterCode < quickCodeLookup.length) ? quickCodeLookup[characterCode] : null;
        }

    }
}
