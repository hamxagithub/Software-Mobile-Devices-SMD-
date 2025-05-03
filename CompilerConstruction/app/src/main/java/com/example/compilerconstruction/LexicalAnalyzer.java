package com.example.compilerconstruction;

import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {
    private String sourceCode;
    private List<Token> tokensList;

    public LexicalAnalyzer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.tokensList = new ArrayList<>();
        analyzeTokens();
    }

    private void analyzeTokens() {
        String[] words = sourceCode.split("\\s+");
        int lineNumber = 1;

        for (String word : words) {
            if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tokensList.add(new Token(word, "Identifier", "Variable", lineNumber));
            } else if (word.matches("\\d+")) {
                tokensList.add(new Token(word, "Number", "Constant", lineNumber));
            } else if (word.equals(";")) {
                tokensList.add(new Token(word, "Semicolon", "Delimiter", lineNumber));
            } else {
                tokensList.add(new Token(word, "Error", "Unrecognized Token", lineNumber));
            }
        }
    }

    public List<Token> getTokenList() {
        return tokensList;
    }
}
