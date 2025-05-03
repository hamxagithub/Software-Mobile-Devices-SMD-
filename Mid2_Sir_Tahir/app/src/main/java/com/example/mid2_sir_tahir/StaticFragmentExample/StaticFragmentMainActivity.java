package com.example.mid2_sir_tahir.StaticFragmentExample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.mid2_sir_tahir.R;

public class StaticFragmentMainActivity extends AppCompatActivity implements ContactsFragment.ListSelectionListener {
    public static String[] contactsArray;
    public static String[] contactsDetailArray;
    DetailsFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_fragment_main);

        contactsArray = getResources().getStringArray(R.array.contacts_array);
        contactsDetailArray = getResources().getStringArray(R.array.detials_array);

        detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.contacts_detail_fragment);

    }

    @Override
    public void onSelection(int position) {
 if(detailsFragment.getCurrentIndex()!=position)
 {
     detailsFragment.ContactIndex(position);
 }
    }
}
