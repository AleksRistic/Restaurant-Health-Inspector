package com.example.cmpt276project.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.cmpt276project.R;
import com.example.cmpt276project.struct.Restaurant;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class CustomInfoViewAdapter implements GoogleMap.InfoWindowAdapter{
    private final LayoutInflater mInflater;
    private String level;
    private ArrayList<Restaurant> restaurants;

    public CustomInfoViewAdapter(LayoutInflater inflater,ArrayList<Restaurant> restaurants) {
        this.mInflater = inflater;
        this.restaurants=restaurants;
    }

    @Override public View getInfoWindow(Marker marker) {

        final View popup = mInflater.inflate(R.layout.temp_window, null);

        TextView hazard = ((TextView) popup.findViewById(R.id.hazard_level_on_marker));
        for( Restaurant restaurant : restaurants){
            if(restaurant.getNAME().equals(marker.getTitle())
                    && restaurant.getPHYSICALADDRESS().equals(marker.getSnippet())){
                level = restaurant.getMostRecentInspection().getHazardRating();
                hazard.setBackgroundResource(R.color.noDanger);
                switch (level){
                    case "Moderate":
                        hazard.setBackgroundResource(R.color.DangerLow);
                        break;
                    case "High":
                        hazard.setBackgroundResource(R.color.DangerHigh);
                        break;
                }
            }
        }



        ((TextView) popup.findViewById(R.id.title_company)).setText(marker.getTitle());
        ((TextView) popup.findViewById(R.id.company_address_on_marker)).setText(marker.getSnippet());


        // linking the logos to the restaurant.
        ImageView logo = ((ImageView) popup.findViewById(R.id.company_logo_on_marker));
        logo.setImageResource(R.drawable.generic_restaurant);

        if(marker.getTitle().contains("A&W") || marker.getTitle().contains("A & W")){
            logo.setImageResource(R.drawable.a_and_w_logo);
        }
        else if(marker.getTitle().contains("McDonald")) {
            logo.setImageResource(R.drawable.mcdonalds);
        }
        else if(marker.getTitle().contains("Starbucks")) {
            logo.setImageResource(R.drawable.starbucks_logo);
        }
        else if(marker.getTitle().contains("Safeway")) {
            logo.setImageResource(R.drawable.safeway);
        }
        else if(marker.getTitle().contains("Save On Food")) {
            logo.setImageResource(R.drawable.save_on_foods_logo);
        }
        else if(marker.getTitle().contains("Panago")) {
            logo.setImageResource(R.drawable.panago_logo);
        }
        else if(marker.getTitle().contains("Subway")) {
            logo.setImageResource(R.drawable.subway);
        }
        else if(marker.getTitle().contains("Pizza Hut")) {
            logo.setImageResource(R.drawable.pizza_hut);
        }
        else if(marker.getTitle().contains("Tim Horton")) {
            logo.setImageResource(R.drawable.tim_hortons_logo);
        }
        else if(marker.getTitle().contains("Dairy Queen")) {
            logo.setImageResource(R.drawable.dairy_queen_logo);
        }
        else if(marker.getTitle().contains("7-Eleven")) {
            logo.setImageResource(R.drawable.seven_eleven_logo);
        }


        return popup;
        //return null;
    }

    @Override public View getInfoContents(Marker marker) {
        final View popup = mInflater.inflate(R.layout.temp_window, null);

        //((TextView) popup.findViewById(R.id.title_company)).setText(marker.ge);

        return popup;
    }


}
