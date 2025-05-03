package com.example.a22f_3189_ai_6b.ListViewExamples;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a22f_3189_ai_6b.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MulticolumnListViewExample extends AppCompatActivity {
    ListView listView;
    public ArrayList<HashMap<String,String>> list;
    public static final String First_Column = "First";
    public static final String Second_Column= "Senond";
    public static final String Third_Column= "Third";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multicolumn_list_view_example);
        listView = findViewById(R.id.lstmulticolumnlistview);
        LoadData();
        MultiColumnAdapter adapter = new MultiColumnAdapter(this,list);
        listView.setAdapter(adapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void LoadData()
    {
        list = new ArrayList<HashMap<String,String>>();  //type
        HashMap<String,String> hashMap1 = new HashMap<String ,String>();
        hashMap1.put(First_Column,"Ali");
        hashMap1.put(Second_Column,"Ahmad");
        hashMap1.put(Third_Column,"Basit"); //key me store hoti ha aray lisy ko dena ha
        list.add(hashMap1);
        HashMap<String,String> hashMap2 = new HashMap<String ,String>();
        hashMap2.put(First_Column,"Wahab");
        hashMap2.put(Second_Column,"Nasir");
        hashMap2.put(Third_Column,"Burhan"); //key me store hoti ha aray lisy ko dena ha
        list.add(hashMap2);
        HashMap<String,String> hashMap3 = new HashMap<String ,String>();
        hashMap3.put(First_Column,"Wajid");
        hashMap3.put(Second_Column,"Raza");
        hashMap3.put(Third_Column,"Awaise"); //key me store hoti ha aray lisy ko dena ha
        list.add(hashMap3);
        HashMap<String,String> hashMap4 = new HashMap<String ,String>();
        hashMap4.put(First_Column,"Wasif");
        hashMap4.put(Second_Column,"Sumair");
        hashMap4.put(Third_Column,"Mia"); //key me store hoti ha aray lisy ko dena ha
        list.add(hashMap4);
        HashMap<String,String> hashMap5 = new HashMap<String ,String>();
        hashMap5.put(First_Column,"Uncle");
        hashMap5.put(Second_Column,"Chori");
        hashMap5.put(Third_Column,"Chaka"); //key me store hoti ha aray lisy ko dena ha
        list.add(hashMap5);
    }
}