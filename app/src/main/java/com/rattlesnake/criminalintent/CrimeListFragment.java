package com.rattlesnake.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private LinearLayout mEmptyLayout;
    private boolean mSubtitleVisible;
    private static int CRIME_REQUEST_CODE = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedState){
        View listView = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mEmptyLayout = (LinearLayout) listView.findViewById(R.id.crime_list_empty_layout);
        Button addCrimeButton = (Button) listView.findViewById(R.id.crime_list_add_button);
        addCrimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createNewCrime();
            }
        });

        mCrimeRecyclerView = (RecyclerView) listView.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedState != null) {
            mSubtitleVisible = savedState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return listView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == CRIME_REQUEST_CODE) {
            Set<UUID> changedCrimeIds = CrimePagerActivity.getChangedCrimes(data);
            Set<UUID> removedCrimeIds = CrimePagerActivity.getRemovedCrimes(data);
            if (changedCrimeIds != null) {
                for (UUID crimeId : changedCrimeIds){
                    mAdapter.notifyCrimeChanged(crimeId);
                }
            }
            if (removedCrimeIds != null){
                for (UUID crimeId : removedCrimeIds){
                    mAdapter.notifyCrimeRemoved(crimeId);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_new_crime:
                createNewCrime();
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewCrime(){
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
        startActivityForResult(intent, CRIME_REQUEST_CODE);
    }

    private void updateSubtitle(){
        int crimeCount = CrimeLab.get(getActivity()).getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (crimes.isEmpty()){
            mCrimeRecyclerView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }

        if (mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
                              implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;

        CrimeHolder(View itemView){
            super(itemView);

            mTitleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView
                    .findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView
                    .findViewById(R.id.list_item_crime_solved_check_box);

            itemView.setOnClickListener(this);
        }

        void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getFormattedDate());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
            mSolvedCheckBox.setEnabled(false);
        }

        @Override
        public void onClick(View v){
            Intent i = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(i, CRIME_REQUEST_CODE);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        void notifyCrimeChanged(UUID crimeId){
            int position = findCrimePosition(crimeId);
            if (position != -1){
                notifyItemChanged(position);
            }
        }

        void notifyCrimeRemoved(UUID crimeId){
            notifyDataSetChanged();
        }

        private int findCrimePosition(UUID crimeId){
            for (int i = 0; i < mCrimes.size(); i++){
                Crime c = mCrimes.get(i);
                if (c.getId().equals(crimeId)){
                    return i;
                }
            }
            return -1;
        }
    }
}
