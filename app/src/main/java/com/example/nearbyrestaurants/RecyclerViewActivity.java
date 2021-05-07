package com.example.nearbyrestaurants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.nearbyrestaurants.PlaceModels.ListImplementation;
import com.example.nearbyrestaurants.PlaceModels.PlaceDataList;

public class RecyclerViewActivity extends AppCompatActivity {
    private Button ratingSortButton;
    private Button distanceSortButton;
    private Button priceSortButton;
    private ImageView menuButton;
    public PlaceDataList restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        restaurants = ListImplementation.sharedInstance();
        ratingSortButton = (Button) findViewById(R.id.get_list_sort_rating);
        distanceSortButton = (Button) findViewById(R.id.get_list_sort_distance);
        priceSortButton = (Button) findViewById(R.id.get_list_sort_price);
        menuButton = (ImageView) findViewById(R.id.title);
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initRecyclerView() {
        sortByRating();
        distanceSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByDistance();
            }
        });

        ratingSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByRating();
            }
        });

        priceSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByPrice();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecyclerViewActivity.this, MainMenu.class);
                startActivity(i);
            }
        });
    }

    private void sortByRating() {
        restaurants.sortRating();
        RecyclerView myRecycler = findViewById(R.id.recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        myRecycler.setAdapter(adapter);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getBaseContext(), InfoActivity.class);
                intent.putExtra("itemPosition", position);
                startActivity(intent);
            }
        });
    }

    private void sortByDistance() {
        restaurants.sortDistance();
        RecyclerView myRecycler = findViewById(R.id.recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        myRecycler.setAdapter(adapter);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getBaseContext(), InfoActivity.class);
                intent.putExtra("itemPosition", position);
                startActivity(intent);
            }
        });
    }

    private void sortByPrice() {
        restaurants.sortPrice();
        RecyclerView myRecycler = findViewById(R.id.recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        myRecycler.setAdapter(adapter);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getBaseContext(), InfoActivity.class);
                intent.putExtra("itemPosition", position);
                startActivity(intent);
            }
        });
    }
}