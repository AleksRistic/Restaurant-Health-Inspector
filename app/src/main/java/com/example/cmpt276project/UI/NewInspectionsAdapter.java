package com.example.cmpt276project.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cmpt276project.InfoActivity;
import com.example.cmpt276project.R;
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.struct.Restaurant;
import com.example.cmpt276project.struct.RestaurantsManager;
import com.google.android.gms.common.util.ArrayUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NewInspectionsAdapter extends BaseAdapter {
    private RestaurantsManager restaurantsManager;
    private ArrayList<Restaurant> restaurantArrayList;
    private ArrayList<Restaurant> FavRestList;
    private ArrayList<Restaurant> Temp;
    private InfoActivity infoActivity;
    private InspectionReports inspectionReports;
    private ArrayList<Restaurant> restaurants;
    private Context context;

    public NewInspectionsAdapter(ArrayList<Restaurant> restaurants, Context context) {
        this.restaurants = restaurants;
        this.context = context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        TODO:
         *      1. Get the new list of Inspections that were downloaded from server
         *      2. Compare these Inspections tracking number to the restaurants tracking number currently in the Favourites list
         *      3. Make a list of the restaurants
         */

        convertView = LayoutInflater.from(context).inflate(R.layout.list_new_inspections, parent, false);
        TextView name = convertView.findViewById(R.id.list_restaurant_text_name);
        TextView issuesFound = convertView.findViewById(R.id.list_restaurant_text_issuesFound);
        TextView dangerInfo = convertView.findViewById(R.id.list_restaurant_text_dangerInfo);
        LinearLayout dangerColor = convertView.findViewById(R.id.list_restaurant_layout_dangerColor);
        ImageView dangericon = convertView.findViewById(R.id.list_restaurant_image_dangerIcon);
        TextView text_date = convertView.findViewById(R.id.list_text_date);
        String isFound = context.getString(R.string.issues_found);
        ImageView fav = convertView.findViewById(R.id.favourite);
/*
        restaurantArrayList = new ArrayList<>();
        for (int i = 0; i < Temp.size(); i++) {
            for (int j = 0; j < restaurants.size(); j++) {
                // Compare the restaurants in the fav list with the restaurants in the updated restaurant list.
                if (restaurants.get(j).getNAME().equals(Temp.get(i).getNAME())
                        && restaurants.get(j).getTRACKINGNUMBER().equals(Temp.get(i).getTRACKINGNUMBER())) {
                    System.out.println(restaurants.get(j) + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    restaurantArrayList.add(restaurants.get(j));

                }
            }
        }
*/
        for(Restaurant rest : restaurants) {
            try {
                System.out.println(rest + "---------------------------------------------------------------------------------------");
                inspectionReports = rest.getMostRecentInspection();
                if (inspectionReports != null) {
                    System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
                    switch (inspectionReports.getHazardRating()) {
                        case "Low":
                            dangerColor.setBackgroundColor(context.getResources().getColor(R.color.noDanger));
                            dangericon.setImageResource(R.drawable.nodanger);
                            break;
                        case "Moderate":
                            dangerColor.setBackgroundColor(context.getResources().getColor(R.color.DangerLow));
                            dangericon.setImageResource(R.drawable.dangerlow);
                            break;
                        case "High":
                            dangerColor.setBackgroundColor(context.getResources().getColor(R.color.DangerHigh));
                            dangericon.setImageResource(R.drawable.dangerhigh);
                            break;
                    }

                    issuesFound.setText(isFound + rest.getNumIssues());
                    String violations = inspectionReports.getViolLump()[0];

                    dangerInfo.setText(violations);
                    // load the date
                    text_date.setText(inspectionReports.getDateForDisplay());
                    fav.setImageResource(R.drawable.ic_favorite_black_24dp);
                    name.setText(rest.getNAME());
                    ImageView logo = convertView.findViewById(R.id.list_restaurant_image);

                    if (rest.getNAME().contains("A&W") || rest.getNAME().contains("A & W")) {
                        logo.setImageResource(R.drawable.a_and_w_logo);
                    } else if (rest.getNAME().contains("McDonald")) {
                        logo.setImageResource(R.drawable.mcdonalds);
                    } else if (rest.getNAME().contains("Starbucks")) {
                        logo.setImageResource(R.drawable.starbucks_logo);
                    } else if (rest.getNAME().contains("Safeway")) {
                        logo.setImageResource(R.drawable.safeway);
                    } else if (rest.getNAME().contains("Save On Foods")) {
                        logo.setImageResource(R.drawable.save_on_foods_logo);
                    } else if (rest.getNAME().contains("Panago")) {
                        logo.setImageResource(R.drawable.panago_logo);
                    } else if (rest.getNAME().contains("Subway")) {
                        logo.setImageResource(R.drawable.subway);
                    } else if (rest.getNAME().contains("Pizza Hut")) {
                        logo.setImageResource(R.drawable.pizza_hut);
                    } else if (rest.getNAME().contains("Tim Horton")) {
                        logo.setImageResource(R.drawable.tim_hortons_logo);
                    } else if (rest.getNAME().contains("Dairy Queen")) {
                        logo.setImageResource(R.drawable.dairy_queen_logo);
                    } else if (rest.getNAME().contains("7-Eleven")) {
                        logo.setImageResource(R.drawable.seven_eleven_logo);
                    }
                }
            }catch(Exception e){

            }

        }
        return convertView;
    }

}
