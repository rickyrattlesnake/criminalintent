package com.rattlesnake.criminalintent.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rattlesnake.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context c) {
        super(c, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String execQuery = String.format(
            "create table %s ( _id integer primary key autoincrement, %s, %s, %s, %s)",
            CrimeTable.NAME,
            CrimeTable.Cols.UUID,
            CrimeTable.Cols.TITLE,
            CrimeTable.Cols.DATE,
            CrimeTable.Cols.SOLVED
        );
        db.execSQL(execQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
