package com.rattlesnake.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rattlesnake.criminalintent.database.CrimeBaseHelper;
import com.rattlesnake.criminalintent.database.CrimeCursorWrapper;
import com.rattlesnake.criminalintent.database.CrimeDbSchema;
import com.rattlesnake.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime c){
        String[] uuidToMatch = new String[] { c.getId().toString() };
        ContentValues values = getContentValues(c);
        String whereClause = CrimeTable.Cols.UUID + " = ?";
        mDatabase.update(CrimeTable.NAME, values, whereClause, uuidToMatch);
    }

    public void removeCrime(Crime c) {
        String[] uuidToMatch = new String[] { c.getId().toString() };
        String whereClause = CrimeTable.Cols.UUID + " = ?";
        mDatabase.delete(CrimeTable.NAME, whereClause, uuidToMatch);
    }

    private CrimeLab(Context context){
        mAppContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mAppContext).getWritableDatabase();
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor =  mDatabase.query(CrimeTable.NAME, null,
                                         whereClause, whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        return values;
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {
        String whereClause = CrimeTable.Cols.UUID + " = ?";
        String[] whereArgs = new String[] { id.toString() };

        CrimeCursorWrapper cursor = queryCrimes(whereClause, whereArgs);

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }
}
