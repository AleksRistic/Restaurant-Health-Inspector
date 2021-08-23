package com.example.cmpt276project.UI;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cmpt276project.R;
import com.example.cmpt276project.RestaurantListActivity;
import com.example.cmpt276project.data.SqlManager;
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.struct.Restaurant;
import com.example.cmpt276project.struct.RestaurantsManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RestaurantAdapter extends BaseAdapter {
    String TAG="error";
    private ArrayList<Restaurant> restaurants;
    private Context context;

    public RestaurantAdapter(ArrayList<Restaurant> restaurants,Context context){
        this.restaurants=restaurants;
        this.context=context;
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder{
        TextView name;
        TextView issuesFound;
        TextView dangerInfo;
        LinearLayout dangerColor;
        ImageView dangericon,favourite;
        TextView text_date;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Restaurant restaurant=restaurants.get(position);
        InspectionReports inspectionReports=getNewInspectionReport(restaurant.getTRACKINGNUMBER());

        ViewHolder viewHolder=null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_restaurant,parent,false);
            viewHolder=new ViewHolder();

            viewHolder.favourite=convertView.findViewById(R.id.list_info_image_favourite);
            viewHolder.name=convertView.findViewById(R.id.list_restaurant_text_name);
            viewHolder.issuesFound=convertView.findViewById(R.id.list_restaurant_text_issuesFound);
            viewHolder.dangerInfo=convertView.findViewById(R.id.list_restaurant_text_dangerInfo);
            viewHolder.dangerColor=convertView.findViewById(R.id.list_restaurant_layout_dangerColor);
            viewHolder.dangericon=convertView.findViewById(R.id.list_restaurant_image_dangerIcon);
            viewHolder.text_date=convertView.findViewById(R.id.list_text_date);

            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        if (SqlManager.getSqlManager().isExistFavouriteRestaurant(restaurant.getTRACKINGNUMBER())){
            viewHolder.favourite.setVisibility(View.VISIBLE);
        }else {
            viewHolder.favourite.setVisibility(View.GONE);
        }

        //add a judgment, cuz some restaurants do not have inspection reports
        if(inspectionReports!=null){
            switch (inspectionReports.getHazardRating()){
                case "Low":
                    viewHolder.dangerColor.setBackgroundColor(context.getResources().getColor(R.color.noDanger));
                    viewHolder.dangericon.setImageResource(R.drawable.nodanger);
                    break;
                case "Moderate":
                    viewHolder.dangerColor.setBackgroundColor(context.getResources().getColor(R.color.DangerLow));
                    viewHolder.dangericon.setImageResource(R.drawable.dangerlow);
                    break;
                case "High":
                    viewHolder.dangerColor.setBackgroundColor(context.getResources().getColor(R.color.DangerHigh));
                    viewHolder.dangericon.setImageResource(R.drawable.dangerhigh);
                    break;
            }
            viewHolder.issuesFound.setText(context.getString(R.string.issues_found_on_restaurantlist) + restaurant.getNumIssues());//+inspectionReports.getNumCritical()+inspectionReports.getNumNonCritical());

            //Gets the first violation
            String violations = inspectionReports.getViolLump()[0];

            viewHolder.dangerInfo.setText(violations);
            // load the date
            viewHolder.text_date.setText(inspectionReports.getDateForDisplay());
        }else{
            viewHolder.text_date.setVisibility(View.GONE);
            viewHolder.issuesFound.setText(context.getString(R.string.issues_found_on_restaurantlist) + 0);
            viewHolder.dangericon.setImageResource(R.drawable.nodanger);
            viewHolder.dangerColor.setBackgroundColor(Color.GREEN);
            viewHolder.dangerInfo.setText(context.getString(R.string.no_violations_found));
        }

        viewHolder.name.setText(restaurant.getNAME());
        ImageView logo = convertView.findViewById(R.id.list_restaurant_image);
        logo.setImageResource(R.drawable.generic_restaurant);

        if(restaurant.getNAME().contains("A&W") || restaurant.getNAME().contains("A & W")){
            logo.setImageResource(R.drawable.a_and_w_logo);
        }
        else if(restaurant.getNAME().contains("McDonald")) {
            logo.setImageResource(R.drawable.mcdonalds);
        }
        else if(restaurant.getNAME().contains("Starbucks")) {
            logo.setImageResource(R.drawable.starbucks_logo);
        }
        else if(restaurant.getNAME().contains("Safeway")) {
            logo.setImageResource(R.drawable.safeway);
        }
        else if(restaurant.getNAME().contains("Save On Foods")) {
            logo.setImageResource(R.drawable.save_on_foods_logo);
        }
        else if(restaurant.getNAME().contains("Panago")) {
            logo.setImageResource(R.drawable.panago_logo);
        }
        else if(restaurant.getNAME().contains("Subway")) {
            logo.setImageResource(R.drawable.subway);
        }
        else if(restaurant.getNAME().contains("Pizza Hut")) {
            logo.setImageResource(R.drawable.pizza_hut);
        }
        else if(restaurant.getNAME().contains("Tim Horton")) {
            logo.setImageResource(R.drawable.tim_hortons_logo);
        }
        else if(restaurant.getNAME().contains("Dairy Queen")) {
            logo.setImageResource(R.drawable.dairy_queen_logo);
        }
        else if(restaurant.getNAME().contains("7-Eleven")) {
            logo.setImageResource(R.drawable.seven_eleven_logo);
        }

        return convertView;
    }

    /**
     * get the latest inspection report
     * @param trackingNumber tracking num
     * @return new inspection report
     */
    private RestaurantsManager manager = RestaurantsManager.getInstance();
    private InspectionReports getNewInspectionReport(String trackingNumber){
        InspectionReports inspectionReport=null;
        int flag=0;
        for(InspectionReports inspectionReports: manager.getInspections()){
            try{
                if(inspectionReports.getTrackingNumber().equals(trackingNumber)){
                    if(flag!=0){
                        // compare the date
                        if(inspectionReport.getInspectionDate().getTime()< inspectionReports.getInspectionDate().getTime()){
                            inspectionReport=inspectionReports;
                        }
                    }else{
                        inspectionReport=inspectionReports;
                        flag=1;
                    }
                }
            }catch (Exception e){
                Log.e(TAG, "getNewInspectionReport: ",e );
            }
        }

        return inspectionReport;
    }


    /**
     * date change to string
     * @param date date
     * @return string
     */
    private String DateToString(Date date,String style){
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
    private Date StringToDate(String stime){
        DateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
        Date date=null;
        try {
            date=dateFormat.parse(stime);
        }catch (Exception e){
            Log.e(TAG, "StringToDate: ",e );
        }
        return date;
    }
}
