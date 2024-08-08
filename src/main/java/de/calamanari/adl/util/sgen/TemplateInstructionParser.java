//@formatter:off
/*
 * TemplateInstructionParser
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
import java.util.List;

/**
 * This parser turns the textual instructions from a sample expression template into executable tokens.
 * <ul>
 * <li><code>'~'</code> stands for optional whitespace.</li>
 * <li><code>'_'</code> stands for mandatory whitespace.</li>
 * <li><code>${TOKENNAME}</code> stands for either a {@link StatelessInstruction} or one of the expression inclusion instructions, see implementation of
 * {@link #createInstruction(String, String)} for details.
 * <ul>
 * <li>Some tokens can carry a payload following the token name separated by a colon. E.g., <code>${ARG_NAME:color}</code> specifies a generated argument name
 * with the exact value <i>color</i></li>
 * </ul>
 * </li>
 * <li>Any text not covered by the above rules will be copied to the generated expression as-is.</li>
 * </ul>
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class TemplateInstructionParser {

    /**
     * Parses the template expression text and returns a sequential list of instructions.
     * 
     * @param templateExpression
     * @return list of instructions
     */
    public List<TemplateInstruction> parse(String templateExpression) {

        List<TemplateInstruction> res = new ArrayList<>();

        StringBuilder currentPlainTextPart = new StringBuilder();

        int len = templateExpression.length();
        int idx = 0;
        while (idx < len) {

            char ch = templateExpression.charAt(idx);

            if (ch == '$' && idx < len - 1 && templateExpression.charAt(idx + 1) == '{') {
                int idxClose = handleInstructionToken(templateExpression, idx, res, currentPlainTextPart);
                idx = idxClose;

            }
            else if (ch == '~') {
                handlePendingPlainText(currentPlainTextPart, res);
                res.add(StatelessInstruction.OPTIONAL_WHITESPACE_OR_COMMENT);
            }
            else if (ch == '_') {
                handlePendingPlainText(currentPlainTextPart, res);
                res.add(StatelessInstruction.MANDATORY_WHITESPACE_OR_COMMENT);
            }
            else {
                currentPlainTextPart.append(ch);
            }
            idx++;
        }
        handlePendingPlainText(currentPlainTextPart, res);

        // must allow empty expression
        if (res.isEmpty()) {
            res.add(new AppendTextInstruction(""));
        }

        return res;

    }

    /**
     * @param currentPlainTextPart
     * @param res
     */
    private void handlePendingPlainText(StringBuilder currentPlainTextPart, List<TemplateInstruction> res) {
        if (!currentPlainTextPart.isEmpty()) {
            res.add(new AppendTextInstruction(currentPlainTextPart.toString()));
            currentPlainTextPart.setLength(0);
        }
    }

    /**
     * @param templateExpression
     * @param idx current position in the template expression text
     * @param res
     * @param currentPlainTextPart
     * @return position of the last character consumed from the template
     */
    private int handleInstructionToken(String templateExpression, int idx, List<TemplateInstruction> res, StringBuilder currentPlainTextPart) {
        handlePendingPlainText(currentPlainTextPart, res);
        String argument = null;

        int idxStart = idx + 2;
        int idxColon = templateExpression.indexOf(':', idxStart);
        int idxClose = templateExpression.indexOf('}', idxStart);
        int idxEnd = idxClose;
        if (idxColon > -1 && idxColon < idxClose) {
            argument = templateExpression.substring(idxColon + 1, idxClose).strip();
            idxEnd = idxColon;
        }

        if (idxClose > 0) {
            String instructionName = templateExpression.substring(idxStart, idxEnd);
            int overrideOutputLimit = -1;
            if (instructionName.endsWith("!")) {
                overrideOutputLimit = 0;
                instructionName = instructionName.substring(0, instructionName.length() - 1);
            }
            else if (instructionName.endsWith("*")) {
                // effectively unlimited
                overrideOutputLimit = 10_000;
                instructionName = instructionName.substring(0, instructionName.length() - 1);
            }
            TemplateInstruction instruction = createInstruction(instructionName, argument);

            validateInstruction(instruction, templateExpression, instructionName, argument);

            if (overrideOutputLimit >= 0) {
                instruction = new OutputLimitOverrideTemplateInstruction(instruction, overrideOutputLimit);
            }
            res.add(instruction);
        }
        else {
            throw new TemplateParseException("Instruction not closed ${... in " + templateExpression);
        }
        return idxClose;
    }

    /**
     * @param instruction
     * @param templateExpression
     * @param instructionName
     * @param argument
     */
    private void validateInstruction(TemplateInstruction instruction, String templateExpression, String instructionName, String argument) {
        if (instruction == null && argument != null) {
            throw new TemplateParseException("Usupported instruction in ${" + instructionName + ":" + argument + "} in expression " + templateExpression);
        }
        else if (instruction == null) {
            throw new TemplateParseException("Usupported instruction ${" + instructionName + "} in " + templateExpression);
        }

        if (!(instruction instanceof IdFilterReuseExpressionInstruction) && !(instruction instanceof AppendTextAsTokenInstruction) && argument != null) {
            throw new TemplateParseException("Usupported text constant instruction in ${" + instructionName + ":" + argument + "} in " + templateExpression);
        }
    }

    @SuppressWarnings("java:S3776")
    private TemplateInstruction createInstruction(String instructionName, String argument) {
        String normalized = instructionName.strip().toUpperCase();

        argument = argument == null ? "" : argument.strip();
        switch (normalized) {
        case "ALL":
            return StatelessInstruction.ALL;
        case "NONE":
            return StatelessInstruction.NONE;
        case "ARG_NAME":
            return argument.isEmpty() ? StatelessInstruction.ARG_NAME : new AppendTextAsTokenInstruction(StatelessInstruction.ARG_NAME, argument);
        case "ARG_VALUE":
            return argument.isEmpty() ? StatelessInstruction.ARG_VALUE : new AppendTextAsTokenInstruction(StatelessInstruction.ARG_VALUE, argument);
        case "ARG_REF":
            return argument.isEmpty() ? StatelessInstruction.ARG_REF : new AppendTextAsTokenInstruction(StatelessInstruction.ARG_REF, argument);
        case "IS":
            return StatelessInstruction.IS;
        case "NOT":
            return StatelessInstruction.NOT;
        case "UNKNOWN":
            return StatelessInstruction.UNKNOWN;
        case "OP":
            return argument.isEmpty() ? StatelessInstruction.OP : new AppendTextAsTokenInstruction(StatelessInstruction.OP, argument);
        case "STRICT":
            return StatelessInstruction.STRICT;
        case "ANY":
            return StatelessInstruction.ANY;
        case "OF":
            return StatelessInstruction.OF;
        case "MIXED_LIST":
            return StatelessInstruction.MIXED_LIST;
        case "CONTAINS":
            return StatelessInstruction.CONTAINS;
        case "SNIPPET":
            return argument.isEmpty() ? StatelessInstruction.SNIPPET : new AppendTextAsTokenInstruction(StatelessInstruction.SNIPPET, argument);
        case "SNIPPET_LIST":
            return StatelessInstruction.SNIPPET_LIST;
        case "BETWEEN":
            return StatelessInstruction.BETWEEN;
        case "BOUND":
            return argument.isEmpty() ? StatelessInstruction.BOUND : new AppendTextAsTokenInstruction(StatelessInstruction.BOUND, argument);
        case "COMMENT":
            return argument.isEmpty() ? StatelessInstruction.COMMENT : new AppendTextAsTokenInstruction(StatelessInstruction.COMMENT, argument);
        case "AND":
            return StatelessInstruction.AND;
        case "OR":
            return StatelessInstruction.OR;
        case "CURB":
            return StatelessInstruction.CURB;
        case "OPAQUE_EXPRESSION":
            return new ReuseOpaqueExpressionInstruction();
        case "COMPOSITE_EXPRESSION":
            return new ReuseCompositeExpressionInstruction();
        case "EXPRESSION":
            return argument.isEmpty() ? null : new IdFilterReuseExpressionInstruction(argument);
        default:
            return null;
        }
    }

}
