package com.example.cmpt276project.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cmpt276project.R;
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.struct.Restaurant;
import com.example.cmpt276project.tool.Time;

import java.util.ArrayList;


public class SqlManager {
    private static String TAG="sql";
    private static String[] tableNames={"restaurants","inspectionreports",/*TODO:add new things*/"favourite"};//Table name
    private static String[] tableStyle={"TRACKINGNUMBER nvarchar(20),NAME nvarchar(20),PHYSICALADDRESS nvarchar(30),PHYSICALCITY nvarchar(20),FACTYPE nvarchar(20),LATITUDE float,LONGITUDE float,PRIMARY KEY(TRACKINGNUMBER)",
            "ID int, TrackingNumber nvarchar(20),InspectionDate DateTime,InspType nvarchar(10),NumCritical int,NumNonCritical int,HazardRating nvarchar(10),ViolLump nvarchar(300),PRIMARY KEY(ID)",
            /*TODO:new things*/"TRACKINGNUMBER nvarchar(20),NAME nvarchar(20),PHYSICALADDRESS nvarchar(30),PHYSICALCITY nvarchar(20),FACTYPE nvarchar(20),LATITUDE float,LONGITUDE float,PRIMARY KEY(TRACKINGNUMBER)"};//表格字段,TRACKINGNUMBER 为主键

    private static SqlManager sqlManager=null;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private Time time=new Time();

    private SqlManager(Context context){
        this.context=context;
        sqLiteDatabase=context.openOrCreateDatabase("Data.db", context.MODE_PRIVATE,null);//Connect to database
        //Create data table
        for(int i=0;i<tableNames.length;i++){
            buildSheet(tableNames[i],tableStyle[i]);
        }
    }

    public static SqlManager getSqlManager(Context context){
        if(sqlManager==null){
            sqlManager=new SqlManager(context);
        }
        return sqlManager;
    }

    public static SqlManager getSqlManager() throws NullPointerException{
        if (sqlManager==null){
            Log.e(TAG, "getSqlManager: Database not initialized");
            throw new NullPointerException();
        }

        return sqlManager;
    }

    public static String[] getTableNames(){
        return tableNames;
    }

    /**
     * Database insert data
     * @param tableName table name
     * @param values    Data need to insert
     * @return true or false
     */
    public boolean insertData(String tableName, ContentValues values){
        if(sqLiteDatabase.insert(tableName,null,values)==-1){
            return false;
        }
        return true;
    }


    /**
     * Database update data
     * @param tableName table name
     * @param key       Field name
     * @param keyName   field
     * @param values    Data need to update
     * @return true or false
     */
    public boolean updateData(String tableName,String key,String[] keyName,ContentValues values){
        if(sqLiteDatabase.update(tableName,values,key+"=? ",keyName)==-1){
            return false;
        }
        return true;
    }

    /**
     * Create data table
     */
    public void buildSheet(String name,String style){
        try {
            sqLiteDatabase.execSQL("CREATE TABLE "+name+"("+style+")");
        }catch (Exception e){
            Log.e(TAG, "buildSheet: ",e );
        }
    }

