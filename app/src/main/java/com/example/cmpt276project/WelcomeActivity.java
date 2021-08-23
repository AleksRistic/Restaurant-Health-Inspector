package com.example.cmpt276project;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cmpt276project.UI.RestaurantAdapter;
import com.example.cmpt276project.data.SPManager;
import com.example.cmpt276project.data.SqlManager;
import com.example.cmpt276project.fileHandle.AssetsFileHandle;
import com.example.cmpt276project.fileHandle.fetchData;
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.struct.Restaurant;
import com.example.cmpt276project.struct.RestaurantsManager;
import com.example.cmpt276project.tool.NetWork;
import com.example.cmpt276project.tool.Time;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Update code
 * Be able to modify existing data and insert data not available
 */
public class WelcomeActivity extends AppCompatActivity {

    private static String[] PERMISSIONS;
    public final static long SECOND_MILLIS = 1000;
    public final static long MINUTE_MILLIS = SECOND_MILLIS*60;
    public final static long HOUR_MILLIS = MINUTE_MILLIS*60;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private String PATH_UPDATE_RESTAURANTS;
    private String PATH_UPDATE_INSPECTION;
    private boolean isUpdate=false;
    fetchData process = new fetchData();
    String TAG="WelcomeActivity";
    private boolean isUpdataFavourite=false;//TODO:add
    ArrayList<String> updataTRACKINGNUMBER=new ArrayList<>();

    Time time=new Time();

    RestaurantsManager restaurantsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        try {
            Integer i = process.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PATH_UPDATE_RESTAURANTS = process.csvUrl;
        PATH_UPDATE_INSPECTION = process.inspectionsCsvUrl;
        //Initialize the database
        SqlManager.getSqlManager(this);
        System.out.println(SPManager.getSPManager(this).getUpdateDateRestaurant());
        //This is the permission to apply, which can be adjusted according to the Android version
        if(android.os.Build.VERSION.SDK_INT>28){
            PERMISSIONS= new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        }else{
            PERMISSIONS= new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }

        networkChack();
    }

