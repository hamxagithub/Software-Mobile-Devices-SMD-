package com.example.a22f_3189_ai_6b.StaticFragmentExample;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.a22f_3189_ai_6b.R;

public class StaticFragmentMainActivity extends AppCompatActivity implements ContactsFragment.ListSelectionListener {
    public static String[] contactArray;
    public static String[] contactsDetailArray;

    ContactsDetails obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_static_fragment_main);

        contactArray = getResources().getStringArray(R.array.contacts);
        contactsDetailArray = getResources().getStringArray(R.array.contactsdetails);

        // Fragment initialization fix
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Contacts List Fragment ko add karo
        ContactsFragment contactsFragment = new ContactsFragment();
        transaction.replace(R.id.fragment_contacts_container, contactsFragment);

        // Contacts Detail Fragment ko add karo
        obj = new ContactsDetails();
        transaction.replace(R.id.fragment_details_container, obj);

        transaction.commit();
    }

    @Override
    public void onSelection(int position) {
        if (obj != null) {
            obj.contactIndex(position);
        }
    }
}
