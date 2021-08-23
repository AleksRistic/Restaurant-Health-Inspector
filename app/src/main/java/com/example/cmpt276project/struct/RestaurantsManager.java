package com.example.cmpt276project.struct;

import android.util.Log;

import java.util.ArrayList;

/*
    This class holds a list of all Restaurants and InspectionReports.
    As a non-activity class cannot access the assets folder, it must be
    initialized on startup by the main activity.
 */
public class RestaurantsManager {

    private ArrayList<Restaurant> restaurants;
    private ArrayList<InspectionReports> inspections;
    private static RestaurantsManager instance;

    private ArrayList<Restaurant> favourite;

    private RestaurantsManager(ArrayList<Restaurant> restaurants, ArrayList<InspectionReports> inspections){
        this.restaurants = restaurants;
        this.inspections = inspections;

        java.util.Collections.sort(restaurants);
        //java.util.Collections.reverse(restaurants);

        assignInspectionToRestaurant();
    }

    private RestaurantsManager(ArrayList<Restaurant> favourite){
        this.favourite=favourite;
        java.util.Collections.sort(favourite);

        assignInspectionToRestaurant();
    }

    public static RestaurantsManager getInstance(ArrayList<Restaurant> restaurants,ArrayList<InspectionReports> inspections){
        if(instance == null){
            instance = new RestaurantsManager(restaurants,inspections);
        }
        return  instance;
    }

    public static RestaurantsManager getInstance(ArrayList<Restaurant> favourite){
        if(instance == null){
            instance = new RestaurantsManager(favourite);
        }
        return  instance;
    }

    public static  RestaurantsManager getInstance(){
        if(instance == null){
            Log.d("NULL RESTAURANT MANAGER", "getInstance: restaurant manager has not been Initialized ");
            return null;
        }
        return instance;
    }

    /*
        @returns a list of all restaurants
         each restaurant contains a list of its inspections
    */
    public ArrayList<Restaurant> getRestaurants(){
        return restaurants;
    }

    public ArrayList<Restaurant> getFavourite(){return favourite; }

    /*
       @return a list of every inspection
       Use Restaurant.getInspections() instead wherever applicable
    */
    public ArrayList<InspectionReports> getInspections(){
        return inspections;
    }

    private void assignInspectionToRestaurant() {
        for(Restaurant r : restaurants) {
            for(InspectionReports i : inspections){
                if(r.getTRACKINGNUMBER().equals(i.getTrackingNumber())){
                    r.addInspection(i);
                }
            }
        }
    }

    public ArrayList<InspectionReports> getInspectionReports(Restaurant restaurant){
        ArrayList<InspectionReports> reports = new ArrayList<InspectionReports>(restaurant.getInspections());

        java.util.Collections.sort(reports);

        return reports;//SqlManager.getSqlManager().queryInspectionReportsData(restaurant);
    }
}

