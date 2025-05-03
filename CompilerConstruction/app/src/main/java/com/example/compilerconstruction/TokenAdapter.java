package com.example.compilerconstruction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.TokenViewHolder> {

    private List<Token> tokenList = new ArrayList<>();

    public void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TokenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_token, parent, false);
        return new TokenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TokenViewHolder holder, int position) {
        Token token = tokenList.get(position);
        holder.tvLexeme.setText("Lexeme: " + token.getLexeme());
        holder.tvTokenName.setText("Token: " + token.getTokenName());
        holder.tvAttribute.setText("Attribute: " + token.getAttribute());
        holder.tvLineNumber.setText("Line: " + token.getLineNumber());
    }

    @Override
    public int getItemCount() {
        return tokenList.size();
    }

    public static class TokenViewHolder extends RecyclerView.ViewHolder {
        TextView tvLexeme, tvTokenName, tvAttribute, tvLineNumber;

        public TokenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLexeme = itemView.findViewById(R.id.tv_lexeme);
            tvTokenName = itemView.findViewById(R.id.tv_token_name);
            tvAttribute = itemView.findViewById(R.id.tv_attribute);
            tvLineNumber = itemView.findViewById(R.id.tv_line_number);
        }
    }
}
