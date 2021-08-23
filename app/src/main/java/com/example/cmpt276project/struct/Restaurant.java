package com.example.cmpt276project.struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Comparable<Restaurant>, Serializable {
    private String TRACKINGNUMBER;
    private String NAME;
    private String PHYSICALADDRESS;
    private String PHYSICALCITY;
    private String FACTYPE;
    private double LATITUDE;
    private double LONGITUDE;

    private List<InspectionReports> inspections = new ArrayList<>();

    public Restaurant(String TRACKINGNUMBER, String NAME, String PHYSICALADDRESS, String PHYSICALCITY, String FACTYPE, double LATITUDE, double LONGITUDE) {
        this.TRACKINGNUMBER = TRACKINGNUMBER;
        this.NAME = NAME;
        this.PHYSICALADDRESS = PHYSICALADDRESS;
        this.PHYSICALCITY = PHYSICALCITY;
        this.FACTYPE = FACTYPE;
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
    }

    // Allows Restaurants to be comparable by name
    @Override
    public int compareTo(Restaurant other) {
        return NAME.compareTo(other.NAME);
    }

    protected void addInspection(InspectionReports inspection){
        this.inspections.add(inspection);
        java.util.Collections.sort(inspections);
        java.util.Collections.reverse(inspections);
    }

    //Returns the sum of all Critical and NonCritical Issues of most recent inspection
    public int getNumIssues(){
        return inspections.get(0).getNumNonCritical() + inspections.get(0).getNumCritical();
    }

    //@return a List of all InspectionReports sorted by Date (descending order)
    public List<InspectionReports> getInspections(){
        // java.util.Collections.sort(inspections);
        // java.util.Collections.reverse(inspections);
        return inspections;

    }

    // @return most recent inspection
    // Assumes inspections list is already sorted.
    public InspectionReports getMostRecentInspection(){
        if(!(inspections.isEmpty())){
            return inspections.get(0);
        }else{
            return null;
        }
    }

    public String getTRACKINGNUMBER() {
        return TRACKINGNUMBER;
    }

    public String getNAME() {
        return NAME;
    }

    public String getPHYSICALADDRESS() {
        return PHYSICALADDRESS;
    }

    public String getPHYSICALCITY() {
        return PHYSICALCITY;
    }

    public String getFACTYPE() {
        return FACTYPE;
    }

    public double getLATITUDE() {
        return LATITUDE;
    }

    public double getLONGITUDE() {
        return LONGITUDE;
    }


    //For debugging only
    @Override
    public String toString(){
        return " " + TRACKINGNUMBER + " | "
                + NAME + " | "
                + PHYSICALCITY + " | "
                + PHYSICALCITY + " | "
                + LATITUDE + " | "
                + LONGITUDE + " "
                + "\n";
    }

}
