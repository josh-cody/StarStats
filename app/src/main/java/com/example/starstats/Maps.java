package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ScrollView;

public class Maps extends AppCompatActivity {

    private ScrollView container;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        recyclerView = findViewById(R.id.mapsList); container = findViewById(R.id.mapsScroll);


    }
}