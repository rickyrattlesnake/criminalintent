package com.rattlesnake.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {
    private static String ARG_TIME = "time";
    private static String EXTRA_TIME = "com.rattlesnake.criminalintent.time";
    private TimePicker mTimePicker;
    private Calendar mTime;


    public static TimePickerFragment newInstance(Date time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Date getTime(Intent data){
        return (Date) data.getSerializableExtra(EXTRA_TIME);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date crimeTime = (Date) getArguments().getSerializable(ARG_TIME);
        mTime = new GregorianCalendar();
        mTime.setTime(crimeTime);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            mTimePicker.setCurrentHour(mTime.get(Calendar.HOUR));
            mTimePicker.setCurrentMinute(mTime.get(Calendar.MINUTE));
        } else {
            mTimePicker.setHour(mTime.get(Calendar.HOUR));
            mTimePicker.setMinute(mTime.get(Calendar.MINUTE));
        }

        return new AlertDialog.Builder(getActivity())
            .setView(v)
            .setTitle(R.string.time_picker_title)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int hour;
                    int minute;
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                        hour = mTimePicker.getCurrentHour();
                        minute = mTimePicker.getCurrentMinute();
                    } else {
                        hour = mTimePicker.getHour();
                        minute = mTimePicker.getMinute();
                    }
                    mTime.set(Calendar.HOUR, hour);
                    mTime.set(Calendar.MINUTE, minute);
                    sendResult(Activity.RESULT_OK, mTime.getTime());
                }
            })
            .create();
    }

    private void sendResult(int resultCode, Date date){
        if (getTargetFragment() == null){
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_TIME, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
