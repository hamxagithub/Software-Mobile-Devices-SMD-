package com.example.myapplication.FIREBASE_WITHRECYCLERVIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDataFragment extends Fragment {
    private EditText editName, editTitle, editDescription;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_data, container, false);

        editName = view.findViewById(R.id.edit_name);
        editTitle = view.findViewById(R.id.edit_title);
        editDescription = view.findViewById(R.id.edit_description);
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://my-application-4e04a-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("data");

        btnSubmit.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            if (!name.isEmpty() && !title.isEmpty() && !description.isEmpty()) {
                String id = databaseReference.push().getKey();
                DataModel data = new DataModel(id, name, title, description);
                assert id != null;
                databaseReference.child(id).setValue(data);

                editName.setText("");
                editTitle.setText("");
                editDescription.setText("");

                Toast.makeText(getContext(), "Data added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

