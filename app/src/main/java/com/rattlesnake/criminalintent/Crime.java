package com.rattlesnake.criminalintent;

import android.text.format.DateFormat;
import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(String uuidString) {
        this(UUID.fromString(uuidString));
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Crime setTitle(String mTitle) {
        this.mTitle = mTitle;
        return this;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public Crime setSolved(boolean solved) {
        mSolved = solved;
        return this;
    }

    public Date getDate() {
        return mDate;
    }

    public String getFormattedDate(){
        String pattern = "EEE, d MMM yyyy";
        CharSequence formattedDate = DateFormat.format(pattern, mDate);
        return formattedDate.toString();
    }

    public String getFormattedTime() {
        String pattern = "hh:mm a";
        CharSequence formattedTime = DateFormat.format(pattern, mDate);
        return formattedTime.toString();
    }

    public Crime setDate(Date date) {
        mDate = date;
        return this;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public Crime setSuspect(String suspect) {
        mSuspect = suspect;
        return this;
    }
}
