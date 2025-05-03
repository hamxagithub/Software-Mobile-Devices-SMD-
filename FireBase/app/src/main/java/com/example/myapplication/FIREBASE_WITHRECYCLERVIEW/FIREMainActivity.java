package com.example.myapplication.FIREBASE_WITHRECYCLERVIEW;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class FIREMainActivity extends AppCompatActivity {

        Button btnAddData, btnShowData;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_firemain);


            btnAddData = findViewById(R.id.btn_add_data);
            btnShowData = findViewById(R.id.btn_show_data);

            btnAddData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddDataFragment addDataFragment = new AddDataFragment();
                    loadFragment(addDataFragment);
                }
            });
            btnShowData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowDataFragment showDataFragment = new ShowDataFragment();
                    loadFragment(showDataFragment);
                }
            });

            loadFragment(new ShowDataFragment());
        }

        private void loadFragment(Fragment fragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }


}
