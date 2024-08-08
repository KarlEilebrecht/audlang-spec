//@formatter:off
/*
 * AntlrTestHelper
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * This test helper allows parsing an expression with a grammar without creating an individual implementation. This is very similar to what ANTLR's TestRig (aka
 * grun) does but with a different focus. Instead of visualizing the tree and showing diagnosis information this implementation collect information (See
 * {@link AntlrParseResult}) about tokens and executed rules that can be used in assertions to verify parsing expressions in unit tests.
 * 
 * @author <a href="mailto:Karl.Eilebrecht(a/t)calamanari.de">Karl Eilebrecht</a>
 */
public class AntlrTestHelper {

    private final Class<? extends Lexer> lexerClass;

    private final Class<? extends Parser> parserClass;

    private final String defaultStartRuleName;

    public AntlrTestHelper(Class<? extends Lexer> lexerClass, Class<? extends Parser> parserClass, String defaultStartRuleName) {

        if (defaultStartRuleName == null) {
            throw new IllegalArgumentException("defaultStartRuleName must not be null");
        }

        this.lexerClass = lexerClass;
        this.parserClass = parserClass;
        this.defaultStartRuleName = defaultStartRuleName;

    }

    public AntlrParseResult parse(String input, String startRuleName) {

        if (startRuleName == null) {
            startRuleName = defaultStartRuleName;
        }

        Lexer lexer = createInstance(lexerClass, CharStream.class);

        AntlrParseResult res = new AntlrParseResult();

        ANTLRErrorListener errorListener = new RecordingErrorListener(res);

        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        CharStream inputCharStream = CharStreams.fromString(input);
        lexer.setInputStream(inputCharStream);
        CommonTokenStream allTokens = new CommonTokenStream(lexer);

        allTokens.fill();

        Parser parser = createInstance(parserClass, TokenStream.class);

        Vocabulary vocabulary = parser.getVocabulary();

        for (Token token : allTokens.getTokens()) {
            String tokenName = vocabulary.getSymbolicName(token.getType());
            if (tokenName == null) {
                tokenName = vocabulary.getLiteralName(token.getType());
            }
            if (tokenName == null) {
                tokenName = token.toString();
            }
            res.tokenList.add(tokenName);
        }

        parser.setBuildParseTree(true);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.addParseListener(new DataCollectListener(res, parser.getRuleNames()));

        parser.setTokenStream(allTokens);

        parseInternal(parser, startRuleName);

        return res;
    }

    private ParserRuleContext parseInternal(Parser parser, String startRuleName) {
        try {
            Method startRuleMethod = parserClass.getMethod(startRuleName);
            return (ParserRuleContext) startRuleMethod.invoke(parser, (Object[]) null);
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | RuntimeException ex) {
            throw new RuntimeException(String.format("Could execute start rule '%s' on parser", startRuleName), ex);
        }

    }

    public AntlrParseResult parse(String input) {
        return parse(input, null);
    }

    private static <T, A> T createInstance(Class<T> resultClass, Class<A> argumentClass) {
        Constructor<T> constructor = resolveConstructor(resultClass, argumentClass);
        try {
            return constructor.newInstance((A) null);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | RuntimeException e) {
            throw new RuntimeException(
                    String.format("Unexpected error calling contructor of class %s with single argument %s", constructor.getDeclaringClass(), argumentClass));
        }

    }

    private static <T> Constructor<T> resolveConstructor(Class<T> constructorClass, Class<?> argumentClass) {
        try {
            return constructorClass.getConstructor(argumentClass);
        }
        catch (NoSuchMethodException | RuntimeException ex) {
            throw new RuntimeException(
                    String.format("Unexpected error resolving contructor of class %s with single argument %s", constructorClass, argumentClass));
        }
    }

    private static class DataCollectListener implements ParseTreeListener {

        private final AntlrParseResult parseResult;

        private final String[] ruleNames;

        public DataCollectListener(AntlrParseResult parseResult, String[] ruleNames) {
            this.parseResult = parseResult;
            this.ruleNames = ruleNames;
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            // not of interest
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
            // not of interest
        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            // not of interest
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            String ruleName = ruleNames[ctx.getRuleIndex()];

            if (ctx.getText() != null) {
                List<String> values = parseResult.ruleNameToValueMap.computeIfAbsent(ruleName, s -> new ArrayList<>());
                values.add(ctx.getText());
            }

        }

    }

    private static class RecordingErrorListener extends BaseErrorListener {

        private final AntlrParseResult parseResult;

        public RecordingErrorListener(AntlrParseResult parseResult) {
            this.parseResult = parseResult;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                throws ParseCancellationException {

            if (!parseResult.isError()) {
                parseResult.error = new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg, e);
            }
        }

    }
}
