package com.example.android.quakereport;

/**
 * Created by NOURDINE on 20/11/2016.
 */
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private static final String LOG_TAG =QueryUtils.class.getName();
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractFeaturesFromJson(String jsonResponse) {
        if(TextUtils.isEmpty(jsonResponse)){
            return  null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject rootJsonObject=new JSONObject(jsonResponse);
            JSONArray featuresJsonArray=rootJsonObject.optJSONArray("features");

            for(int i=0;i<featuresJsonArray.length();i++){
                JSONObject currentJsonObject=featuresJsonArray.getJSONObject(i);
                JSONObject propertiesJsonObject=currentJsonObject.getJSONObject("properties");

                double mag= (propertiesJsonObject.getDouble("mag"));


                // String mag_str=String.valueOf(mag);
                String location=propertiesJsonObject.getString("place");

                Long  date=propertiesJsonObject.getLong("time");
                String url=propertiesJsonObject.getString("url");
                // Date dateObject=new Date(date);
                //SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM DD, YYYY");
                // String dateToDisplay=simpleDateFormat.format(dateObject);

                Earthquake earthquake=new Earthquake(mag,location,date,url);
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }
    private static URL createUrl(String  stringUrl){
        URL url=null;
        if(stringUrl==null){
            return  null;
        }
        try {
            url = new URL(stringUrl);
        }catch(Exception e){
            Log.e(LOG_TAG,"ERRORR creating url"+e);
        }
        return url;
    }
    private  static String makeHttpRequest(URL url){
          String jsonResponse=" ";
        if(url==null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(50000);
            urlConnection.setReadTimeout(50000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                inputStream=urlConnection.getInputStream();
                jsonResponse=readFromStream(inputStream);

            }else {
                Log.e(LOG_TAG,"ERROR Response code"+urlConnection.getResponseCode());
            }

        }catch (IOException e){
            Log.e(LOG_TAG,"ERROR  retrieving jsonData"+e);

        }finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(inputStream!=null){

                ///this what may throws IOException
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG,"ERROR  closing the input stream"+e);
                }
            }
        }
        return jsonResponse;
    }
    private  static  String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder stringBuilder=new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String line=bufferedReader.readLine();//may throw IOException
            while (line!=null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }
public  static List<Earthquake>fetchEarthquakeData(String requestUrl)  {
    Log.i(LOG_TAG,"TEST ::calling fetchEarthquakeData");
    //force sleep to test progress bar
  /* try {
        Thread.sleep(10000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }*/

    URL url=createUrl(requestUrl);
    String jsonResponse=null;
    try {
        jsonResponse= makeHttpRequest(url);
    }catch (Exception e){
        Log.e(LOG_TAG,"Problem making HTTP Request");
    }
List<Earthquake>earthquakes=extractFeaturesFromJson(jsonResponse);
    return  earthquakes;

}

}