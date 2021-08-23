package com.example.cmpt276project;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.example.cmpt276project.UI.InspectionReportAdapter;
import com.example.cmpt276project.data.SqlManager;
import com.example.cmpt276project.struct.InspectionReports;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {
    TextView text_info;
    ListView list_list;
    TextView text_title;

    FloatingActionButton button_fab;
    boolean isFavorite=false;

    ArrayList<InspectionReports> inspection;
    NestedScrollView nestedScrollView;
    MapsActivity mapsActivity = new MapsActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// add the back pattern
        getSupportActionBar().setHomeButtonEnabled(true); // set back button usable

        final Intent intent=getIntent();
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.info_collapsing);
        collapsingToolbarLayout.setTitle(intent.getStringExtra("name"));
        final String TRACKINGNUMBER=String.valueOf(intent.getStringExtra("TRACKINGNUMBER"));
        final String latitude=String.valueOf(intent.getStringExtra("latitude"));
        final String longitude=String.valueOf(intent.getStringExtra("longitude"));
        final double la=Double.parseDouble(latitude);
        final double lo=Double.parseDouble(longitude);
        final String address=intent.getStringExtra("address");
        final String city=intent.getStringExtra("city");
        final String FACTYPE=intent.getStringExtra("FACTYPE");
        text_info=findViewById(R.id.info_text_info);
        text_info.setText(getString(R.string.address)+address+", "+city+"\n" + getString(R.string.gps_address)+latitude+" "+longitude);

        inspection= (ArrayList<InspectionReports>) intent.getSerializableExtra("inspection");
        java.util.Collections.sort(inspection);
        java.util.Collections.reverse(inspection);
        nestedScrollView=findViewById(R.id.info_nestedScroll);
        list_list=findViewById(R.id.info_list_list);
        text_title=findViewById(R.id.info_text_title);

        //TODO：save favourite list and make it savable
        button_fab=findViewById(R.id.info_fab);
        if (SqlManager.getSqlManager().isExistFavouriteRestaurant(TRACKINGNUMBER)){
            button_fab.setImageResource(R.drawable.ic_favorite_black_24dp);
            isFavorite=true;
        }else {
            button_fab.setImageResource(R.drawable.ic_favorite_cancel_black_24dp);
            isFavorite=false;
        }
        button_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite){
                    if (SqlManager.getSqlManager().deleteData(SqlManager.getTableNames()[2],"TRACKINGNUMBER",new String[]{TRACKINGNUMBER})){
                        Toast.makeText(InfoActivity.this,"favorite has been canceled",Toast.LENGTH_SHORT).show();
                        setResult(1);
                        button_fab.setImageResource(R.drawable.ic_favorite_cancel_black_24dp);
                    }else {
                        Toast.makeText(InfoActivity.this,"cancel failed",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("TRACKINGNUMBER", TRACKINGNUMBER);
                    contentValues.put("NAME", intent.getStringExtra("name"));
                    contentValues.put("PHYSICALADDRESS",address);
                    contentValues.put("PHYSICALCITY",city);
                    contentValues.put("FACTYPE",FACTYPE);
                    contentValues.put("LATITUDE",latitude);
                    contentValues.put("LONGITUDE",longitude);

                    if (SqlManager.getSqlManager().insertData(SqlManager.getTableNames()[2], contentValues)) {
                        Toast.makeText(InfoActivity.this,"Added as favorite",Toast.LENGTH_SHORT).show();
                        setResult(1);
                        button_fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }else {
                        Toast.makeText(InfoActivity.this,"Add as favorite failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        text_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(la, lo);
                //GoogleMap map = mapsActivity.mGoogleMap;
                //mapsActivity.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Intent intent1 = new Intent(InfoActivity.this, MapsActivity.class);
                intent1.putExtra("la", la);
                intent1.putExtra("lo", lo);
                intent1.putExtra("latLng", latLng);
                startActivity(intent1);
            }
        });

        loadList();
        final Intent i = new Intent(InfoActivity.this, ViolationListActivity.class);
        i.putExtra("inspection", (Serializable) inspection);

        ListView violation_list = findViewById(R.id.info_list_list);
        violation_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                i.putExtra("position", position);
                startActivity(i);
            }
        });

    }

    /**
     * load list
     */
    private void loadList() {
        if (inspection.size() == 0) {
            text_title.setText(getString(R.string.have_no_data));
        } else {
            text_title.setText(getString(R.string.check_list));
            list_list.setAdapter(new InspectionReportAdapter(inspection, this));
        }

        //Solve the problem of nested listview in scrolling view, and listview cannot scroll
        list_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    // When the touches listview, the parent control loses focus and cannot scroll
                    case MotionEvent.ACTION_DOWN:
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // Let the parent control regain focus when it is released
                        nestedScrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }
}
