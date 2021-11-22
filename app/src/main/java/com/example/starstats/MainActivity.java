package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout mainLayout;
    String tag;
    FloatingActionButton search;
    EditText tagInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.enterID); search = findViewById(R.id.search); tagInput = findViewById(R.id.tagInput);
        search.setOnClickListener(view -> {
            tag = getTag();
            edit.putString("tag", tag).apply();
            goToProfile();
        });
    }
    //TODO: ADD RECENT TAGS
    public void goToProfile() {
        Intent goToProfile = new Intent(this, ProfilePage.class);
        goToProfile.putExtra("tag", getSharedPreferences("def", Context.MODE_PRIVATE).getString("tag",""));
        startActivity(goToProfile);
    }
    public String getTag() {  return tagInput.getText().toString();  }
}