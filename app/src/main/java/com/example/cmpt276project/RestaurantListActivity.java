package com.example.cmpt276project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmpt276project.UI.AutoCompleteTextViewAdapter;
import com.example.cmpt276project.UI.RestaurantAdapter;
import com.example.cmpt276project.UI.SearchFilterDialog;
import com.example.cmpt276project.data.SqlManager;
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.struct.Restaurant;
import com.example.cmpt276project.struct.RestaurantsManager;

import java.io.Serializable;
import java.util.ArrayList;

public class RestaurantListActivity extends AppCompatActivity implements SearchFilterDialog.SearchFilterDialogListener {

    String TAG="RestaurantListActivity";
    ListView list_restaurant;
    RestaurantAdapter restaurantAdapter;

    private RestaurantsManager manager;
    SqlManager sqlManager;

    private AutoCompleteTextView autoEdit_search;
    private Button button_search;
    private Button button_search_filter;

    //Search Filter condtitions
    private boolean isRestaurantFavorite;
    private String restaurantHazardLevel;
    private int minNumberViolations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        //default values for search
        isRestaurantFavorite = false;
        restaurantHazardLevel = "High";
        minNumberViolations = 0;


        sqlManager= SqlManager.getSqlManager(this);

        list_restaurant=findViewById(R.id.list_list_restaurant);

        autoEdit_search = findViewById(R.id.list_auto_edit_search);
        button_search = findViewById(R.id.list_button_search);
        button_search_filter = findViewById(R.id.search_filter_button);

        //Get list from manager
        updateInfo();
        loadList();

        searchView();

        button_search_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });


        // click list event
        list_restaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(RestaurantListActivity.this,InfoActivity.class);
                intent.putExtra("TRACKINGNUMBER",manager.getRestaurants().get(position).getTRACKINGNUMBER());
                intent.putExtra("FACTYPE",manager.getRestaurants().get(position).getFACTYPE());

                intent.putExtra("name",manager.getRestaurants().get(position).getNAME());
                intent.putExtra("address",manager.getRestaurants().get(position).getPHYSICALADDRESS());
                intent.putExtra("city",manager.getRestaurants().get(position).getPHYSICALCITY());
                intent.putExtra("latitude",String.valueOf(manager.getRestaurants().get(position).getLATITUDE()));
                intent.putExtra("longitude",String.valueOf(manager.getRestaurants().get(position).getLONGITUDE()));
                intent.putExtra("inspection", (Serializable) manager.getRestaurants().get(position).getInspections());
                startActivityForResult(intent,250);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 250 && resultCode == 1) {
            loadList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Map_View:
                Intent intent = new Intent(RestaurantListActivity.this, MapsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * update the restaurant information
     */
    public void updateInfo(){
        manager = RestaurantsManager.getInstance();//sqlManager.queryRestaurantsData(), sqlManager.queryInspectionReportsData());
    }
    /**
     * load list
     */
    private void loadList(){
        Log.d(TAG, "loadList: 123"+getIntent().hasExtra("restaurants"));
        if (getIntent().hasExtra("restaurants")){
            Log.d(TAG, "loadList: loading");
            Bundle bundle=getIntent().getBundleExtra("restaurants");
            ArrayList<Restaurant> restaurants=(ArrayList<Restaurant>) bundle.getSerializable("restaurants");
            restaurantAdapter=new RestaurantAdapter(restaurants,this);
            list_restaurant.setAdapter(restaurantAdapter);
        }else {
            restaurantAdapter=new RestaurantAdapter(manager.getRestaurants(),this);
            list_restaurant.setAdapter(restaurantAdapter);
        }
        list_restaurant.invalidate();
    }

    private void loadFilteredList(ArrayList<Restaurant> restaurants){
        restaurantAdapter = new RestaurantAdapter(restaurants, this);
        list_restaurant.setAdapter(restaurantAdapter);
    }


    //when click the back button can make the app finish
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(keycode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    //Setup the search view and button functionality
    private void searchView() {
        final AutoCompleteTextViewAdapter autoCompleteTextViewAdapter = new AutoCompleteTextViewAdapter(this, manager.getRestaurants());
        autoEdit_search.setAdapter(autoCompleteTextViewAdapter);

        autoEdit_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoEdit_search.setText(autoCompleteTextViewAdapter.getItemRestaurant(position).getNAME());
            }
        });
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter
                ArrayList<Restaurant> search_restaurants=new ArrayList<>();
                String name=autoEdit_search.getText().toString().toUpperCase();
                if (name.equals("") || name.isEmpty()){
                    for (Restaurant restaurant : manager.getRestaurants()){
                        if (fitsCriteria(restaurant)){
                            search_restaurants.add(restaurant);
                        }
                    }
                }else {
                    for (Restaurant restaurant : manager.getRestaurants()){
                        if (restaurant.getNAME().toUpperCase().contains(name) && fitsCriteria(restaurant)){
                            search_restaurants.add(restaurant);
                        }
                    }
                    Toast.makeText(RestaurantListActivity.this,"Found "+search_restaurants.size()+" recordings",Toast.LENGTH_SHORT).show();
                }
                loadFilteredList(search_restaurants);
            }
        });
    }


    private void openDialog(){
        SearchFilterDialog searchFilterDialog = new SearchFilterDialog();
        searchFilterDialog.show(getSupportFragmentManager(), "Search Dialog");
    }

    @Override
    public void getInput(boolean isFavourited, String hazard, int minViolationsInt) {
        isRestaurantFavorite = isFavourited;
        restaurantHazardLevel = hazard;
        minNumberViolations = minViolationsInt;
    }


    private boolean fitsCriteria(Restaurant restaurant){
        if(numViolationsThisYear(restaurant) > minNumberViolations ){
            if(isLessThanMaxHazardLevel(restaurant)){
                if(isRestaurantFavorited(restaurant)){
                    return true;
                }
            }

        }
        return false;
    }

    private int numViolationsThisYear(Restaurant restaurant){
        int critViolations = 0;
        for(InspectionReports i : restaurant.getInspections()){
            if(i.getDaysSince() <= 365){
                critViolations += i.getNumCritical();
            }
        }
        return critViolations;
    }

    private boolean isLessThanMaxHazardLevel(Restaurant restaurant){
        if(restaurant.getMostRecentInspection() != null) {
            switch (restaurantHazardLevel) {
                case "High":
                    return true;
                case "Medium":
                    return !restaurant.getMostRecentInspection().getHazardRating().equals("High")
                            && !restaurant.getMostRecentInspection().getHazardRating().equals("high");
                case "Low":
                    return restaurant.getMostRecentInspection().getHazardRating().equals("Low")
                            || restaurant.getMostRecentInspection().getHazardRating().equals("low");
                default:
                    return true;
            }
        }else {
            return true;
        }
    }

    //If we are searching by favorited restaurants only, check is restaurant is favorited. Otherwise
    //just return true
    private boolean isRestaurantFavorited(Restaurant restaurant){
        if(isRestaurantFavorite){
            return SqlManager.getSqlManager(this).isExistFavouriteRestaurant(restaurant.getTRACKINGNUMBER());
        }else{
            return true;
        }
    }

}

