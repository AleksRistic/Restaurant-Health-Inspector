package com.example.cmpt276project.tool;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
    String TAG="error";

    /**
     * get the display date
     * @param date date
     * @return string
     */
    public String getDate(String date){
        // if within 30 days
        if((Math.abs(new Date().getTime() - StringToDate(date).getTime()))/(1000 * 60 * 60 * 24)<=30){
            return (Math.abs(new Date().getTime() - StringToDate(date).getTime()))/(1000 * 60 * 60 * 24)+"days before";
        }else if((Math.abs(new Date().getTime() - StringToDate(date).getTime()))/(float)(1000 * 365 * 60 * 60 * 24)<1){
            // if within 1 year
            Log.d(TAG, "getDate: "+DateToString(StringToDate(date),"MM, dd"));
            return DateToString(StringToDate(date),"MM, dd");
        }else{
            return DateToString(StringToDate(date),"yyyy, MM");
        }
    }

    /**
     * date change to string
     * @param date date
     * @return string
     */
    public String DateToString(Date date,String style){
        try {
            DateFormat dateFormat=new SimpleDateFormat(style);
            return dateFormat.format(date);
        }catch (Exception e){
            Log.e(TAG, "DateToString: ", e);
        }
        return null;
    }

    /**
     * string change to date
     * @param stime string date
     * @return date
     */
    public Date StringToDate(String stime,String style){
        DateFormat dateFormat=new SimpleDateFormat(style);
        Date date=null;
        try {
            date=dateFormat.parse(stime.replace("\"",""));
        }catch (Exception e){
            Log.e(TAG, "StringToDate: ",e );
        }
        return date;
    }

    /**
     * string change to date
     * @param stime string date
     * @return date
     */
    public Date StringToDate(String stime){
        DateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
        Date date=null;
        try {
            date=dateFormat.parse(stime.replace("\"",""));
        }catch (Exception e){
            Log.e(TAG, "StringToDate: ",e );
        }
        return date;
    }
}
