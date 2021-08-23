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
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.tool.Time;

import java.util.ArrayList;

public class InspectionReportAdapter extends BaseAdapter {
    Time time=new Time();
    String TAG="error";
    private ArrayList<InspectionReports> inspections;
    private Context context;

    public InspectionReportAdapter(ArrayList<InspectionReports> inspections, Context context){
        this.inspections=inspections;
        this.context=context;
    }

    @Override
    public int getCount() {
        return inspections.size();
    }

    @Override
    public Object getItem(int position) {
        return inspections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InspectionReports inspectionReport=inspections.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.list_inspectionreport,parent,false);

        TextView text_criticalissuesfound=convertView.findViewById(R.id.info_text_criticalissuesfound);
        TextView text_noncriticalissuesfound=convertView.findViewById(R.id.info_text_noncriticalissuesfound);
        TextView text_date=convertView.findViewById(R.id.list_inspectionreport_text_time);
        ImageView imageView_icon=convertView.findViewById(R.id.list_inspectionreport_image_icon);
        LinearLayout linearLayout_color=convertView.findViewById(R.id.list_inspectionreport_linearlayout_color);

        switch (inspectionReport.getHazardRating()){
            case "Low":
                linearLayout_color.setBackgroundColor(context.getResources().getColor(R.color.noDanger));
                imageView_icon.setImageResource(R.drawable.nodanger);
                break;
            case "Moderate":
                linearLayout_color.setBackgroundColor(context.getResources().getColor(R.color.DangerLow));
                imageView_icon.setImageResource(R.drawable.dangerlow);
                break;
            case "High":
                linearLayout_color.setBackgroundColor(context.getResources().getColor(R.color.DangerHigh));
                imageView_icon.setImageResource(R.drawable.dangerhigh);
                break;
        }

        text_criticalissuesfound.setText(context.getString(R.string.critical_issues_found) +inspectionReport.getNumCritical());
        text_noncriticalissuesfound.setText(context.getString(R.string.non_critical_issues_found)+inspectionReport.getNumNonCritical());
        text_date.setText(inspectionReport.getDateForDisplay());
        return convertView;
    }
}
