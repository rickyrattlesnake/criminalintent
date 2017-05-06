package com.rattlesnake.criminalintent;

import android.text.format.DateFormat;
import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
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
        CharSequence formattedDate = DateFormat.format(pattern.toString(), mDate);
        return formattedDate.toString();
    }

    public Crime setDate(Date date) {
        mDate = date;
        return this;
    }
}
