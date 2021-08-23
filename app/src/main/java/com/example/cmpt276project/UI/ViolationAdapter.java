package com.example.cmpt276project.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cmpt276project.R;
import com.example.cmpt276project.tool.Time;

import java.util.ArrayList;

public class ViolationAdapter extends BaseAdapter {

    Time time=new Time();
    String TAG="error";
    private String[] violDescription;
    private ArrayList<String> info;
    private Context context;

    public ViolationAdapter(ArrayList<String> info, Context context){
        this.info=info;
        this.context=context;
    }
    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Object getItem(int position) {
        return info.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String _info = info.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.list_violations,parent,false);
        violDescription = context.getResources().getStringArray(R.array.violation_description_list);

        TextView text_violationDescription=convertView.findViewById(R.id.violationTxt);
        ImageView imageView_icon=convertView.findViewById(R.id.violImg);
        LinearLayout linearLayout_color=convertView.findViewById(R.id.criticalColour);
        ImageView image_danger_icon=convertView.findViewById(R.id.list_restaurant_image_dangerIcon);

        if(_info.toUpperCase().indexOf("EQUIPMENT")!=-1){
            imageView_icon.setImageResource(R.drawable.equipment);
            text_violationDescription.setText(context.getString(R.string.violation_description_equipment));
        }else if(_info.toUpperCase().indexOf("PEST")!=-1){
            imageView_icon.setImageResource(R.drawable.pests);
            text_violationDescription.setText(context.getString(R.string.violation_description_pest));
        }else if (_info.toUpperCase().indexOf("FOODSAFE")!=-1) {
            imageView_icon.setImageResource(R.drawable.foodsafe);
            text_violationDescription.setText(context.getString(R.string.violation_description_foodsafe));
        }else if(_info.toUpperCase().indexOf("FOOD")!=-1){
            imageView_icon.setImageResource(R.drawable.food);
            text_violationDescription.setText(context.getString(R.string.violation_description_food));
        }else if (_info.toUpperCase().indexOf("HYGIENE")!=-1) {
            imageView_icon.setImageResource(R.drawable.hygiene);
            text_violationDescription.setText(context.getString(R.string.violation_description_hygiene));
        }else if (_info.toUpperCase().indexOf("WASH")!=-1) {
            imageView_icon.setImageResource(R.drawable.hygiene);
            text_violationDescription.setText(context.getString(R.string.violation_description_wash));
        }else if (_info.toUpperCase().indexOf("MAINTAINED")!=-1) {
            imageView_icon.setImageResource(R.drawable.hammer);
            text_violationDescription.setText(context.getString(R.string.violation_description_maintained));
        }else if (_info.toUpperCase().indexOf("CHEMICALS")!=-1) {
            imageView_icon.setImageResource(R.drawable.chemical);
            text_violationDescription.setText(context.getString(R.string.violation_description_chemicals));
        }else if (_info.toUpperCase().indexOf("")!=-1) {
            imageView_icon.setImageResource(R.drawable.checkmark);
            text_violationDescription.setText(context.getString(R.string.no_violations_found));
        }

        //Comma separated
        String[] infos=_info.split(",");
        if(infos.length > 1) {
            switch (infos[1]) {
                case "Not Critical":
                    image_danger_icon.setImageResource(R.drawable.nodanger);
                    linearLayout_color.setBackgroundColor(context.getResources().getColor(R.color.noDanger));
                    break;
                case "Critical":
                    image_danger_icon.setImageResource(R.drawable.dangerhigh);
                    linearLayout_color.setBackgroundColor(context.getResources().getColor(R.color.DangerHigh));
                    break;
            }
        }else{
            image_danger_icon.setImageResource(R.drawable.nodanger);
            linearLayout_color.setBackgroundColor(context.getResources().getColor(R.color.noDanger));
        }



        return convertView;

    }

}
