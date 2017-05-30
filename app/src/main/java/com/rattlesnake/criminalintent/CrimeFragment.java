package com.rattlesnake.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;

    private Crime mCrime;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mChooseSuspect;
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
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mChooseSuspect = (Button) v.findViewById(R.id.crime_suspect);

        configureWidgets();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_delete_crime:
                removeCrime();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null){
            if (requestCode == REQUEST_DATE){
                Date newDate = DatePickerFragment.getDate(data);
                mCrime.setDate(newDate);
            } else if (requestCode == REQUEST_TIME){
                Date newDate = TimePickerFragment.getTime(data);
                mCrime.setDate(newDate);
            } else if (requestCode == REQUEST_CONTACT) {
                Uri contactUri = data.getData();
                String[] queryFields = new String[] { Contacts.DISPLAY_NAME };
                try (Cursor c = getActivity().getContentResolver().query(contactUri,
                                                                         queryFields,
                                                                         null,
                                                                         null,
                                                                         null)) {
                    if (c.getCount() == 0) {
                        return;
                    }
                    c.moveToFirst();
                    mCrime.setSuspect(c.getString(0));
                    mChooseSuspect.setText(mCrime.getSuspect());
                }
            }
        }

        updateWidgetContent();
        registerChange();
    }

    private void updateWidgetContent(){
        mDateButton.setText(mCrime.getFormattedDate());
        mTimeButton.setText(mCrime.getFormattedTime());

        if (mCrime.getSuspect() != null) {
            mChooseSuspect.setText(mCrime.getSuspect());
        }
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

        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                                              Contacts.CONTENT_URI);
        mChooseSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        PackageManager pm = getActivity().getPackageManager();
        if (pm.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mChooseSuspect.setEnabled(false);
        }

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setChooserTitle(R.string.send_report)
                .setText(getCrimeReport())
                .setSubject(getString(R.string.crime_report_subject))
                .startChooser();
            }
        });

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                registerChange();
            }
        });

        updateWidgetContent();
    }

    private void removeCrime(){
        UUID crimeId = mCrime.getId();
        CrimeLab.get(getActivity()).removeCrime(mCrime);
        mCrimeChangedCallback.onCrimeRemoved(crimeId);
        getActivity().onBackPressed();
    }

    private String getCrimeReport() {
        String solvedString = (mCrime.isSolved()) ? getString(R.string.crime_report_solved) :
                                getString(R.string.crime_report_unsolved);
        String datetime = mCrime.getFormattedDate() + mCrime.getFormattedTime();
        String suspect = mCrime.getSuspect();
        suspect = (suspect == null) ? getString(R.string.crime_report_no_suspect) :
                    getString(R.string.crime_report_suspect, suspect);

        return getString(R.string.crime_report,
                          mCrime.getTitle(),
                          datetime,
                          solvedString,
                          suspect);

    }

    interface OnCrimeChangedListener {
        void onCrimeChanged(UUID crimeId);
        void onCrimeRemoved(UUID crimeId);
    }
}
