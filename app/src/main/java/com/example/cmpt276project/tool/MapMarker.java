package com.example.cmpt276project.tool;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

import com.example.cmpt276project.R;
import com.example.cmpt276project.struct.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/*
     This class represents a single map marker (aka ClusterItem)
     Holds all necessary info a map marker must contain
 */
public class MapMarker implements ClusterItem {
    private Restaurant restaurant;
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;
    private int mHue;

    public MapMarker(double lat, double lng){
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
    }

    public MapMarker(double lat, double lng, String title, String snippet, int hue){
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mHue = hue;
    }

    public MapMarker(double lat, double lng, int hue, Restaurant restaurant){
        mPosition = new LatLng(lat, lng);
        this.restaurant = restaurant;
        mTitle = restaurant.getNAME();
        if(!(restaurant.getInspections().isEmpty())) {
            mSnippet = restaurant.getPHYSICALADDRESS();
        }else{
            mSnippet = restaurant.getPHYSICALADDRESS()
                    + "\n"
                    + Resources.getSystem().getString(R.string.no_inspections);
            //+ R.string.no_inspections;
        }
        mHue = hue;
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public int getmHue(){
        return mHue;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }


}
