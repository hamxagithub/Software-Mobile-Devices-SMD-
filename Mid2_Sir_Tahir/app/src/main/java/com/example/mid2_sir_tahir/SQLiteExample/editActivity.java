package com.example.mid2_sir_tahir.SQLiteExample;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mid2_sir_tahir.R;

import java.util.HashMap;

public class editActivity extends AppCompatActivity {
    EditText firstName, secondName, phoneNumber, homeAddress, emailAddress;
    Button updateButton, deleteButton;
    DBQueries dbQueries;
    String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        // Initialize UI elements
        firstName = findViewById(R.id.editfirstname);
        secondName = findViewById(R.id.editsecondname);
        phoneNumber = findViewById(R.id.editphonenumber);
        emailAddress = findViewById(R.id.editemailaddress);
        homeAddress = findViewById(R.id.edithomeaddress);
        updateButton = findViewById(R.id.btnupdate);
        deleteButton = findViewById(R.id.btndelete);

        // Get Contact ID from Intent
        contactId = getIntent().getStringExtra("contact_id");
        dbQueries = new DBQueries(this);

        // Fetch and display existing contact data
        loadContactData();

        // Set onClickListener for Update Button
        updateButton.setOnClickListener(v -> updateContact());

        // Set onClickListener for Delete Button
        deleteButton.setOnClickListener(v -> deleteContact());

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadContactData() {
        HashMap<String, String> contact = dbQueries.getSingleRecord(contactId);
        if (contact != null) {
            firstName.setText(contact.get("firstName"));
            secondName.setText(contact.get("secondName"));
            phoneNumber.setText(contact.get("phoneNumber"));
            emailAddress.setText(contact.get("emailAddress"));
            homeAddress.setText(contact.get("homeaAddress"));
        } else {
            Toast.makeText(this, "Contact not found!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if contact doesn't exist
        }
    }

    private void updateContact() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("firstName", firstName.getText().toString());
        contentValues.put("secondName", secondName.getText().toString());
        contentValues.put("phoneNumber", phoneNumber.getText().toString());
        contentValues.put("emailAddress", emailAddress.getText().toString());
        contentValues.put("homeaAddress", homeAddress.getText().toString());

        int rowsUpdated = dbQueries.updateContact(contactId, contentValues);
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Contact Updated Successfully!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, "Failed to Update Contact", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteContact() {
        int rowsDeleted = dbQueries.deleteContact(contactId);
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Contact Deleted Successfully!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, "Failed to Delete Contact", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(editActivity.this, MainActivitySQLite.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
