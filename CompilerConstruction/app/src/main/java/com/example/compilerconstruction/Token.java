package com.example.compilerconstruction;

public class Token {
    private String lexeme;
    private String tokenName;
    private String attribute;
    private int lineNumber;

    public Token(String lexeme, String tokenName, String attribute, int lineNumber) {
        this.lexeme = lexeme;
        this.tokenName = tokenName;
        this.attribute = attribute;
        this.lineNumber = lineNumber;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getAttribute() {
        return attribute;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
