package com.example.assignmentno1.Q2;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.example.assignmentno1.R;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> contacts;
    private ArrayList<Contact> filteredContacts;
    private Context context;

    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        this.context = context;
        this.contacts = contacts;
        this.filteredContacts = new ArrayList<>(contacts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, parent, false);
        }

        Contact contact = filteredContacts.get(position);

        TextView name = convertView.findViewById(R.id.contact_name);
        TextView phone = convertView.findViewById(R.id.contact_phone);
        ImageView favorite = convertView.findViewById(R.id.fav_icon);
        ImageView delete = convertView.findViewById(R.id.delete_contact);

        name.setText(contact.getName());
        phone.setText(contact.getPhone());
        favorite.setImageResource(contact.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        favorite.setOnClickListener(view -> {
            contact.setFavorite(!contact.isFavorite());
            notifyDataSetChanged();
        });

        delete.setOnClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Contact")
                    .setMessage("Are you sure you want to delete this contact?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        contacts.remove(contact);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }

    public void filter(String query) {
        filteredContacts.clear();
        if (query.isEmpty()) {
            filteredContacts.addAll(contacts);
        } else {
            for (Contact contact : contacts) {
                if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredContacts.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }
}
