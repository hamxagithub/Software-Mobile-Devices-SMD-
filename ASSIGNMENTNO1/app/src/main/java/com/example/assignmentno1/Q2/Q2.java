package com.example.assignmentno1.Q2;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignmentno1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class Q2 extends AppCompatActivity {

    private ListView contactListView;
    private EditText searchBar;
    private FloatingActionButton addContactButton;
    private ContactAdapter adapter;
    private ArrayList<Contact> contactList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactListView = findViewById(R.id.contact_list);
        searchBar = findViewById(R.id.search_bar);
        addContactButton = findViewById(R.id.add_contact);

        contactList = new ArrayList<>();
        populateContacts();

        adapter = new ContactAdapter(this, contactList);
        contactListView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showContactDetails(position);
                return true;
            }
        });

        addContactButton.setOnClickListener(view -> {
            contactList.add(new Contact("New Contact", "000-000-0000", false));
            adapter.notifyDataSetChanged();
        });
    }

    private void populateContacts() {
        contactList.add(new Contact("Alice Johnson", "123-456-7890", false));
        contactList.add(new Contact("Bob Smith", "987-654-3210", true));
        contactList.add(new Contact("Charlie Brown", "555-123-4567", false));
    }

    private void showContactDetails(int position) {
        Contact contact = contactList.get(position);
        new AlertDialog.Builder(this)
                .setTitle(contact.getName())
                .setMessage("Phone: " + contact.getPhone())
                .setPositiveButton("OK", null)
                .show();
    }
}
