package com.example.a22f_3189_ai_6b.SQLiteExamples;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DbQueries extends SQLiteOpenHelper {
    public DbQueries(Context context){
        super(context, "ContactDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Query = "CREATE TABLE CONTACTS("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "firstName TEXT,"+
                "secondName TEXT,"+
                "phoneNumber TEXT,"+
                "emailAdress TEXT,"+
                "homeAdress TEXT)";
        db.execSQL(Query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void Insert(HashMap<String, String> hashMap) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contact = new ContentValues();
        contact.put("_id",hashMap.get("_id"));
        contact.put("firstName",hashMap.get("firstName"));
        contact.put("secondName",hashMap.get("secondName"));
        contact.put("phoneNumber",hashMap.get("phoneNumber"));
        contact.put("emailAddress",hashMap.get("emailAddress"));
        contact.put("homeAddress",hashMap.get("homeAddress"));
        db.insert("CONTACTS", null, contact);

    }

    public ArrayList<HashMap<String,String>>getAllContacts()
    {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

        String Query = "SELECT * FROM CONTACTS";
        Cursor cursor = db.rawQuery(Query,null);
        if(cursor.moveToFirst())
        {
            do {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("id",cursor.getString(0));
                hashMap.put("firstName",cursor.getString(1));
                hashMap.put("secondName",cursor.getString(2));
                hashMap.put("phoneNumber",cursor.getString(3));
                hashMap.put("emailAddress",cursor.getString(4));
                hashMap.put("homeAddress",cursor.getString(5));
                arrayList.add(hashMap);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }

    public HashMap<String, String> getSingleRecord(String id) {
        SQLiteDatabase db = getReadableDatabase();
        HashMap<String,String> hashMap = new HashMap<>();
        String Query = "SELECT* FROM CONTACTS WHERE _id="+id;
        Cursor cursor = db.rawQuery(Query,null);
        if (cursor.moveToFirst())
        {
            hashMap.put("_id",cursor.getString(0));
            hashMap.put("firstName", cursor.getString(1));
            hashMap.put("secondName", cursor.getString(2));
            hashMap.put("phoneNumber", cursor.getString(3));
            hashMap.put("emailAddress", cursor.getString(4));
            hashMap.put("homeAddress", cursor.getString(5));

        }
        return hashMap;
    }
}