    /**
     * Check the network
     */
    private void networkChack(){
        //Determine whether to connect to the network
        if(NetWork.isLink(this)){
            int NETWORK= NetWork.DATA_NETWORK;
            //Judge network type
            if(NetWork.getNetWorkType(this)==NetWork.WIFI){
                NETWORK=NetWork.WIFI;
            }else if (NetWork.getNetWorkType(this)==NetWork.DATA_NETWORK){
                NETWORK=NetWork.DATA_NETWORK;
            }
            update(NETWORK);

        }else{
            if(SPManager.getSPManager(this).isFirstRun()){
                AlertDialog.Builder builder  = new AlertDialog.Builder(WelcomeActivity.this);
                builder.setTitle(getString(R.string.no_network) ) ;
                builder.setMessage(getString(R.string.internet_needed) ) ;
                builder.setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        networkChack();
                    }
                });
                builder.show();
            }else {
                Toast.makeText(this,getString(R.string.network_needed),Toast.LENGTH_LONG).show();
                start();
            }
        }
    }

    /**
     * Program startup
     */
    private void start(){
        if(isServicesOK()){//Judge Google services
            if(permissionCheck()){//Judgement authority
                startGo();
            }else {
                AlertDialog.Builder builder  = new AlertDialog.Builder(WelcomeActivity.this);
                builder.setTitle(getString(R.string.permissions_needed) ) ;
                builder.setMessage(getString(R.string.location_needed) ) ;
                builder.setPositiveButton(getString(R.string.allow), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPermission();
                    }
                });
                builder.show();
            }
        }
    }

    /**
     * Update module
     * @param network
     *
     * Listing some of the variables you will be working with:
     *  process.dateModifiedToDate -> Date of the newest restaurant report retrieved from the JSON file.
     *  inspectionsDateModifiedToDate -> Date of the newest inspections report retrieved from the JSON file.
     *
     */
    private void update(int network){


        //First run update data
        if (SPManager.getSPManager(this).isFirstRun()){
            downloadFromCSV();
        }
        //Time interval between judgment and last update
        //getting today's date
        long millis=System.currentTimeMillis();
        java.util.Date date=new java.util.Date(millis);

        // Comparing Today's date with the date of the last restaurant file/ inspection file. These dates are just constants at the moment.
        long time_difference_restaurants = (int)((date.getTime()/HOUR_MILLIS)- (SPManager.getSPManager(this).getUpdateDateRestaurant().getTime()/HOUR_MILLIS));// (process.LAST_MODIFIED_DATE.getTime()/HOUR_MILLIS));
        long time_difference_inspections = (int)((date.getTime()/HOUR_MILLIS ) - (SPManager.getSPManager(this).getUpdateDateInspections().getTime()/HOUR_MILLIS));//(process.LAST_MODIFIED_DATE_INSPECTIONS.getTime()/HOUR_MILLIS));
        if(time_difference_restaurants > 20 || time_difference_inspections > 20 ){
            isUpdate=true;

        }else {
            start();
        }


        if(isUpdate){
            final AlertDialog.Builder builder  = new AlertDialog.Builder(WelcomeActivity.this);
            builder.setTitle(getString(R.string.updates_to_download)) ;
            if(network==NetWork.WIFI){
                builder.setMessage(getString(R.string.want_to_download) ) ;
            }else if(network==NetWork.DATA_NETWORK){
                builder.setMessage(getString(R.string.data_traffic) ) ;
            }

            builder.setPositiveButton(getString(R.string.download), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showDownloadDialog(getString(R.string.download_new_data),false);
                    long millis=System.currentTimeMillis();
                    java.util.Date date=new java.util.Date(millis);
                    SPManager.getSPManager(WelcomeActivity.this).setUpdateDate(date);
                    SPManager.getSPManager(WelcomeActivity.this).setUpdateDateInsepections(date);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //download from csv.
                    start();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    //Enumeration variables
    int rest_num=0;
    int ins_num=0;
    Handler handler;
    Thread download_thread;
    /**
     * Pop up download information
     * @return boolean
     */
    private boolean showDownloadDialog(String title,boolean isFirst) {
        final AlertDialog.Builder setDownloadDialog = new AlertDialog.Builder(this);
        //Acquisition interface
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_download_loading, null);
        //Fill the interface into the alertdialog container
        setDownloadDialog.setView(dialogView);
        //Cancel clicking the external disappearing pop-up window
        setDownloadDialog.setCancelable(false);
        //Create alertdialog
        setDownloadDialog.create();
        //Display pop-up
        final AlertDialog alertDialog = setDownloadDialog.show();

        //Get control
        TextView text_title=dialogView.findViewById(R.id.dialog_download_loading_text_title);
        final TextView text_rest_num=dialogView.findViewById(R.id.dialog_download_loading_text_restaurant_num);
        final TextView text_ins_num=dialogView.findViewById(R.id.dialog_download_loading_text_inspection_num);
        final ProgressBar progressBar_restaurant=dialogView.findViewById(R.id.dialog_download_loading_bar_restaurant);
        final ProgressBar progressBar_inspection=dialogView.findViewById(R.id.dialog_download_loading_bar_inspection);
        final ImageView imageView_restaurant=dialogView.findViewById(R.id.dialog_download_loading_image_restaurant);
        final ImageView imageView_inspection=dialogView.findViewById(R.id.dialog_download_loading_image_inspection);
        ImageButton imageButton_close=dialogView.findViewById(R.id.dialog_download_loading_button_close);

        text_title.setText(title);
        imageView_inspection.setVisibility(View.GONE);
        imageView_restaurant.setVisibility(View.GONE);

        //Update cannot be interrupted in the first run
        if (isFirst){
            imageButton_close.setVisibility(View.GONE);
        }

        imageButton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder  = new AlertDialog.Builder(WelcomeActivity.this);
                builder.setTitle(getString(R.string.downloading_updates) ) ;
                builder.setMessage(getString(R.string.ask_to_terminate) ) ;
                builder.setPositiveButton(getString(R.string.terminate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        download_thread.interrupt();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        /**
         * Handling UI update events
         */
        handler=new Handler(){
            public void handleMessage(Message message){
                switch (message.what){
                    case 1:
                        text_rest_num.setText(rest_num+"");
                        break;
                    case 2:
                        text_ins_num.setText(ins_num+"");
                        break;
                }
            }
        };

        //Read completed in thread

        download_thread=new Thread(new Runnable() {

            @Override
            public void run() {
                SqlManager sqlManager=SqlManager.getSqlManager(WelcomeActivity.this);
                String[] restaurant_style={"TRACKINGNUMBER","NAME","PHYSICALADDRESS","PHYSICALCITY","FACTYPE","LATITUDE","LONGITUDE"};
                String[] inspection_style={"ID","TrackingNumber","InspectionDate","InspType","NumCritical","NumNonCritical","HazardRating","ViolLump"};
                try {
                    //创Construction flow
                    URL URL_PATH_UPDATE_RESTAURANTS = new URL(PATH_UPDATE_RESTAURANTS);
                    URL URL_PATH_UPDATE_INSPECTION = new URL(PATH_UPDATE_INSPECTION);
                    BufferedReader bufferedReader_restaurants = new BufferedReader(new InputStreamReader(URL_PATH_UPDATE_RESTAURANTS.openStream()));
                    BufferedReader bufferedReader_inspection = new BufferedReader(new InputStreamReader(URL_PATH_UPDATE_INSPECTION.openStream()));

                    String readline=null;
                    //Get restaurant updates

                    //delete previous data
                    sqlManager.deleteRows();

                    while ((readline=bufferedReader_restaurants.readLine())!=null){
                        //Judge whether it is interrupted
                        if(Thread.currentThread().isInterrupted()){
                            break;
                        }

                        //In thread update interface
                        Message message=new Message();
                        message.what=1;
                        handler.sendMessage(message);

                        if(!readline.split(",")[0].equals("\"TRACKINGNUMBER\"")){
                            String[] infos=readline.split(",");
                            ContentValues contentValues=new ContentValues();
                            //Determine whether the restaurant already exists
                            if(sqlManager.isExistRestaurant(infos[0].replace("\"",""))){
                                //Restaurant update data
                                for(int i=1;i<restaurant_style.length;i++){
                                    contentValues.put(restaurant_style[i],infos[i].replace("\"",""));
                                }
                                sqlManager.updateData(SqlManager.getTableNames()[0],restaurant_style[0],new String[]{infos[0].replace("\"","")},contentValues);//refresh data
                            }else {
                                //insert data
                                for(int i=0;i<restaurant_style.length;i++){
                                    contentValues.put(restaurant_style[i],infos[i].replace("\"",""));
                                }
                                sqlManager.insertData(SqlManager.getTableNames()[0],contentValues);
                            }
                        }

                        rest_num++;
                    }

                    //In thread update interface
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar_restaurant.setVisibility(View.GONE);
                            imageView_restaurant.setVisibility(View.VISIBLE);
                        }
                    });

                    readline=null;

                    System.out.println("_____________________________________________________________________________________________");
                    //Get check update information
                    int id = 1;
                    while ((readline=bufferedReader_inspection.readLine())!=null){
                        //Judge whether it is interrupted
                        if(Thread.currentThread().isInterrupted()){
                            break;
                        }

                        //In thread update interface
                        Message message=new Message();
                        message.what=2;
                        handler.sendMessage(message);

                        String[] readlines=readline.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");

                        if(!(readlines.length>0)){
                            continue;
                        }

                        if(!readlines[0].toUpperCase().equals(("TrackingNumber").toUpperCase())){//除去表格头

                            String[] infos=readline.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                            if(infos.length>5){
                                //Since some don't have dangerous information, we have to make a judgment
                                String ViolLump="";
                                int hazardRatingColumn=5;
                                if(infos.length==7){
                                    ViolLump=infos[5].replace("\"","");
                                    hazardRatingColumn=6;
                                }
                                String[] cache=new String[8];
                                for(int i=0;i<6;i++){
                                    cache[i+1]=infos[i].replace("\"","");
                                }
                                cache[0] = String.valueOf(id);
                                cache[7]=ViolLump;
                                //Judge whether the information already exists
                                if(!sqlManager.isExistInspectionReport(cache)){
                                    ContentValues contentValues=new ContentValues();
                                    contentValues.put(inspection_style[0], id);
                                    //"ID","TrackingNumber","InspectionDate","InspType","NumCritical","NumNonCritical","HazardRating","ViolLump"
                                    contentValues.put(inspection_style[1], infos[0]);
                                    contentValues.put(inspection_style[2], infos[1]);
                                    contentValues.put(inspection_style[3], infos[2]);
                                    contentValues.put(inspection_style[4], infos[3]);
                                    contentValues.put(inspection_style[5], infos[4]);
                                    contentValues.put(inspection_style[6], infos[hazardRatingColumn]);
                                    contentValues.put(inspection_style[7],ViolLump);
                                    //TODO:add
                                    updataTRACKINGNUMBER.add(infos[0]);

                                    /*
                                    for(int i=1;i<inspection_style.length-1;i++){
                                        contentValues.put(inspection_style[i],infos[i-1].replace("\"",""));
                                    }
                                     */
                                    System.out.println(contentValues.toString());
                                    sqlManager.insertData(SqlManager.getTableNames()[1],contentValues);//insert data
                                }
                                id++;
                            }
                        }
                        ins_num++;
                    }
                    //In thread update interface
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar_inspection.setVisibility(View.GONE);
                            imageView_inspection.setVisibility(View.VISIBLE);
                            Toast.makeText(WelcomeActivity.this,getString(R.string.update_complete),Toast.LENGTH_LONG).show();
                            //SPManager.getSPManager(WelcomeActivity.this).setUpdateDate();//Update update time
                            alertDialog.dismiss();//Close the window
                            start();//Update completed
                        }
                    });


                } catch (MalformedURLException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WelcomeActivity.this,getString(R.string.update_failed),Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();//Close the window
                            start();//Update completed
                        }
                    });

                    Log.e(TAG, "run: ",e );
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WelcomeActivity.this,getString(R.string.update_failed),Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();//Close the window
                            start();//Update completed
                        }
                    });

                    Log.e(TAG, "run: ",e );
                }

            }
        });

        download_thread.start();//Startup thread

        return true;
    }


    /**
     * Get authority
     */
    private void getPermission(){
        ActivityCompat.requestPermissions(this,PERMISSIONS, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int i=0;i<PERMISSIONS.length;i++){
            if(grantResults[i]==-1){
                Toast.makeText(this,PERMISSIONS[i]+ getString(R.string.permission_rejected),Toast.LENGTH_SHORT).show();
                goIntentSetting();
            }
        }

        startGo();
    }

    /**
     * Jump to permission setting interface
     */
    private void goIntentSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        try {
            this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check necessary authority
     * @return
     */
    private boolean permissionCheck(){
        for(String per:PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, per)!= PackageManager.PERMISSION_GRANTED ){
                return false;
            }
        }
        return true;
    }

    /**
     * Google service check
     * @return
     */
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //TODO:check whether have new inspection on favourite list
    private void isUpdataFavourite(){
        ArrayList<Restaurant> restaurants=new ArrayList<>();

        for(Restaurant restaurant:SqlManager.getSqlManager().queryFavouriteRestaurant()){

            for(String TRACKINGNUMBER:updataTRACKINGNUMBER){
                if (restaurant.getTRACKINGNUMBER().equals(TRACKINGNUMBER)){
                    restaurants.add(restaurant);
                }
            }

            ArrayList<Restaurant> newRestaurants=SqlManager.getSqlManager().queryRestaurant("TRACKINGNUMBER",restaurant.getTRACKINGNUMBER());
            if (newRestaurants.size()!=0){
                Restaurant newRestaurant=newRestaurants.get(0);
                if (!restaurant.toString().equals(newRestaurant.toString())){
                    restaurants.add(newRestaurant);
                    SqlManager.getSqlManager().deleteData(SqlManager.getTableNames()[2],"TRACKINGNUMBER",new String[]{restaurant.getTRACKINGNUMBER()});//删除原有数据

                    ContentValues contentValues=new ContentValues();
                    contentValues.put("TRACKINGNUMBER",newRestaurant.getTRACKINGNUMBER());
                    contentValues.put("NAME", newRestaurant.getNAME());
                    contentValues.put("PHYSICALADDRESS",newRestaurant.getPHYSICALADDRESS());
                    contentValues.put("PHYSICALCITY",newRestaurant.getPHYSICALCITY());
                    contentValues.put("FACTYPE",newRestaurant.getFACTYPE());
                    contentValues.put("LATITUDE",newRestaurant.getLATITUDE());
                    contentValues.put("LONGITUDE",newRestaurant.getLONGITUDE());

                    SqlManager.getSqlManager().insertData(SqlManager.getTableNames()[2],contentValues);
                }
            }
        }

        if (restaurants.size()!=0){
            dialogUpdataFavourite(restaurants);
        }
    }

    //TODO:show update favourite list
    private void dialogUpdataFavourite(ArrayList<Restaurant> restaurants){
        final AlertDialog.Builder setDownloadDialog = new AlertDialog.Builder(this);
        //Acquisition interface
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tip_favourite_updata, null);
        //Fill the interface into the alertdialog container
        setDownloadDialog.setView(dialogView);
        //Cancel clicking the external disappearing pop-up window
        setDownloadDialog.setCancelable(false);
        //Create alertdialog
        setDownloadDialog.create();
        //Display pop-up
        final AlertDialog alertDialog = setDownloadDialog.show();

        ListView list_list=dialogView.findViewById(R.id.dialog_tip_favourite_updata_list);
        Button button_ok=dialogView.findViewById(R.id.dialog_tip_favourite_updata_button_ok);

        list_list.setAdapter(new RestaurantAdapter(restaurants,this));

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    /**
     * Animation and jump codes
     */
    private void startGo(){
        //TODO：here
        isUpdataFavourite();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SqlManager manager = SqlManager.getSqlManager(WelcomeActivity.this);
                restaurantsManager = RestaurantsManager.getInstance(manager.queryRestaurantsData(),
                        manager.queryInspectionReportsData());
            }
        });

        setupSkipButton();

        AnimationSet animationSet=new AnimationSet(false);

        Animation alphaAnimation=new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(1500);

        Animation rotateAnimation=new RotateAnimation(0,360);
        rotateAnimation.setDuration(1500);

        Animation scaleAnimation=new ScaleAnimation(0.5f,1,0.5f,1);
        scaleAnimation.setDuration(1500);

        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        go();
                    }
                };

                Handler h = new Handler();
                // The Runnable will be executed after the given delay time
                h.postDelayed(r, 4000);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ImageView image=findViewById(R.id.welcome);
        image.startAnimation(animationSet);

    }

    private void setupSkipButton() {
        Button skip=findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });
    }

    int flag=0;
    private void go(){
        if(isUpdate){
            Intent intent = new Intent(WelcomeActivity.this, NewInspectionsActivity.class);
            startActivity(intent);
        }
        if(flag==0){
            startActivity(new Intent(WelcomeActivity.this, MapsActivity.class));
            flag=1;
            finish();
        }
    }

    private void downloadFromCSV(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code

        AssetsFileHandle fileHandler = AssetsFileHandle.getAssetsFileHandle();
        fileHandler.start(WelcomeActivity.this);

        List<Restaurant> restaurants = fileHandler.getRestaurantInfo();
        List<InspectionReports> inspections = fileHandler.getInspectionReports();

        SqlManager db =SqlManager.getSqlManager(WelcomeActivity.this);

        String[] restaurant_style={"TRACKINGNUMBER","NAME","PHYSICALADDRESS","PHYSICALCITY","FACTYPE","LATITUDE","LONGITUDE"};
        String[] inspection_style={"ID","TrackingNumber","InspectionDate","InspType","NumCritical","NumNonCritical","HazardRating","ViolLump"};

        for(Restaurant restaurant : restaurants){
            ContentValues contents = new ContentValues();
            contents.put(restaurant_style[0], restaurant.getTRACKINGNUMBER());
            contents.put(restaurant_style[1], restaurant.getNAME());
            contents.put(restaurant_style[2], restaurant.getPHYSICALADDRESS());
            contents.put(restaurant_style[3], restaurant.getPHYSICALCITY());
            contents.put(restaurant_style[4], restaurant.getFACTYPE());
            contents.put(restaurant_style[5], restaurant.getLATITUDE());
            contents.put(restaurant_style[6], restaurant.getLONGITUDE());
            db.insertData(SqlManager.getTableNames()[0], contents);
        }

        int id = 1;
        for(InspectionReports inspection : inspections ){
            ContentValues contents = new ContentValues();
            contents.put(inspection_style[0], id);
            contents.put(inspection_style[1], inspection.getTrackingNumber());
            contents.put(inspection_style[2], getInspectionDate(inspection.getInspectionDate(), "yyyyMMdd" ));
            contents.put(inspection_style[3], inspection.getInspType());
            contents.put(inspection_style[4], inspection.getNumCritical());
            contents.put(inspection_style[5], inspection.getNumNonCritical());
            contents.put(inspection_style[6], inspection.getHazardRating());
            contents.put(inspection_style[7], getViolDump(inspection.getViolLump()) );
            db.insertData(SqlManager.getTableNames()[1], contents);
            id++;
        }
            }
        });
    }

    private String getViolDump(String[] arr){
        StringBuilder vioDump = new StringBuilder();
        for(String string : arr){
            vioDump.append(string);
        }
        return vioDump.toString();
    }
    public String getInspectionDate(Date date, String format){
        Time time = new Time();
        return time.DateToString(date, format);
    }

}

