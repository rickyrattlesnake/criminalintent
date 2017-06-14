package com.rattlesnake.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeListActivity extends SingleFragmentActivity
    implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_master_detail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent i = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(i);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit();
        }
    }

    @Override
    public void onCrimeChanged(UUID crimeId) {
        CrimeListFragment clf = (CrimeListFragment) getSupportFragmentManager()
            .findFragmentById(R.id.fragment_container);
        clf.updateUI();
    }

    @Override
    public void onCrimeRemoved(UUID crimeId) {
        if (findViewById(R.id.detail_fragment_container) != null) {
            CrimeFragment detail = (CrimeFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment_container);
            getSupportFragmentManager().beginTransaction()
                .remove(detail)
                .commit();
        }

        CrimeListFragment clf = (CrimeListFragment) getSupportFragmentManager()
            .findFragmentById(R.id.fragment_container);
        clf.updateUI();
    }
}
