package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout mainLayout;
    String tag;
    FloatingActionButton search;
    TextView title;
    EditText tagInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        setContentView(R.layout.activity_main);
        if(!pref.getString("tag", "defaulterror").equals("defaulterror")) {
            goToProfile();
        }
        mainLayout = findViewById(R.id.enterID); search = findViewById(R.id.search); title = findViewById(R.id.starTitle); tagInput = findViewById(R.id.tagInput);
        search.setOnClickListener(view -> {
            tag = getTag();
            edit.putString("tag", tag).apply();
            goToProfile();
        });
    }
    public void goToProfile() {
        Intent goToProfile = new Intent(this, ProfilePage.class);
        goToProfile.putExtra("tag", getSharedPreferences("def", Context.MODE_PRIVATE).getString("tag",""));
        startActivity(goToProfile);
    }
    public String getTag() {  return tagInput.getText().toString();  }
}