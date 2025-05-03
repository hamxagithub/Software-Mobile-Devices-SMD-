package com.example.a22f_3189_ai_6b.SharedPrefrenceExamples;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

import java.util.ArrayList;

public class ListViewWithSharedPref extends AppCompatActivity {
    SharedPreferences preferences;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_view_with_shared_pref);
        listView = findViewById(R.id.lstsharedpref);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences = getSharedPreferences("MyNewFile", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("A", "Ali");
        editor.putString("B", "Ahmad");
        editor.putString("C", "Ayesha");
        editor.putString("D", "Mariz");
        editor.putString("E", "Furkhnda");
        editor.putString("F", "Marina");
        editor.apply();
    }

    public void ShowOnListView(View view) {
        String A = preferences.getString("A", " ");
        String B = preferences.getString("B", " ");
        String C = preferences.getString("C", " ");
        String D = preferences.getString("D", " ");
        String E = preferences.getString("E", " ");
        String F = preferences.getString("F", " ");
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(A);
        arrayList.add(B);
        arrayList.add(C);
        arrayList.add(D);
        arrayList.add(E);
        arrayList.add(F);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);


    }
}