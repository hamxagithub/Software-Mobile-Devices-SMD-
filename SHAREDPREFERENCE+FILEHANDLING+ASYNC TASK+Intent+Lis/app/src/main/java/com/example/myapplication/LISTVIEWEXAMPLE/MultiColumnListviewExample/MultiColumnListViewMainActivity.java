package com.example.myapplication.LISTVIEWEXAMPLE.MultiColumnListviewExample;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MultiColumnListViewMainActivity extends AppCompatActivity {
ListView listView;
public ArrayList<HashMap<String,String>> list;
public static final String First_column="First";
    public static final String Second_column="Second";
    public static final String Third_column="Third";
    public static final String Forth_column="Forth";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multi_column_list_view_main);
        listView = findViewById(R.id.lstmulticolumn);
        LoadData();
        MyMultiColumnListAdapter adapter =
                new MyMultiColumnListAdapter(this,list);
        listView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void LoadData()
    {
        list = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> hashMap1=new HashMap<String,String>();
        hashMap1.put(First_column,"Ali");
        hashMap1.put(Second_column,"Ahmad");
        hashMap1.put(Third_column,"Hamza");
        hashMap1.put(Forth_column,"Asfand");
        list.add(hashMap1);
        HashMap<String,String>hashMap2 =new HashMap<String,String>();
        hashMap2.put(First_column,"Maria");
        hashMap2.put(Second_column,"Rukshana");
        hashMap2.put(Third_column,"Hadia");
        hashMap2.put(Forth_column,"Alizeh");
        list.add(hashMap2);
        HashMap<String,String>hashMap3 =new HashMap<String,String>();
        hashMap3.put(First_column,"Mariam");
        hashMap3.put(Second_column,"ALIA");
        hashMap3.put(Third_column,"Hafas");
        hashMap3.put(Forth_column,"Harram");
        list.add(hashMap3);
        HashMap<String,String>hashMap4 =new HashMap<String,String>();
        hashMap4.put(First_column,"Asad");
        hashMap4.put(Second_column,"Rabia");
        hashMap4.put(Third_column,"Hadia");
        hashMap4.put(Forth_column,"Alizeh");
        list.add(hashMap4);
    }
}