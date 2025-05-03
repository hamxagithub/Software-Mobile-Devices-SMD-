package com.example.mid2_sir_tahir.FRAGMNETEXAMPLES;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mid2_sir_tahir.R;

public class FragmentExampleOneMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fragment_example_one_main);
        Configuration config = getResources().getConfiguration();


        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        if(config.orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            LM_Fragment lmFragment=new LM_Fragment();
            fragmentTransaction.replace(android.R.id.content,lmFragment);
        }
        else {
            PM_Fragment pmFragment=new PM_Fragment();
            fragmentTransaction.replace(android.R.id.content,pmFragment);
        }
        fragmentTransaction.commit();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}