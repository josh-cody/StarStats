package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout mainLayout, buttonsLayout;
    String tag;
    Button toMaps, toBrawlers;
    FloatingActionButton search;
    EditText tagInput;
    TextView disclaimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        setContentView(R.layout.activity_main);
        toBrawlers =  findViewById(R.id.toBrawlers); buttonsLayout = findViewById(R.id.buttonsLayout); toMaps = findViewById(R.id.toMaps); mainLayout = findViewById(R.id.enterID); search = findViewById(R.id.search); tagInput = findViewById(R.id.tagInput); disclaimer = findViewById(R.id.disclaimer);
        disclaimer.setText("This content is not affiliated with, endorsed, sponsored, or specifically approved by Supercell and Supercell is not responsible for it. For more information see Supercell’s Fan Content Policy: www.supercell.com/fan-content-policy.");

        search.setOnClickListener(view -> {
            tag = getTag();
            edit.putString("tag", tag).apply();
            System.out.println("tag: "+pref.getString("tag", "NO TAG"));
            goToProfile();
        });
        toMaps.setOnClickListener(view -> {
            goToMaps();
        });
        toBrawlers.setOnClickListener(view -> {
            goToBrawlers();
        });
    }

    public void goToBrawlers() {
        Intent goToBrawlers = new Intent(this, AllBrawlers.class);
        startActivity(goToBrawlers);
    }

    public void goToProfile() {
        Intent goToProfile = new Intent(this, ProfilePage.class);
        goToProfile.putExtra("tag", getSharedPreferences("def", Context.MODE_PRIVATE).getString("tag", ""));
        startActivity(goToProfile);
    }

    public void goToMaps() {
        Intent goToMaps = new Intent(this, Maps.class);
        startActivity(goToMaps);
    }


    public String getTag() {
        return tagInput.getText().toString();
    }

}