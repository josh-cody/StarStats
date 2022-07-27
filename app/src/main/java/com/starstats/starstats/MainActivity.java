package com.starstats.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout mainLayout, buttonsLayout;
    String tag;
    Button toMaps, toBrawlers, toLeaderboards, recent1, recent2, recent3;
    FloatingActionButton search;
    EditText tagInput;
    TextView disclaimer, loading, hashtag, searchInstructions;
    Spinner chooseSearch;
    CardView searchCardview;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, initializationStatus -> {});
        mAdView = findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        searchCardview = findViewById(R.id.searchCardview); searchInstructions = findViewById(R.id.searchInstructions); chooseSearch = findViewById(R.id.chooseSearch); toLeaderboards = findViewById(R.id.toLeaderboards); recent1 = findViewById(R.id.recent1); recent2 = findViewById(R.id.recent2); recent3 = findViewById(R.id.recent3); hashtag = findViewById(R.id.hashtag); loading = findViewById(R.id.loading); toBrawlers =  findViewById(R.id.toBrawlers); buttonsLayout = findViewById(R.id.buttonsLayout); toMaps = findViewById(R.id.toMaps); mainLayout = findViewById(R.id.enterID); search = findViewById(R.id.search); tagInput = findViewById(R.id.tagInput); disclaimer = findViewById(R.id.disclaimer);

        loading.setVisibility(View.GONE);
        disclaimer.setText(R.string.supercellDisclaimer);
        recent1.setVisibility(View.GONE); recent2.setVisibility(View.GONE); recent3.setVisibility(View.GONE);


        String[] options = {"Player search", "Club search"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        chooseSearch.setAdapter(adapter);

        chooseSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();

                if(i == 0) { edit.putString("searchType","player").apply(); searchInstructions.setText("Enter player tag:"); }
                else { edit.putString("searchType", "club").apply(); searchInstructions.setText("Enter club tag:"); }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        search.setOnClickListener(view -> {
            tag = getTag();
            hideKeyboard();
            toggleLoading();
            tag = tag.replace(" ", "");
            edit.putString("tag", tag).apply();
            if(pref.getString("searchType", "").equals("player")) { goToProfile(); finish(); }
            else if(pref.getString("searchType", "").equals("club")) { goToClubPage(); finish(); }
        });

        toMaps.setOnClickListener(view -> {
            hideKeyboard();
            toggleLoading();
            goToMaps();
            finish();
        });

        toBrawlers.setOnClickListener(view -> {
            hideKeyboard();
            toggleLoading();
            goToBrawlers();
            finish();
        });

        toLeaderboards.setOnClickListener(view -> {
            hideKeyboard();
            toggleLoading();
            goToLeaderboards();
            finish();
        });

        recent1.setOnClickListener(view -> {
            tag = recent1.getText().toString();
            hideKeyboard();
            toggleLoading();
            tag = tag.replace(" ", "");
            edit.putString("tag", tag).apply();
            if(pref.getString("searchType", "").equals("player")) { goToProfile(); finish(); }
            else if(pref.getString("searchType", "").equals("club")) { goToClubPage(); finish(); }
        });

        recent2.setOnClickListener(view -> {
            tag = recent2.getText().toString();
            hideKeyboard();
            toggleLoading();
            tag = tag.replace(" ", "");
            edit.putString("tag", tag).apply();
            if(pref.getString("searchType", "").equals("player")) { goToProfile(); finish(); }
            else if(pref.getString("searchType", "").equals("club")) { goToClubPage(); finish(); }
        });

        recent3.setOnClickListener(view -> {
            tag = recent3.getText().toString();
            hideKeyboard();
            toggleLoading();
            tag = tag.replace(" ", "");
            edit.putString("tag", tag).apply();
            if(pref.getString("searchType", "").equals("player")) { goToProfile(); finish(); }
            else if(pref.getString("searchType", "").equals("club")) { goToClubPage(); finish(); }
            finish();
        });

        if(pref.contains("recent1")) {
            recent1.setVisibility(View.VISIBLE);
            recent1.setText(pref.getString("recent1",""));
        }
        if(pref.contains("recent2")) {
            recent2.setVisibility(View.VISIBLE);
            recent2.setText(pref.getString("recent2",""));
        }
        if(pref.contains("recent3")) {
            recent3.setVisibility(View.VISIBLE);
            recent3.setText(pref.getString("recent3",""));
        }
    }

    private void goToLeaderboards() {
        Intent toLeaderboards = new Intent(this, Leaderboards.class);
        startActivity(toLeaderboards);
    }

    private void goToClubPage() {
        Intent toClubPage = new Intent(this, ClubPage.class);
        startActivity(toClubPage);
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    public void toggleLoading() {
        buttonsLayout.setVisibility(View.GONE);
        recent1.setVisibility(View.GONE);
        recent2.setVisibility(View.GONE);
        recent3.setVisibility(View.GONE);
        searchCardview.setVisibility(View.GONE);
        disclaimer.setVisibility(View.GONE);
        hashtag.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        tagInput.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    public String getTag() {
        return tagInput.getText().toString();
    }
}