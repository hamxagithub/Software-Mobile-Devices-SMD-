package com.example.cardviewfilehandling_mam.DATABASE;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cardviewfilehandling_mam.R;

public class MainActivity1 extends AppCompatActivity {
      Cursor cursor;
    DBClass dbClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main1);
        dbClass=new DBClass(this);
        dbClass.addinfo("Hamza","647657");
        dbClass.addinfo("Ahmad","12345");
        dbClass.addinfo("Akram","12345");
        dbClass.addinfo("Saleem","12345");
        dbClass.addinfo("Muraad","12345");

        cursor = dbClass.getAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show(); } else {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);
                Log.d("DB_FETCH", "ID: " + id + ", Name: " + name + ", Phone: " + phone);
            }
        }
        boolean isUpdated = dbClass.updateData(3, "ALIA", "1234567890"); if (isUpdated) {
            Log.d("DB_UPDATE", "Data updated successfully!");
        } else {
            Log.d("DB_UPDATE", "Failed to update data.");
        }
        boolean isDeleted = dbClass.deleteData();

        if (isDeleted) {
            Log.d("DB_DELETE", "All data deleted successfully!");
        } else {
            Log.d("DB_DELETE", "Failed to delete all data.");
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}