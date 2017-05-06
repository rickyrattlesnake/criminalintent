package com.rattlesnake.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String CRIME_ID_EXTRA = "com.rattlesnake.criminalintent.crime_uid";

    @Override
    protected Fragment createFragment(){
        UUID crimeId = (UUID) getIntent().getSerializableExtra(CRIME_ID_EXTRA);
        return CrimeFragment.newInstance(crimeId);
    }

    public static Intent createIntent(Context context, UUID crimeId){
        Intent i = new Intent(context, CrimeActivity.class);
        i.putExtra(CRIME_ID_EXTRA, crimeId);
        return i;
    }
}