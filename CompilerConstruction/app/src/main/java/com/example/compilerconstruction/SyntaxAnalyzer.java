package com.example.compilerconstruction;

import java.util.List;

public class SyntaxAnalyzer {
    private LexicalAnalyzer lexicalAnalyzer;
    private String error;

    public SyntaxAnalyzer(String sourceCode) {
        this.lexicalAnalyzer = new LexicalAnalyzer(sourceCode);
        this.error = "";
    }

    public boolean recognizeSyntax() {
        List<Token> tokens = lexicalAnalyzer.getTokenList();
        if (tokens.isEmpty()) {
            error = "Syntax Error: No tokens found.";
            return false;
        }
        return true;
    }

    public List<Token> getTokenList() {
        return lexicalAnalyzer.getTokenList();
    }

    public String getError() {
        return error;
    }
}
