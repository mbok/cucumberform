package io.stephub.expression.grammar;

import io.stephub.expression.ParseException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
        throw new ParseException("Failed to parse JSON at line " + line + ":" + charPositionInLine + " due to " + msg);
    }
}
