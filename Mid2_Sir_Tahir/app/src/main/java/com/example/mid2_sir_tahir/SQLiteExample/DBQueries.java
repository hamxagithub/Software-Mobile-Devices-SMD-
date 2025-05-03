package com.example.mid2_sir_tahir.SQLiteExample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBQueries extends SQLiteOpenHelper {

    public  DBQueries(Context context)
    {
        super(context,"ContactsDB",null,1);
        Log.d("TAG","DATABASE IS CREATED");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
String Query="CREATE TABLE CONTACT1 ("+"_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
        "firstName TEXT,"+
        "secondName TEXT,"+
        "phoneNumber TEXT,"+
        "emailAddress TEXT,"+
        "homeaAdreess TEXT)";
db.execSQL(Query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void Insert(HashMap<String,String>contact)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("_id",contact.get("_id"));
        contentValues.put("firstName",contact.get("firstName"));
        contentValues.put("secondName",contact.get("secondName"));
        contentValues.put("phoneNumber",contact.get("phoneNumber"));
        contentValues.put("emailAddress",contact.get("emailAddress"));
        contentValues.put("homeaAddress",contact.get("homeaAddress"));
      long i =  db.insert("CONTACT1",null,contentValues);
        if(i!=-1)
        {
            Log.d("TAG","NEW CONTACT ADDES .....");
        }
        else {
            Log.d("TAG","Failed");
        }
    }
    public ArrayList<HashMap<String,String>> getAllContacts()
    {
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<HashMap<String,String>> allContacts=
                new ArrayList<HashMap<String,String>>();
        String Query = "SELECT * FROM CONTACT1";
        Cursor cursor=db.rawQuery(Query,null);
        if(cursor.moveToFirst())
        {
            do{
                HashMap<String,String>contact=new HashMap<>();
                contact.put("_id",cursor.getString(0));
                contact.put("firstName",cursor.getString(1));
                contact.put("secondName",cursor.getString(2));
                contact.put("phoneNumber",cursor.getString(3));
                contact.put("emailAddress",cursor.getString(4));
                contact.put("homeaAddress",cursor.getString(5));
                allContacts.add(contact);
            }while(cursor.moveToNext());
        }
        return allContacts;
    }
    public HashMap<String,String> getSingleRecord(String id)
    {
        SQLiteDatabase db=getReadableDatabase();
        HashMap<String,String> singleRecord=new HashMap<>();
        String Query = "SELECT * FROM CONTACT1 WHERE _id="+ id;
        Cursor cursor=db.rawQuery(Query,null);
        if(cursor.moveToFirst())
        {
            singleRecord.put("_id",cursor.getString(0));
            singleRecord.put("firstName",cursor.getString(1));
            singleRecord.put("secondName",cursor.getString(2));
            singleRecord.put("phoneNumber",cursor.getString(3));
            singleRecord.put("emailAddress",cursor.getString(4));
            singleRecord.put("homeaAddress",cursor.getString(5));

        }
        return singleRecord;
    }
    public int updateContact(String id, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update("CONTACT1", contentValues, "_id=?", new String[]{id});
    }
    public int deleteContact(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("CONTACT1", "_id=?", new String[]{id});
    }
}
