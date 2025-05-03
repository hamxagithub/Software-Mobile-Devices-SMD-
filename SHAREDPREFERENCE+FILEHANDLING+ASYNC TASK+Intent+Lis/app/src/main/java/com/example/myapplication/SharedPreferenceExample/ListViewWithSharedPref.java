package com.example.myapplication.SharedPreferenceExample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

import java.util.ArrayList;

public class ListViewWithSharedPref extends AppCompatActivity {
ListView listView;
Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_view_with_shared_pref);
        listView=findViewById(R.id.lstsharedpref);
        btn = findViewById(R.id.btnsharedpref);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences= getSharedPreferences("MyFileNew",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("A","Ali");
        editor.putString("B","Ahmad");
        editor.putString("C","Zubair");
        editor.putString("D","Ijaz");
        editor.putString("E","Asad");
        editor.putString("F","Jabbar");
        editor.putString("G","Huzaifa");
        editor.putString("H","Sharaz");
        editor.apply();
    }

    public void Show_Data(View view) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("MyFileNew",0);
        String A=sharedPreferences.getString("A","");
        String B =sharedPreferences.getString("B","");
        String C =sharedPreferences.getString("C","");
        String D =sharedPreferences.getString("D","");
        String E =sharedPreferences.getString("E","");
        String F =sharedPreferences.getString("F","");
        String G =sharedPreferences.getString("G","");
        String H =sharedPreferences.getString("H","");
        ArrayList list= new ArrayList();
        list.add(A);
        list.add(B);
        list.add(C);
        list.add(D);
        list.add(E);
        list.add(F);
        list.add(G);
        list.add(H);

        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
    }
}