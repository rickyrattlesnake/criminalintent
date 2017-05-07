package com.rattlesnake.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static String ARG_CRIME_ID = "crime_id";

    private Crime mCrime;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private EditText mTitleField;
    private OnCrimeChangedListener mCrimeChangedCallback;

    public CrimeFragment() {
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle argsBundle = new Bundle();
        argsBundle.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment cf = new CrimeFragment();
        cf.setArguments(argsBundle);
        return cf;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCrimeChangedCallback = (OnCrimeChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnCrimeChangedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        configureWidgets();
        return v;
    }

    private void registerChange() {
        mCrimeChangedCallback.onCrimeChanged(mCrime.getId());
    }

    private void configureWidgets() {
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                registerChange();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton.setText(mCrime.getFormattedDate());
        mDateButton.setEnabled(false);

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                registerChange();
            }
        });
    }

    interface OnCrimeChangedListener {
        void onCrimeChanged(UUID crimeId);
    }
}
