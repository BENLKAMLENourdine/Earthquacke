/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
   // private ProgressBar mLoadingIndicator =(ProgressBar) findViewById(R.id.loading_indicator);
    private static final int EARTHQUAKE_LOADER_ID = 1;//usefull when have more than 1 loader
    private EarthquakeAdapter mAdapter;
    private TextView mEmptyTextView;
    private static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL ="http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=8&orderby=time";
          // " https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10&starttime=2016-11-16";
    private static final String USGS_REQUEST_URL_NEW=
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG,"TEST ::calling onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

                ListView EarthquakesListView=(ListView)findViewById(R.id.list);

        mEmptyTextView=(TextView)findViewById(R.id.empty_view);
        EarthquakesListView.setEmptyView(mEmptyTextView);

        mAdapter=new EarthquakeAdapter(this,new ArrayList<Earthquake>());
        EarthquakesListView.setAdapter(mAdapter);
        // Create a fake list of earthquake locations.
        //List<Earthquake> earthquakes = new ArrayList<Earthquake>();
        EarthquakesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currentEarthquake =mAdapter.getItem(position);
                Uri earhquakeUri= Uri.parse(currentEarthquake.getUrl());

                Intent websiteIntent=new Intent(Intent.ACTION_VIEW,earhquakeUri);
                startActivity(websiteIntent);

            }
        });

        /**
         *o retrieve an earthquake, we need to get the loader manager and tell the loader manager to
         * initialize the loader with the specified ID, the second argument allows us to pass a bundle of additional information,
         * which we'll skip. The third argument is
         *  what object should receive the LoaderCallbacks (and therefore, the data when the load is complete!) -
         *  which will be this activity
         * */
        //LoaderManager loaderManager=getLoaderManager() ;
        ConnectivityManager cm=(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=cm.getActiveNetworkInfo();
        boolean isConnected= activeNetworkInfo!=null&&activeNetworkInfo.isConnectedOrConnecting();

        Log.i(LOG_TAG,"TEST ::calling initLoader");
        if(isConnected) {
            LoaderManager loaderManager=getLoaderManager() ;
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        }else{
            ProgressBar mLoadingIndicator =(ProgressBar) findViewById(R.id.loading_indicator);
            mLoadingIndicator.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.no_network_state);
        }


       /* earthquakes.add(new Earthquake("4.6","San Francisco","OCT 2015-12-23"));
        earthquakes.add(new Earthquake("4.6","San Francisco","OCT 2015-12-23"));
        earthquakes.add(new Earthquake("4.6","San Francisco","OCT 2015-12-23"));
        earthquakes.add(new Earthquake("4.6","San Francisco","OCT 2015-12-23"));
        earthquakes.add(new Earthquake("4.6","San Francisco","OCT 2015-12-23"));
        earthquakes.add(new Earthquake("4.6","San Francisco","OCT 2015-12-23"));*/

        //earthquakes=QueryUtils.extractEarthquakes();
       /* earthquakes.add("London");
        earthquakes.add("Tokyo");
        earthquakes.add("Mexico City");
        earthquakes.add("Moscow");
        earthquakes.add("Rio de Janeiro");
        earthquakes.add("Paris");*/

        // Find a reference to the {@link ListView} in the layout
       /* ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);*/
        // final EarthquakeAdapter earthquakeAdapter =new EarthquakeAdapter(this,earthquakes);

        // EarthquakesListView.setAdapter(earthquakeAdapter);

       // EarthquakeAsyncClass task=new EarthquakeAsyncClass();
       // task.execute(USGS_REQUEST_URL);


    }

   //We need onCreateLoader(), for when the LoaderManager has determined that
   // the loader with our specified ID isn't running, so we should create a new one
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG,"TEST ::calling onCreareLoader");
        //return new EarthquakeAsyncLoader(this,USGS_REQUEST_URL);
         SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        Uri baseUri = Uri.parse(USGS_REQUEST_URL_NEW);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("orderby", orderBy);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("limit", "10");
       // uriBuilder.appendQueryParameter("starttime", "2017-01-01");//starttime=2017-01-01

        Log.v(LOG_TAG,"this is the new url::"+uriBuilder.toString());
        return new EarthquakeAsyncLoader(this, uriBuilder.toString());

        //return new EarthquakeAsyncLoader(this, USGS_REQUEST_URL);
    }
//as onPostExecute
    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        Log.v(LOG_TAG,"TEST::calling onLoadFinished");
        ProgressBar mLoadingIndicator =(ProgressBar) findViewById(R.id.loading_indicator);
        mLoadingIndicator.setVisibility(View.GONE);
        mEmptyTextView.setText(R.string.empty_state);
        //for indicator


        mAdapter.clear();
        if (data != null && !data.isEmpty()) {

                mAdapter.addAll(data);
         }

    }
    //And we need onLoaderReset(), we're we're being informed that the data from our loader is no longer valid.
    // This isn't actually a case that's going to come up with our simple loader,
    // but the correct thing to do is to remove all the earthquake data from our UI by clearing out the adapter’s data set

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.i(LOG_TAG,"TEAT :: calling onLoaderReset");

        mAdapter.clear();
    }
    /*****  private class EarthquakeAsyncClass extends AsyncTask<String,Void,List<Earthquake>>{
        @Override
        protected List<Earthquake> doInBackground(String... urls) {

            if(urls.length<1||urls[0]==null){
                return  null;
            }
            List<Earthquake>result=QueryUtils.fetchEarthquakeData(urls[0]);
            return  result;
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            //super.onPostExecute(earthquake);
            mAdapter.clear();
            if(earthquakes!=null&&!earthquakes.isEmpty()){
                mAdapter.addAll(earthquakes);
            }
        }
    }***/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
