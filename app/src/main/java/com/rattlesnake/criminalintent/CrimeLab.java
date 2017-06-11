package com.rattlesnake.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.rattlesnake.criminalintent.database.CrimeBaseHelper;
import com.rattlesnake.criminalintent.database.CrimeCursorWrapper;
import com.rattlesnake.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mAppContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mAppContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime c) {
        String[] uuidToMatch = new String[]{c.getId().toString()};
        ContentValues values = getContentValues(c);
        String whereClause = CrimeTable.Cols.UUID + " = ?";
        mDatabase.update(CrimeTable.NAME, values, whereClause, uuidToMatch);
    }

    public void removeCrime(Crime c) {
        String[] uuidToMatch = new String[]{c.getId().toString()};
        String whereClause = CrimeTable.Cols.UUID + " = ?";
        mDatabase.delete(CrimeTable.NAME, whereClause, uuidToMatch);
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME, null,
                                        whereClause, whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        try (CrimeCursorWrapper cursor = queryCrimes(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {
        String whereClause = CrimeTable.Cols.UUID + " = ?";
        String[] whereArgs = new String[]{id.toString()};

        try (CrimeCursorWrapper cursor = queryCrimes(whereClause, whereArgs)) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }
    }

    public File getPhotoFile(Crime crime) {
        File externalFilesDir = mAppContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFileName());
    }
}