    /**
     * judge whether exist restaurant
     * @param TRACKINGNUMBER string
     * @return true or false
     */
    public boolean isExistRestaurant(String TRACKINGNUMBER){

        //This below will greatly affect the update speed, CAREFULLY
        Cursor cursor=sqLiteDatabase.query(tableNames[0],new String[]{"TRACKINGNUMBER"},"TRACKINGNUMBER=?",new String[]{TRACKINGNUMBER},null,null,null,null);
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * Judge whether there is inspection information
     * @param keyName
     * @return
     */
    public boolean isExistInspectionReport(String[] keyName){
        Cursor cursor = sqLiteDatabase.query(tableNames[1], null, "ID=? and TrackingNumber=? and InspectionDate=? and InspType=? and NumCritical=? and NumNonCritical=? and HazardRating=? and ViolLump=?", keyName, null, null, null, null);
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    /**
     * Get restaurant information
     * @return query
     */
    public ArrayList<Restaurant> queryRestaurantsData(){
        ArrayList<Restaurant> studentInfoList=new ArrayList<Restaurant>();
        try {
            //Query all data
            Cursor cursor1 = sqLiteDatabase.rawQuery("select * from " + tableNames[0] ,null);
            //Move cursor to first row
            cursor1.moveToFirst();
            int cols=cursor1.getColumnCount();
            //Loop read data
            while(!cursor1.isAfterLast()){
                String[] studentInfo=new String[cols];
                Restaurant restaurant=new Restaurant(cursor1.getString(0),cursor1.getString(1),cursor1.getString(2),cursor1.getString(3),
                        cursor1.getString(4),cursor1.getDouble(5),cursor1.getDouble(6));
                studentInfoList.add(restaurant);
                //Move cursor to next line
                cursor1.moveToNext();
            }
        }catch (Exception e){
            Log.e("error", "queryData: ",e );
        }
        return studentInfoList;
    }

    /**
     * Get inspection information
     * @return information
     */
    public ArrayList<InspectionReports> queryInspectionReportsData(){
        ArrayList<InspectionReports> studentInfoList=new ArrayList<InspectionReports>();
        try {
            //Query all data
            Cursor cursor1 = sqLiteDatabase.rawQuery("select TrackingNumber,InspectionDate,InspType,NumCritical,NumNonCritical,HazardRating,ViolLump from " + tableNames[1] ,null);
            //Move cursor to first row
            cursor1.moveToFirst();
            int cols=cursor1.getColumnCount();
            //Loop read data
            while(!cursor1.isAfterLast()){
                String[] studentInfo=new String[cols];
                InspectionReports inspectionReports=new InspectionReports(cursor1.getString(0),time.StringToDate(cursor1.getString(1),"yyyyMMdd" /*HH:MM:SS*/),cursor1.getString(2),cursor1.getInt(3),
                        cursor1.getInt(4),cursor1.getString(5),cursor1.getString(6).split("\\|"));
                studentInfoList.add(inspectionReports);
                //Move cursor to next line
                cursor1.moveToNext();
            }
        }catch (Exception e){
            Log.e("error", "queryData: ",e );
        }
        return studentInfoList;
    }

    //TODO:add new method

    /**
     * Return to the inspection report according to the designated restaurant
     * @param restaurant
     * @return
     */
    public ArrayList<InspectionReports> queryInspectionReportsData(Restaurant restaurant){
        ArrayList<InspectionReports> studentInfoList=new ArrayList<InspectionReports>();

        try {
            //Query all data
            Cursor cursor1 = sqLiteDatabase.rawQuery("select * from " + tableNames[1] + "where TrackingNumber="+restaurant.getTRACKINGNUMBER(),null);
            //Move cursor to first row
            cursor1.moveToFirst();
            int cols=cursor1.getColumnCount();
            //Loop read data
            while(!cursor1.isAfterLast()){
                String[] studentInfo=new String[cols];
                InspectionReports inspectionReports=new InspectionReports(cursor1.getString(0),time.StringToDate(cursor1.getString(1),"yyyyMMdd" /*HH:MM:SS*/),cursor1.getString(2),cursor1.getInt(3),
                        cursor1.getInt(4),cursor1.getString(5),cursor1.getString(6).split("\\|"));
                studentInfoList.add(inspectionReports);
                //Move cursor to next line
                cursor1.moveToNext();
            }
        }catch (Exception e){
            Log.e("error", "queryData: ",e );
        }
        return studentInfoList;
    }

    public void deleteRows(){
        sqLiteDatabase.delete(tableNames[0], null, null);
        sqLiteDatabase.delete(tableNames[1], null, null);

    }

    //TODO:add new methods

    /**
     * Conditional query
     * @param key
     * @param values
     * @return
     */
    public ArrayList<Restaurant> queryRestaurant(String key,String values){
        ArrayList<Restaurant> restaurants=new ArrayList<>();

        try {
            //Query all data
            Cursor cursor1 = sqLiteDatabase.rawQuery("select * from " + tableNames[0] + "where "+key+"="+values,null);
            //Move cursor to first row
            cursor1.moveToFirst();
            int cols=cursor1.getColumnCount();
            //Loop read data
            while(!cursor1.isAfterLast()){
                String[] studentInfo=new String[cols];
                Restaurant restaurant=new Restaurant(cursor1.getString(0),cursor1.getString(1),cursor1.getString(2),cursor1.getString(3),
                        cursor1.getString(4),cursor1.getDouble(5),cursor1.getDouble(6));
                restaurants.add(restaurant);
                //Move cursor to next line
                cursor1.moveToNext();
            }
        }catch (Exception e){
            Log.e("error", "queryData: ",e );
        }
        return restaurants;
    }

    /**
     * Check the full list of favorite restaurants
     * @return
     */
    public ArrayList<Restaurant> queryFavouriteRestaurant(){
        ArrayList<Restaurant> restaurantInfoList=new ArrayList<>();
        try {
            //Query all data
            Cursor cursor1 = sqLiteDatabase.rawQuery("select * from " + tableNames[2] ,null);
            //Move cursor to first row
            cursor1.moveToFirst();
            int cols=cursor1.getColumnCount();
            //Loop read data
            while(!cursor1.isAfterLast()){
                Restaurant restaurant=new Restaurant(cursor1.getString(0),cursor1.getString(1),cursor1.getString(2),cursor1.getString(3),
                        cursor1.getString(4),cursor1.getDouble(5),cursor1.getDouble(6));
                restaurantInfoList.add(restaurant);
                //Move cursor to next line
                cursor1.moveToNext();
            }
        }catch (Exception e){
            Log.e("error", "queryData: ",e );
        }
        return restaurantInfoList;
    }

    /**
     * Delete field
     * @param tableName
     * @param key
     * @param value
     * @return
     */
    public boolean deleteData(String tableName,String key,String[] value){
        if(sqLiteDatabase.delete(tableName,key+"=?",value)==0){
            return false;
        }
        return true;
    }

    /**
     * Determine if the restaurant is the favorite
     * @param TRACKINGNUMBER
     * @return
     */
    public boolean isExistFavouriteRestaurant(String TRACKINGNUMBER){
        Cursor cursor=sqLiteDatabase.query(tableNames[2],new String[]{"TRACKINGNUMBER"},"TRACKINGNUMBER=?",new String[]{TRACKINGNUMBER},null,null,null,null);
        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
}
