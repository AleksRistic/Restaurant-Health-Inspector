
package com.example.cmpt276project.fileHandle;

import android.content.Context;
import android.util.Log;


import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.struct.Restaurant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class AssetsFileHandle {
    String TAG="error";

    private Context context;
    private AssetsFileHandle(){};
    private static AssetsFileHandle assetsFileHandle=new AssetsFileHandle();
    public static AssetsFileHandle getAssetsFileHandle(){
        return assetsFileHandle;
    }

    /**
     * initialize
     * @param context context
     */

    public void start(Context context){
        this.context=context;
    }

    /**
     * read content file information from assets folder
     * @param fileName file
     * @return null or string
     */

    private String getFromAssets(String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader( context.getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            StringBuilder Result = new StringBuilder();
            while((line = bufReader.readLine()) != null)
                Result.append(line);
            return Result.toString();
        } catch (Exception e) {
            Log.e(TAG, "getFromAssets: ",e );
        }
        return null;
    }


    /**
     * get the restaurants information
     * @return information
     */

    public ArrayList<Restaurant> getRestaurantInfo(){
        ArrayList<Restaurant> restaurants=new ArrayList<>();
        try {
            Scanner scanner = new Scanner(context.getResources().getAssets().open("restaurants_itr1.csv"));
            //Skip first line (contains field titles)
            scanner.nextLine();
            while (scanner.hasNext()) {
                //Split line into restaurant fields, uses "," as delimiter
                String[] attributes = scanner.nextLine().split(",");

                //Get all restaurant attributes
                //Trims whitespace and removes brackets fro stings
                String track = attributes[0].trim().replace("\"","");
                String name = attributes[1].trim().replace("\"","");
                String address = attributes[2].trim().replace("\"","");
                String city = attributes[3].trim().replace("\"","");
                String type = attributes[4].trim().replace("\"","");
                double lat = Double.parseDouble(attributes[5].trim().replace("\"",""));
                double lon = Double.parseDouble(attributes[6].trim().replace("\"",""));

                //Add new restaurant to the array List
                restaurants.add(new Restaurant(track, name, address, city, type, lat, lon));
            }
            //Close the scanner
            scanner.close();

        } catch (Exception e) {
            Log.e(TAG, "getFromAssets: ",e );
        }

        java.util.Collections.sort(restaurants);
        java.util.Collections.reverse(restaurants);
        return restaurants;
    }
    /*
    ArrayList<Restaurant> restaurants=new ArrayList<>();
        try {
        InputStreamReader inputReader = new InputStreamReader( context.getResources().getAssets().open("restaurants_itr1.csv") );
        BufferedReader bufReader = new BufferedReader(inputReader);
        String line="";
        while((line = bufReader.readLine()) != null){
            if(!line.split(",")[0].equals("\"TRACKINGNUMBER\"")){
                String[] infos=line.split(",");
                restaurants.add(new Restaurant(infos[0].replace("\"",""),infos[1].replace("\"",""),infos[2].replace("\"",""),
                        infos[3].replace("\"",""),infos[4].replace("\"",""),infos[5].replace("\"",""),infos[6].replace("\"","")));
            }
        }
    } catch (Exception e) {
        Log.e(TAG, "getFromAssets: ",e );
    }
        return restaurants;
}*/


    /**
     * get inspection reports
     * @return the inspection reports
     */

    public ArrayList<InspectionReports> getInspectionReports(){
        ArrayList<InspectionReports> inspectionReports=new ArrayList<>();
        try {
            // InputStreamReader inputReader = new InputStreamReader( context.getResources().getAssets().open("inspectionreports_itr1.csv") );
            Scanner scanner = new Scanner(context.getResources().getAssets().open("inspectionreports_itr1.csv"));
            scanner.nextLine();
            //BufferedReader bufReader = new BufferedReader(inputReader);
            while(scanner.hasNext()){
                String[] inspection = scanner.nextLine().split(",", 7);
                inspectionReports.add(getInspectionFromString(inspection));
            }
            scanner.close();

        } catch (Exception e) {
            Log.e(TAG, "getFromAssets: ",e );
        }

        //sort InspectionReports by date
        java.util.Collections.sort(inspectionReports);
        java.util.Collections.reverse(inspectionReports);
        return inspectionReports;
    }

    //Gets all InspectionReport attributes from a String passed to it by getInspectionReports();
    private InspectionReports getInspectionFromString(String[] arr ) {
        //get all necessary Inspection values from array
        String trackingNum = arr[0].trim().replace("\"","");
        String dateAsString = arr[1].trim().replace("\"","");
        Date date = new Date();
        //Convert String to Date
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(dateAsString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String type = arr[2].trim().replace("\"","");
        int critical = Integer.parseInt(arr[3].trim());
        int nonCritical = Integer.parseInt(arr[4].trim());
        String hazardLvl = arr[5].trim().replace("\"","");
        String[] violations = getViolationsFromFile(arr[arr.length-1]);
        return new InspectionReports(trackingNum, date, type, critical, nonCritical, hazardLvl, violations);
    }

    // Parses a string and returns String array of violations passed to it by getInspectionFromString();
    private String[] getViolationsFromFile(String violationsToSplit) {
        if(!violationsToSplit.isEmpty()) {
            return violationsToSplit.split("\\|");
        }else{
            return null;
        }
    }
}
