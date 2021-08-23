package com.example.cmpt276project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmpt276project.UI.NewInspectionsAdapter;
import com.example.cmpt276project.UI.RestaurantAdapter;
import com.example.cmpt276project.data.SqlManager;
import com.example.cmpt276project.struct.Restaurant;
import com.example.cmpt276project.struct.RestaurantsManager;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

public class NewInspectionsActivity extends AppCompatActivity {
    private RestaurantsManager restaurantsManager;
    private ArrayList<Restaurant> restaurantArrayList;
    private ArrayList<Restaurant> restaurants;
    private ArrayList<Restaurant> Temp;
    private InfoActivity infoActivity;
    private SqlManager sqlManager;
    ListView list_new_inspections;
    NewInspectionsAdapter newInspectionsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_inspections);
        sqlManager = SqlManager.getSqlManager();
        list_new_inspections=findViewById(R.id.new_inspections_list);
        Button btn = findViewById(R.id.btn_to_map);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewInspectionsActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        displayFavRestaurants();
    }

    private void displayFavRestaurants(){
        /*
        TODO:
         *      1. Get the new list of Inspections that were downloaded from server
         *      2. Compare these Inspections tracking number to the restaurants tracking number currently in the Favourites list
         *      3. Make a list of the restaurants
          */
        restaurantsManager = RestaurantsManager.getInstance();
        restaurants = restaurantsManager.getRestaurants();
        Temp = sqlManager.queryFavouriteRestaurant();
        // 1.
        restaurantArrayList = new ArrayList<>();
        for (int i = 0; i < Temp.size(); i++) {
            for (int j = 0; j < restaurants.size(); j++) {
                // Compare the restaurants in the fav list with the restaurants in the updated restaurant list.
                if (restaurants.get(j).getNAME().equals(Temp.get(i).getNAME())
                        && restaurants.get(j).getTRACKINGNUMBER().equals(Temp.get(i).getTRACKINGNUMBER())) {
                    System.out.println(restaurants.get(j) + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    restaurantArrayList.add(restaurants.get(j));
                    System.out.println(restaurants.get(j).getMostRecentInspection() + "PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP" );
                    System.out.println(restaurantArrayList.get(0).getMostRecentInspection() + "}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");
                }
            }
        }

        if(restaurantArrayList.isEmpty()){
            TextView textView = findViewById(R.id.favlist_is_empty);
            textView.setText("");
        }

        newInspectionsAdapter=new NewInspectionsAdapter(restaurantArrayList,this);
        list_new_inspections.setAdapter(newInspectionsAdapter);


        list_new_inspections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(NewInspectionsActivity.this,InfoActivity.class);
                intent.putExtra("tracking",restaurantsManager.getRestaurants().get(position).getTRACKINGNUMBER());
                intent.putExtra("type",restaurantsManager.getRestaurants().get(position).getFACTYPE());
                intent.putExtra("name",restaurantsManager.getRestaurants().get(position).getNAME());
                intent.putExtra("address",restaurantsManager.getRestaurants().get(position).getPHYSICALADDRESS());
                intent.putExtra("city",restaurantsManager.getRestaurants().get(position).getPHYSICALCITY());
                intent.putExtra("latitude",String.valueOf(restaurantsManager.getRestaurants().get(position).getLATITUDE()));
                intent.putExtra("longitude",String.valueOf(restaurantsManager.getRestaurants().get(position).getLONGITUDE()));
                intent.putExtra("inspection", (Serializable) restaurantsManager.getRestaurants().get(position).getInspections());
                intent.putExtra("fav", restaurantsManager.getFavourite());
                startActivity(intent);
            }
        });

    }
}
