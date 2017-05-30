package com.rattlesnake.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rattlesnake.criminalintent.Crime;
import com.rattlesnake.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;


public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int solved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        return new Crime(uuidString)
            .setDate(new Date(date))
            .setTitle(title)
            .setSolved(solved != 0)
            .setSuspect(suspect);
    }
}
