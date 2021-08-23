package com.example.cmpt276project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cmpt276project.UI.AutoCompleteTextViewAdapter;
import com.example.cmpt276project.UI.CustomInfoViewAdapter;
import com.example.cmpt276project.UI.SearchFilterDialog;
import com.example.cmpt276project.data.SqlManager;
import com.example.cmpt276project.struct.InspectionReports;
import com.example.cmpt276project.struct.Restaurant;
import com.example.cmpt276project.struct.RestaurantsManager;
import com.example.cmpt276project.tool.MapMarker;
import com.example.cmpt276project.tool.MarkerClusterRenderer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.io.Serializable;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


/**
 * Initialized the Google map on screen and displays the clusters on screen with the markers and their respective colours.
 */

public class MapsActivity extends AppCompatActivity implements
        ClusterManager.OnClusterItemInfoWindowClickListener<MapMarker>,
        OnMapReadyCallback,
        SearchFilterDialog.SearchFilterDialogListener
{

    public GoogleMap mGoogleMap;
    private static int REQUEST_CODE = 1234;
    private static final String TAG = "MapsActivity";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;

    private ClusterManager<MapMarker> mClusterManager;
    ArrayList<Restaurant> restaurantList;
    private RestaurantsManager manager;
    private MapView mMapView;
    private AutoCompleteTextView autoEdit_search;
    private Button button_search;
    private Button button_search_filter;
    private String MAPVIEW_KEY;
    ArrayList<Restaurant> search_restaurants=new ArrayList<>();

    //Search Filter condtitions
    private boolean isRestaurantFavorite;
    private String restaurantHazardLevel;
    private int minNumberViolations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        manager = RestaurantsManager.getInstance();
        restaurantList = manager.getRestaurants();

        isRestaurantFavorite = false;
        restaurantHazardLevel = "Low";
        minNumberViolations = 0;

        button_search=findViewById(R.id.map_button_search);
        button_search_filter = findViewById(R.id.map_search_filter_button);
        autoEdit_search=findViewById(R.id.map_auto_edit_search);

        MAPVIEW_KEY=this.getString(R.string.google_maps_key);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_KEY);
        }
        mMapView = (MapView) findViewById(R.id.map_mapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);


        button_search_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.googleToolbar);
        setSupportActionBar(myToolbar);
        getPermission();

        searchView();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.google_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_List_View:
                Intent intent = new Intent(MapsActivity.this, RestaurantListActivity.class);
                //TODO: modified here
                Log.d("RestaurantListActivity", "loadList: " + search_restaurants.size());
                if (search_restaurants.size() != 0) {
                    Log.d("RestaurantListActivity", "loadList: put in data");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("restaurants", search_restaurants);
                    intent.putExtra("restaurants", bundle);
                }
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

              /*  startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    private void moveToLocation(double la, double lo){
        MapsInitializer.initialize(this);
        System.out.println("============================================= IN MOVE TO LOCATION");
        System.out.println(la);
        System.out.println(lo);
        LatLng latLng = new LatLng(la, lo);
        if (latLng!=null){
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25f));
        }
        return;
    }

    private void getPermission() {
        String[] permissions = {ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                //initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_CODE);
        }

    }

   // private void initMap() {
    //    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
     //   mapFragment.getMapAsync(MapsActivity.this);
    //}

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));

                        } else {
                            Toast.makeText(MapsActivity.this, R.string.current_location_failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        setupClusterer(restaurantList);

        Intent intent = getIntent();
        if(intent.hasExtra("latLng")) {
            Bundle b = intent.getExtras();
            double la = b.getDouble("la");
            double lo = b.getDouble("lo");
            Toast.makeText(this,getString(R.string.has_targeted)+la+"\n"+lo,Toast.LENGTH_LONG).show();
            moveToLocation(la, lo);
        }
        else{
            if (mLocationPermissionsGranted) {
                getDeviceLocation();
            }
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

    }


    //Sets up map markers and the clustering functionality
    private void setupClusterer(ArrayList<Restaurant> restaurants) {
        mClusterManager = new ClusterManager<MapMarker>(this, mGoogleMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, mGoogleMap, mClusterManager));
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);

        for (Restaurant restaurant : restaurants) {
            try {

                double lat = restaurant.getLATITUDE();
                double lon = restaurant.getLONGITUDE();

                MapMarker marker = new MapMarker(lat, lon, getHazardColor(restaurant), restaurant);
                mClusterManager.addItem(marker);

                mClusterManager.getMarkerCollection()
                        .setInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(this), restaurants));

                mGoogleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

            } catch (Exception e) {
                //Toast.makeText(MapsActivity.this, getString(R.string.marker_failure), Toast.LENGTH_LONG).show();
                e.printStackTrace();
                Log.d("ERROR: ", "placeRestaurantMarkers: Parse or Null exception ");
            }
        }
    }

    //@return hue value corresponding to hazard level
    //marker is green is restaurant has never had an inspection
    private int getHazardColor(Restaurant restaurant) {
        if (!(restaurant.getInspections().isEmpty())) {
            switch (restaurant.getMostRecentInspection().getHazardRating()) {
                case "Low":
                    return 121;
                case "Moderate":
                    return 41;
                case "High":
                    return 1;
                default:
                    return 240;
            }
        } else {
            return 121;
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(MapMarker mapMarker) {
        Intent intent=new Intent(MapsActivity.this,InfoActivity.class);
        intent.putExtra("name", mapMarker.getRestaurant().getNAME());
        intent.putExtra("address",mapMarker.getRestaurant().getPHYSICALADDRESS());
        intent.putExtra("city",mapMarker.getRestaurant().getPHYSICALCITY());
        intent.putExtra("latitude",mapMarker.getRestaurant().getLATITUDE());
        intent.putExtra("longitude",mapMarker.getRestaurant().getLONGITUDE());
        intent.putExtra("latitude",String.valueOf(mapMarker.getRestaurant().getLATITUDE()));
        intent.putExtra("longitude",String.valueOf(mapMarker.getRestaurant().getLONGITUDE()));
        intent.putExtra("inspection", (Serializable) mapMarker.getRestaurant().getInspections());
        startActivity(intent);

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


    private void searchView(){
        final AutoCompleteTextViewAdapter autoCompleteTextViewAdapter=new AutoCompleteTextViewAdapter(this,RestaurantsManager.getInstance().getRestaurants());
        autoEdit_search.setAdapter(autoCompleteTextViewAdapter);
        autoEdit_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoEdit_search.setText(autoCompleteTextViewAdapter.getItemRestaurant(position).getNAME());
            }
        });

        //Button click event
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter
                search_restaurants=new ArrayList<>();
                String name=autoEdit_search.getText().toString().toUpperCase();
                mGoogleMap.clear();
                if (name.equals("") || name.isEmpty()){
                    for (Restaurant restaurant : restaurantList){
                        if (fitsCriteria(restaurant)){
                            search_restaurants.add(restaurant);
                        }
                    }
                }else {
                    for (Restaurant restaurant:restaurantList){
                        if (restaurant.getNAME().toUpperCase().indexOf(name)!=-1 && fitsCriteria(restaurant)){
                            search_restaurants.add(restaurant);
                        }
                    }
                    //
                    Toast.makeText(MapsActivity.this,"Found "+search_restaurants.size()+" recordings",Toast.LENGTH_SHORT).show();
                    moveToLocation(search_restaurants.get(0).getLATITUDE(),search_restaurants.get(0).getLONGITUDE());//Moving lens
                }
                setupClusterer(search_restaurants);
                System.out.println(search_restaurants.size());
                System.out.println(restaurantList.size());
            }
        });
    }

    //TODO：Added new methods
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }



    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void openDialog(){
        SearchFilterDialog searchFilterDialog = new SearchFilterDialog();
        searchFilterDialog.show(getSupportFragmentManager(), "Search Dialog");
    }

    @Override
    public void getInput(boolean isFavourited, String hazard, int minViolationsInt) {
        System.out.println("________________________________________________________________");
        System.out.println(isFavourited + " | " + " | " + hazard + " | " + minViolationsInt);
        isRestaurantFavorite = isFavourited;
        restaurantHazardLevel = hazard;
        minNumberViolations = minViolationsInt;
    }


    private boolean fitsCriteria(Restaurant restaurant){
        if(numViolationsThisYear(restaurant) >= minNumberViolations ){
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
        }else
            return true;
    }

    //If we are searching by favorited restaurants only, check is restaurant is favorited. Otherwise
    //just return true
    private boolean isRestaurantFavorited(Restaurant restaurant){
        if(isRestaurantFavorite){
            return SqlManager.getSqlManager(this).isExistFavouriteRestaurant(String.valueOf(restaurant.getTRACKINGNUMBER()));
        }else{
            return true;
        }
    }


}
