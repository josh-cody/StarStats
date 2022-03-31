package com.example.starstats;

import android.content.Intent;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MapZoom extends AppCompatActivity {

    private ImageView mapZoom;
    private TextView mapNameTextView;
    private int mapID;
    private String mapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_zoom);
        Intent i = getIntent();
        mapID = i.getIntExtra("mapID", 0);
        mapName = i.getStringExtra("mapName");
        mapZoom = findViewById(R.id.mapZoom); mapNameTextView = findViewById(R.id.mapNameTextView);
        mapNameTextView.setText(mapName);
        mapZoom.setImageResource(this.mapID);
        if(mapName.equals("G.G. Mortuary")) {mapZoom.setImageResource(R.drawable.ggmortuary); } //TODO: STRING FORMATTING
    }

}