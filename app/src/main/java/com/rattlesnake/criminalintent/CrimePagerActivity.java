package com.rattlesnake.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CrimePagerActivity
        extends AppCompatActivity
        implements CrimeFragment.Callbacks {
    private static final String EXTRA_CRIME_ID = "com.rattlesnake.criminalintent.crime_uid";
    private static final String EXTRA_CHANGED_CRIMES = "changed_crimes";
    private static final String EXTRA_REMOVED_CRIMES = "removed_crimes";
    private List<Crime> mCrimes;
    private ViewPager mViewPager;
    private Set<UUID> mChangedCrimes;
    private Set<UUID> mRemovedCrimes;

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent i = new Intent(context, CrimePagerActivity.class);
        i.putExtra(EXTRA_CRIME_ID, crimeId);
        return i;
    }

    public static Set<UUID> getChangedCrimes(Intent data){
        @SuppressWarnings("unchecked")
        Set<UUID> changedCrimes = (Set<UUID>) data.getSerializableExtra(EXTRA_CHANGED_CRIMES);
        return changedCrimes;
    }

    public static Set<UUID> getRemovedCrimes(Intent data){
        @SuppressWarnings("unchecked")
        Set<UUID> removedCrimes = (Set<UUID>) data.getSerializableExtra(EXTRA_REMOVED_CRIMES);
        return removedCrimes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);


        UUID selectedCrimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mCrimes = CrimeLab.get(this).getCrimes();
        mChangedCrimes = new HashSet<>();
        mRemovedCrimes = new HashSet<>();
        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return CrimeFragment.newInstance(mCrimes.get(position).getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(selectedCrimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onCrimeChanged(UUID crimeId) {
        mChangedCrimes.add(crimeId);
    }

    @Override
    public void onCrimeRemoved(UUID crimeId) {
        mRemovedCrimes.add(crimeId);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        if (!mChangedCrimes.isEmpty()) {
            data.putExtra(EXTRA_CHANGED_CRIMES, (Serializable) mChangedCrimes);
        }
        if (!mRemovedCrimes.isEmpty()){
            data.putExtra(EXTRA_REMOVED_CRIMES, (Serializable) mRemovedCrimes);
        }
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}
