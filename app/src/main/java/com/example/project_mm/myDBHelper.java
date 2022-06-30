package com.example.project_mm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDBHelper extends SQLiteOpenHelper {
    public myDBHelper(Context context){
        super(context, "groupDB", null, 1);    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE groupTBL (gName CHAR(60) PRIMARY KEY, gwifiName CHAR(60) ,mText CHAR(60), mText2 CHAR(60))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int nerVersion){
        db.execSQL("DROP TABLE IF EXISTS groupTBL");
        onCreate(db);
    }
}
