package com.example.compilerconstruction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etInputCode;
    private RecyclerView rvTokens;
    private TokenAdapter tokenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInputCode = findViewById(R.id.et_input_code);
        rvTokens = findViewById(R.id.rv_tokens);
        Button btnLexicalAnalyzer = findViewById(R.id.btn_lexical_analyzer);
        Button btnSyntaxAnalyzer = findViewById(R.id.btn_syntax_analyzer);
        Button btnExit = findViewById(R.id.btn_exit);

        rvTokens.setLayoutManager(new LinearLayoutManager(this));
        tokenAdapter = new TokenAdapter();
        rvTokens.setAdapter(tokenAdapter);

        btnLexicalAnalyzer.setOnClickListener(v -> performLexicalAnalysis());
        btnSyntaxAnalyzer.setOnClickListener(v -> performSyntaxAnalysis());
        btnExit.setOnClickListener(v -> finish());
    }

    private void performLexicalAnalysis() {
        String inputCode = etInputCode.getText().toString().trim();
        if (inputCode.isEmpty()) {
            Toast.makeText(this, "Please enter code.", Toast.LENGTH_SHORT).show();
            return;
        }

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(inputCode);
        List<Token> tokenList = lexicalAnalyzer.getTokenList();

        tokenAdapter.setTokenList(tokenList);
    }

    private void performSyntaxAnalysis() {
        String inputCode = etInputCode.getText().toString().trim();
        if (inputCode.isEmpty()) {
            Toast.makeText(this, "Please enter code.", Toast.LENGTH_SHORT).show();
            return;
        }

        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(inputCode);
        boolean isValid = syntaxAnalyzer.recognizeSyntax();
        List<Token> tokenList = syntaxAnalyzer.getTokenList();

        tokenAdapter.setTokenList(tokenList);

        if (isValid) {
            Toast.makeText(this, "Syntax is correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Syntax error: " + syntaxAnalyzer.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
