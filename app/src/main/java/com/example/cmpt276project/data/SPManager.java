package com.example.cmpt276project.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;


import com.example.cmpt276project.R;
import com.example.cmpt276project.tool.Time;

import java.util.Date;

/**
 * Expected put in field
 * run          Used to determine whether the first operation
 * updataDate   Data update time
 */

public class SPManager {
    //Create SharedPreferences object
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SPManager spManager=null;
    private Context context;
    private Time time=new Time();

    /**
     * Constructor initialization variable
     * @param context
     */
    private SPManager(Context context){
        this.context=context;
        sharedPreferences= context.getSharedPreferences("Data", context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    //Singleton mode
    public static SPManager getSPManager(Context context){
        if(spManager==null){
            spManager=new SPManager(context);
        }
        return spManager;
    }

    /**
     * Update data update time
     * @return
     */

    public boolean setUpdateDate(Date date){
        editor.putString("updateRestaurantDate",time.DateToString(date,"yyyy-MM-dd HH:mm:ss"));
        if (editor.commit()){
            return true;
        }
        return false;
    }


    public boolean setUpdateDateInsepections(Date date){
        editor.putString("updateInspectionsDate",time.DateToString(date,"yyyy-MM-dd HH:mm:ss"));
        if (editor.commit()){
            return true;
        }
        return false;
    }

    /**
     * Get data update time
     * @return
     */
    public Date getUpdateDateRestaurant(){
        return new Time().StringToDate(sharedPreferences.getString("updateRestaurantDate","2020-03-27 00:00:00"),"yyyy-MM-dd HH:mm:ss");
    }
    public Date getUpdateDateInspections(){
        return new Time().StringToDate(sharedPreferences.getString("updateInspectionsDate","2020-03-27 00:00:00"),"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Judge whether it is the first operation
     * @return
     */
    public boolean isFirstRun(){
        if(sharedPreferences.getInt("run",0)==0){
            editor.putInt("run",1);
            if(editor.commit()){
                return true;
            }else {
                Toast.makeText(context, R.string.app_start_failure,Toast.LENGTH_LONG).show();
                System.exit(1);
            }
        }
        return false;
    }

}
