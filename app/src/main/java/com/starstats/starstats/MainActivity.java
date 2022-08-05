package com.starstats.starstats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout mainLayout, buttonsLayout;
    String tag;
    Button toMaps, toBrawlers, toLeaderboards, recent1, recent2, recent3;
    FloatingActionButton search;
    EditText tagInput;
    TextView disclaimer, loading, hashtag, searchInstructions;
    Spinner chooseSearch;
    CardView searchCardview;
    boolean searchType = true; //TRUE = PLAYER SEARCH, FALSE = CLUB SEARCH

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("fromLeaderboards", false).apply();
        edit.putBoolean("fromClubPage", false).apply();
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, initializationStatus -> {});

        RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        AdView mAdView = findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder().build();
        System.out.println("Test: "+ adRequest.getCustomTargeting());

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
                if(i == 0) { //PLAYER SEARCH
                    searchType = true;
                    searchInstructions.setText("Enter player tag:");
                    playerButtons();
                }
                else { //CLUB SEARCH
                    searchType = false;
                    searchInstructions.setText("Enter club tag:");
                    clubButtons();
                }
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

            if(searchType) { goToProfile(); }
            else  { goToClubPage(); }
            finish();
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
            if(searchType) { goToProfile(); }
            else { goToClubPage(); }
            finish();
        });

        recent2.setOnClickListener(view -> {
            tag = recent2.getText().toString();
            hideKeyboard();
            toggleLoading();
            tag = tag.replace(" ", "");
            edit.putString("tag", tag).apply();
            if(searchType) { goToProfile(); }
            else  { goToClubPage(); }
            finish();
        });

        recent3.setOnClickListener(view -> {
            tag = recent3.getText().toString();
            hideKeyboard();
            toggleLoading();
            tag = tag.replace(" ", "");
            edit.putString("tag", tag).apply();
            if(searchType) { goToProfile(); }
            else { goToClubPage(); }

            finish();
        });

        if(searchType) { playerButtons(); }
        else { clubButtons(); }
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

    private void playerButtons() {
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        Set<String> fetch = pref.getStringSet("profileRecents", new HashSet<>());
        HashSet<String> profileRecents = new HashSet<>();
        profileRecents.addAll(fetch);
        Object[] tmp = profileRecents.toArray();
        ArrayList<String> tmp2 = new ArrayList<>();
        for(Object o : tmp) { tmp2.add((String) o); }
        try { recent1.setText(tmp2.get(0)); recent1.setVisibility(View.VISIBLE); } catch (Exception ignored) { recent1.setVisibility(View.GONE); }
        try { recent2.setText(tmp2.get(1)); recent2.setVisibility(View.VISIBLE); } catch (Exception ignored) { recent2.setVisibility(View.GONE); }
        try { recent3.setText(tmp2.get(2)); recent3.setVisibility(View.VISIBLE); } catch (Exception ignored) { recent3.setVisibility(View.GONE); }
    }

    private void clubButtons() {
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        Set<String> fetch = pref.getStringSet("clubRecents", new HashSet<>());
        HashSet<String> clubRecents = new HashSet<>();
        clubRecents.addAll(fetch);
        Object[] tmp = clubRecents.toArray();
        ArrayList<String> tmp2 = new ArrayList<>();
        for(Object o : tmp) { tmp2.add((String) o); }
        try { recent1.setText(tmp2.get(0)); recent1.setVisibility(View.VISIBLE); } catch (Exception ignored) { recent1.setVisibility(View.GONE); }
        try { recent2.setText(tmp2.get(1)); recent2.setVisibility(View.VISIBLE); } catch (Exception ignored) { recent2.setVisibility(View.GONE); }
        try { recent3.setText(tmp2.get(2)); recent3.setVisibility(View.VISIBLE); } catch (Exception ignored) { recent3.setVisibility(View.GONE); }
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

    @Override
    public void onBackPressed() { }

    public String getTag() {
        return tagInput.getText().toString();
    }
}