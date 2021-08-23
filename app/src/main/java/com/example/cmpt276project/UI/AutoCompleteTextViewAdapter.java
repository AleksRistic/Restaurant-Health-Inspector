package com.example.cmpt276project.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.cmpt276project.R;
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.struct.Restaurant;
import com.example.cmpt276project.struct.RestaurantsManager;

import java.util.ArrayList;
import java.util.Date;

//TODO: New class

/**
 * Map interface auto-complete search box adapter
 */

public class AutoCompleteTextViewAdapter extends BaseAdapter implements Filterable {
    String TAG="AutoCompleteTextViewAdapter";
    ArrayList<Restaurant> restaurantArrayList=new ArrayList<>();
    ArrayList<Restaurant> restaurants=new ArrayList<>();
    Context context;
    private LayoutInflater inflater=null;
    private Filter filter;

    public AutoCompleteTextViewAdapter(Context context,ArrayList<Restaurant> restaurantArrayList){
        this.restaurantArrayList=restaurantArrayList;
        restaurants=restaurantArrayList;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return restaurantArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurantArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Return to the corresponding restaurant
     * @param postion
     * @return
     */
    public Restaurant getItemRestaurant(int postion){
        return restaurantArrayList.get(postion);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Restaurant restaurant=restaurantArrayList.get(position);
        ArrayList<InspectionReports> inspectionReports= RestaurantsManager.getInstance().getInspectionReports(restaurant);

        ViewHolder viewHolder=null;
        if (convertView==null){
            convertView=inflater.inflate(R.layout.list_auto_edit, null);
            viewHolder=new ViewHolder();
            viewHolder.text_name=convertView.findViewById(R.id.list_auto_edit_text_name);
            viewHolder.text_level=convertView.findViewById(R.id.list_auto_edit_text_level);
            viewHolder.text_num=convertView.findViewById(R.id.list_auto_edit_text_num);

            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        //Restaurant name
        viewHolder.text_name.setText(restaurant.getNAME());
        //Check the hazard level recently
        if (getLatelyInspectionReport(inspectionReports)==null){
            viewHolder.text_level.setText("Most recent hazard level: None");
        }else {
            viewHolder.text_level.setText("Most recent hazard level：" + getLatelyInspectionReport(inspectionReports).getHazardRating());
        }

        //get number of serious violations in the past year
        int num=getNumberOfViolationsInThePastYear(inspectionReports);
        if (num<=5){
            viewHolder.text_num.setText("Number of violations in the last year："+num);
        }else {
            viewHolder.text_num.setText(R.string.overFiveViolations);

        }

        return convertView;
    }

    /**
     * get number of violations in the recent year
     * @param inspectionReportsArrayList
     * @return
     */
    private int getNumberOfViolationsInThePastYear(ArrayList<InspectionReports> inspectionReportsArrayList){
        int num=0;
        for (InspectionReports inspectionReports:inspectionReportsArrayList){
            long day=(new Date().getTime()-inspectionReports.getInspectionDate().getTime())/1000/60/60/24;
            //Judging time
            if (day<365){
                if (inspectionReports.getHazardRating().equals("High")){
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Get the latest inspection report
     * @return
     */
    private InspectionReports getLatelyInspectionReport(ArrayList<InspectionReports> reportsArrayList){
        InspectionReports inspectionReports=null;
        if (reportsArrayList.size()!=0){
            inspectionReports=reportsArrayList.get(0);
            for (InspectionReports inspectionReport:reportsArrayList){
                if (inspectionReports.getInspectionDate().getTime()<inspectionReport.getInspectionDate().getTime()){
                    inspectionReports=inspectionReport;
                }
            }
        }
        return inspectionReports;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MyFilter();
        }
        return filter;
    }

    class ViewHolder{
        TextView text_name,text_level,text_num;
    }

    private ArrayList<Restaurant> getRestaurants(){
        return restaurants;
    }

    //filter
    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            FilterResults results = new FilterResults();

            ArrayList<Restaurant> restaurants=new ArrayList<>();

            String name=prefix.toString().toUpperCase();
            for (Restaurant restaurant:getRestaurants()){
                if (restaurant.getNAME().toUpperCase().indexOf(name)!=-1){
                    restaurants.add(restaurant);
                }
            }
            // Then assign this new collection data to the FilterResults object
            results.values = restaurants;
            results.count = restaurants.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // Re-assign the List associated with the adapter
            if (results.values!=null){
                restaurantArrayList = (ArrayList<Restaurant>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }

    }
}

