package com.example.android.quakereport;

/**
 * Created by NOURDINE on 20/11/2016.
 */
public class Earthquake {
    private String mLocation;
    private long mDate;
    private double mMagnitude;
    private String mUrl;

    public Earthquake(double mag,String location ,long date,String url ){
        mMagnitude=mag;
        mLocation=location;
        mDate=date;
        mUrl=url;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public long getDate() {
        return mDate;
    }

    public String getLocation() {
        return mLocation;
    }
    public String  getUrl(){return mUrl;}



}
