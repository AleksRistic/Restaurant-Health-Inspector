package com.example.cmpt276project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cmpt276project.UI.ViolationAdapter;
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.tool.Time;
import java.util.ArrayList;

/**
 * Shows the violations on screen with icons and a short description of the violation.
 */

public class ViolationListActivity extends AppCompatActivity {
    ArrayList<InspectionReports> inspections;
    String[] violations;
    Time time = new Time();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// add the back pattern
        getSupportActionBar().setHomeButtonEnabled(true); // set back button usable
        assert getSupportActionBar() != null;

        /*
         * get Inspection from intent and get associated violation report.
         */
        Intent intent  = getIntent();
        int position  = intent.getIntExtra("position", 0);

        inspections = (ArrayList<InspectionReports>) intent.getSerializableExtra("inspection");
        violations = inspections.get(position).getViolLump();
        ArrayList<String> info = new ArrayList<>();
        for (int i = 0; i < violations.length; i++){
            info.add(violations[i]);

        }

        TextView date = findViewById(R.id.dateTxt);
        TextView inspectionType = findViewById(R.id.inspectionType);
        TextView hazardTxt = findViewById(R.id.hazardTxt);
        TextView critNum = findViewById(R.id.critNum);
        TextView nonCritNum = findViewById(R.id.nonCritNum);
        String inspectDate = time.DateToString(inspections.get(position).getInspectionDate() , "MMM dd, yyyy");
        date.setText(inspectDate);
        String inspectiontype = getString(R.string.inspection_type);
        String critFound = getString(R.string.critical_issues_found);
        String nonCritFound = getString(R.string.non_critical_issues_found);
        String hazard = getString(R.string.hazard_on_viol);
        inspectionType.setText(inspectiontype + inspections.get(position).getInspType());
        critNum.setText(critFound + inspections.get(position).getNumCritical());
        nonCritNum.setText(nonCritFound + inspections.get(position).getNumNonCritical());
        hazardTxt.setText(hazard + inspections.get(position).getHazardRating());

        ImageView imageView_icon= findViewById(R.id.hazardImg);
        TextView linearLayout_color= findViewById(R.id.hazardColour);


        switch (inspections.get(position).getHazardRating()){
            case "Low":
                linearLayout_color.setBackgroundColor(this.getResources().getColor(R.color.noDanger));
                imageView_icon.setImageResource(R.drawable.nodanger);
                break;
            case "Moderate":
                linearLayout_color.setBackgroundColor(this.getResources().getColor(R.color.DangerLow));
                imageView_icon.setImageResource(R.drawable.dangerlow);
                break;
            case "High":
                linearLayout_color.setBackgroundColor(this.getResources().getColor(R.color.DangerHigh));
                imageView_icon.setImageResource(R.drawable.dangerhigh);
                break;
        }


        loadList(info);
        displayViolationToast(this);
    }

    public void loadList(ArrayList info){
        ListView listView = findViewById(R.id.violationList);
        listView.setAdapter(new ViolationAdapter(info, this));

    }

    /*
     * Display full violation text as Toast message.
     * */
    public void displayViolationToast(final Context context){
        ListView violation_list = findViewById(R.id.violationList);
        violation_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "" + violations[position], Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
