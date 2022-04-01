package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class BrawlerDescription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brawler_description);
        Intent i = getIntent();
        System.out.println("name: "+ i.getStringExtra("name"));
    }
}