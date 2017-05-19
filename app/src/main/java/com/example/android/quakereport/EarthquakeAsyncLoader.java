package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by NOURDINE on 20/11/2016.
 */
public class EarthquakeAsyncLoader extends AsyncTaskLoader<List<Earthquake>> {
    private static final String LOG_TAG = EarthquakeAsyncLoader.class.getName();
    private  String mUrl;

    public  EarthquakeAsyncLoader(Context context,String url){
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {//it's amost to force loading(trigger loadInBackground)
        Log.v(LOG_TAG,"TEST ::calling onStartLoading");
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        Log.v(LOG_TAG,"TEST ::calling loadInBackground");
        if(mUrl==null)
            return null;
        List<Earthquake> earthquakes=QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}
