package com.example.cmpt276project.struct;

import android.content.res.Resources;

import com.example.cmpt276project.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class InspectionReports implements Comparable<InspectionReports>, Serializable {
    private String TrackingNumber;
    private Date InspectionDate;
    private String InspType;
    private int NumCritical;
    private int NumNonCritical;
    private String HazardRating;
    private String[] ViolLump;


    public class Violation {
        private String ViolDescription;
        private String ViolSeverity;

        public Violation(String violDescription, String violSeverity) {
            ViolDescription = violDescription;
            ViolSeverity = violSeverity;

        }
    }

    public InspectionReports(String trackingNumber, Date inspectionDate, String inspType,
                             int numCritical, int numNonCritical, String hazardRating, String[] violLump) {
        TrackingNumber = trackingNumber;
        InspectionDate = inspectionDate;
        InspType = inspType;
        NumCritical = numCritical;
        NumNonCritical = numNonCritical;
        HazardRating = hazardRating;
        ViolLump = violLump;
    }

    //make inspections comparable by date for sorting
    @Override
    public int compareTo(InspectionReports other) {
        return InspectionDate.compareTo(other.InspectionDate);
    }

    //@returns number of days since this inspection
    public int getDaysSince(){
        Date now = new Date();
        long daysSince = now.getTime() - InspectionDate.getTime();
        return (int) TimeUnit.DAYS.convert(daysSince, TimeUnit.MILLISECONDS);
    }

    //@returns date as a formatted string based on the days since the inspection
    // todo: figure out if it accounts for leap years
    public String getDateForDisplay(){
        SimpleDateFormat monthDay = new SimpleDateFormat("MMM dd", Locale.CANADA);
        SimpleDateFormat monthYear = new SimpleDateFormat("MMM yyyy", Locale.CANADA);

        if(this.getDaysSince() <= 30){
            return String.valueOf(getDaysSince());
        }else if(this.getDaysSince() <= 365){
            // SimpleDateFormat pattern = new SimpleDateFormat("MMM dd", Locale.CANADA);
            return monthDay.format(this.InspectionDate);
        }else if(this.getDaysSince() > 365){
            //SimpleDateFormat pattern = new SimpleDateFormat("MMM yyyy", Locale.CANADA);
            return monthYear.format(this.InspectionDate);
        }else{
            return "Error";
        }

    }

    public String getTrackingNumber() {
        return TrackingNumber;
    }

    //@return the date od inspection
    //NOT formatted for screen output, use getDateForDisplay instead
    public Date getInspectionDate() {
        return InspectionDate;
    }

    public String getInspType() {
        return InspType;
    }

    public int getNumCritical() {
        return NumCritical;
    }

    public int getNumNonCritical() {
        return NumNonCritical;
    }

    public String getHazardRating() {
        return HazardRating;
    }

    //@return an array of every violation this Inspection contains
    public String[] getViolLump() {
        if (ViolLump != null) {
            return ViolLump;
        }else{

            String[] arr = {Resources.getSystem().getString(R.string.no_violations_found)};
            return arr;
        }
    }


/*
    public void setViolLump(String violLump) {
        ViolLump = violLump;
    }
 */
//    public String toString(){
//        return " Critical:"+getNumCritical()+
//                " Noncritical:"+getNumNonCritical()+
//                " Date:"+getDateForDisplay();
//    }

}