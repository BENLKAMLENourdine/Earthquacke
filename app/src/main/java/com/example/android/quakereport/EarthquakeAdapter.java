package com.example.android.quakereport;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by NOURDINE on 20/11/2016.
 */
public class EarthquakeAdapter extends ArrayAdapter<Earthquake>{

    public EarthquakeAdapter(Activity context, ArrayList<Earthquake>earthquakeArray) {
        super(context,0, earthquakeArray);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView =convertView;
        if(listItemView==null){
            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }


        Earthquake currentEarhquake=getItem(position);


        TextView magTextView=(TextView)listItemView.findViewById(R.id.mag);

    /*
    * for circle under magnitude
    *
    * */
        GradientDrawable magnitudeCircle = (GradientDrawable) magTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarhquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);
        /***/

        Double magnitude=currentEarhquake.getMagnitude();
        DecimalFormat doubleFormatter=new DecimalFormat("0.0");
        String magString=doubleFormatter.format(magnitude);
        magTextView.setText(magString);

        TextView locationDetailsTextView=(TextView)listItemView.findViewById(R.id.locationDetails);
        TextView locationTextView=(TextView)listItemView.findViewById(R.id.location);

        String arr[]=splitString(currentEarhquake.getLocation());

        locationDetailsTextView.setText(arr[0]);
        locationTextView.setText(arr[1]);

        TextView dateTextView=(TextView)listItemView.findViewById(R.id.date);
        TextView timeTextView=(TextView)listItemView.findViewById(R.id.time);

        Date dateObject=new Date(currentEarhquake.getDate());

        String formattedDate=formatDate(dateObject);
        String formattedTime=formatTime(dateObject);

        dateTextView.setText(formattedDate);
        timeTextView.setText(formattedTime);



        return listItemView;
    }
    private String formatDate(Date dateObject){

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("LLL dd,yyyy");
        return simpleDateFormat.format(dateObject);
    }
    private String formatTime(Date dateObject){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(dateObject);
    }

    private String[] splitString(String text){
        String subStr1;
        String subStr2;
        if(text.contains("km")&&text.contains("of")){
            subStr1=text.substring(0,text.indexOf("of")+2);
            subStr2=text.substring(text.indexOf("of")+3);
        }
        else{
            subStr1 = getContext().getString(R.string.near_the);
            subStr2=text;
        }
        /*boolean isFound=text.contains(subStr1);
        if(!isFound) {
            subStr1 = "Near the ";
            subStr2=text;
        }
        else{
            int pos=text.indexOf(subStr1);
            subStr2=text.substring(pos+subStr1.length()+1);
        }
*/

        String []arr={subStr1,subStr2};
        return  arr;
    }
    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
