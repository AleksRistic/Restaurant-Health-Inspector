package com.example.cmpt276project.fileHandle;

import android.os.AsyncTask;
import android.util.Log;

import com.example.cmpt276project.tool.Time;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Fetch Data reads the urls provided to us on the course website in a background thread and reads the JSON files retrieved from them
 * It parses the files to find where the CSV file is and takes the url, it also finds the last_modified date.
 * These values are passed into other classes to be used in displaying the info.
 */

public class fetchData extends AsyncTask<Void, Void, Integer> {
    String data = "";
    String inspectionData = "";
    public static String csvUrl = "";
    public static String dateModified = "";
    public static String inspectionsCsvUrl = "";
    public static String inspectionsDateModified = "";
    public static String[] inspectionsdateParse;
    public static String[] dateParse;
    public Date inspectionsDateModifiedToDate;
    public Date dateModifiedToDate;
    public Date LAST_MODIFIED_DATE;
    public Date LAST_MODIFIED_DATE_INSPECTIONS;
    Time time = new Time();

    @Override
    protected Integer doInBackground(Void... voids) {
        System.out.println("=========================================== ENTERED DOINBACK" );
        try {
            URL url = new URL("https://data.surrey.ca/api/3/action/package_show?id=restaurants");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }
            JSONObject jObj = new JSONObject(data);
            JSONObject response = jObj.getJSONObject("result"); // outter array
            JSONArray jsonArray = response.getJSONArray("resources"); // inner array
            String checkFormat;
            // iterate through JSONObject to find one that contains format 'CSV'
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonIndex = jsonArray.getJSONObject(i);
                checkFormat = jsonIndex.getString("format");
                if(checkFormat.equals("CSV")) {
                    // Save csv url and last date modified.
                    csvUrl = jsonIndex.getString("url");
                    dateModified = jsonIndex.getString("last_modified");
                    Log.i(".......", csvUrl);
                    Log.i(".......", dateModified);
                }
            }
            dateParse = dateModified.split("T", 2);
            dateModified = dateParse[0] + " " + dateParse[1];
            System.out.println("RESTAURANT DATE =========================================== " + dateModified);
            dateModifiedToDate = time.StringToDate(dateModified, "yyyy-MM-dd HH:mm:ss");
            System.out.println("WITH FUNCT =========================================== " + dateModifiedToDate);
            SimpleDateFormat ftm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                // This is the constant date. If you change this to todays date it will not cause the download prompt
                LAST_MODIFIED_DATE = ftm.parse("2020-03-26 00:00:00");
                System.out.println("RESTAURANT COMPARISON ===========================================" + LAST_MODIFIED_DATE);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } //catch (JSONException e) {
        catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            URL urls = new URL("https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");
            HttpURLConnection httpURLConnections = (HttpURLConnection) urls.openConnection();
            InputStream inputStream = httpURLConnections.getInputStream();
            BufferedReader bufferedReaders = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReaders.readLine();
                inspectionData = inspectionData + line;
            }
            JSONObject jObjs = new JSONObject(inspectionData);
            JSONObject responses = jObjs.getJSONObject("result"); // outter array
            JSONArray jsonArrays = responses.getJSONArray("resources"); // inner array
            String checkFormat;
            // iterate through JSONObject to find one that contains format 'CSV'
            for(int i=0;i<jsonArrays.length();i++)
            {
                JSONObject jsonIndexs = jsonArrays.getJSONObject(i);
                checkFormat = jsonIndexs.getString("format");
                if(checkFormat.equals("CSV")) {
                    // Save csv url and last date modified.
                    inspectionsCsvUrl = jsonIndexs.getString("url");
                    inspectionsDateModified = jsonIndexs.getString("last_modified");
                    Log.i(".......", inspectionsCsvUrl);
                    Log.i(".......", inspectionsDateModified);
                }
            }
            inspectionsdateParse = inspectionsDateModified.split("T", 2);
            inspectionsDateModified = inspectionsdateParse[0] + " " + inspectionsdateParse[1];
            Log.i("MODIFIED INSPECTIONS DATE", inspectionsDateModified);
            inspectionsDateModifiedToDate = time.StringToDate(inspectionsDateModified, "yyyy-MM-dd HH:mm:ss");
            System.out.println("INSPECTIONS REPORT =========================================== " + inspectionsDateModifiedToDate);
            SimpleDateFormat ftm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                // This is the constant date. If you change this to todays date it will not cause the download prompt
                LAST_MODIFIED_DATE_INSPECTIONS = ftm.parse("2020-03-26 00:00:00");
                System.out.println("INSPECTION COMPARISON =========================================== " + LAST_MODIFIED_DATE_INSPECTIONS);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch(MalformedURLException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        } //catch (JSONException e) {
        catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

