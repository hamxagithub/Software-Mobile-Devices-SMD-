package com.example.a22f_3189_ai_6b.FragmentExamples;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.a22f_3189_ai_6b.R;

public class FragmentExampleOneMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fragment_example_one_main);

        Configuration configuration = getResources().getConfiguration();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LM_Fragment lmFragment = new LM_Fragment();
            transaction.replace(R.id.main, lmFragment);
        } else {
            PM_Fragment pmFragment = new PM_Fragment();
            transaction.replace(R.id.main, pmFragment);  
        }

        transaction.commit();
    }
}
