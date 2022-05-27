package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    ConstraintLayout mainLayout, buttonsLayout;
    String tag;
    Button toMaps, toBrawlers, recent1, recent2, recent3;
    FloatingActionButton search, profile;
    EditText tagInput;
    TextView disclaimer, enterTagText, loading, hashtag;
    FragmentContainerView fragmentContainerView;
    ProfileFragment frag;
    boolean fragmentClosed = true;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        setContentView(R.layout.activity_main);
        recent1 = findViewById(R.id.recent1); recent2 = findViewById(R.id.recent2); recent3 = findViewById(R.id.recent3); fragmentContainerView = findViewById(R.id.fragmentContainerView); profile = findViewById(R.id.profile); hashtag = findViewById(R.id.hashtag); loading = findViewById(R.id.loading); enterTagText = findViewById(R.id.enterTagText); toBrawlers =  findViewById(R.id.toBrawlers); buttonsLayout = findViewById(R.id.buttonsLayout); toMaps = findViewById(R.id.toMaps); mainLayout = findViewById(R.id.enterID); search = findViewById(R.id.search); tagInput = findViewById(R.id.tagInput); disclaimer = findViewById(R.id.disclaimer);
        loading.setVisibility(View.GONE);
        disclaimer.setText(R.string.supercellDisclaimer);
        recent1.setVisibility(View.GONE); recent2.setVisibility(View.GONE); recent3.setVisibility(View.GONE);

        if(!pref.contains("musicSetting")) {
            edit.putBoolean("musicSetting", false).apply();
        }
        else if (pref.getBoolean("musicSetting", false)){
            //start music
            Intent bgMusic = new Intent(MainActivity.this, BackgroundSoundService.class);
            startService(bgMusic);
        }
        else {
            //no music
        }

        profile.setOnClickListener(view -> {
            hideKeyboard();
            if(fragmentClosed) {
                frag = ProfileFragment.newInstance();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).replace(R.id.fragmentContainerView, frag).commit();
                fragmentClosed = false;
            }
            else {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(frag).commit();
                tagInput.setFocusable(true);
                fragmentClosed = true;
            }
        });

        mainLayout.setOnClickListener(view ->{
            if(!fragmentClosed) {
                tagInput.setFocusable(true);
                System.out.println("focus: "+tagInput.isFocusable());
                fragmentClosed = true;
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(frag).commit();
            }
        });

        search.setOnClickListener(view -> {
            tag = getTag();
            hideKeyboard();
            toggleLoading();
            edit.putString("tag", tag).apply();
            goToProfile();
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
        recent1.setOnClickListener(view -> {
            tag = recent1.getText().toString();
            hideKeyboard();
            toggleLoading();
            edit.putString("tag", tag).apply();
            goToProfile();
            finish();
        });
        recent2.setOnClickListener(view -> {
            tag = recent2.getText().toString();
            hideKeyboard();
            toggleLoading();
            edit.putString("tag", tag).apply();
            goToProfile();
            finish();
        });
        recent3.setOnClickListener(view -> {
            tag = recent3.getText().toString();
            hideKeyboard();
            toggleLoading();
            edit.putString("tag", tag).apply();
            goToProfile();
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
        profile.setVisibility(View.GONE);
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
        enterTagText.setVisibility(View.GONE);
        disclaimer.setVisibility(View.GONE);
        hashtag.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        tagInput.setVisibility(View.GONE);
        profile.setVisibility(View.GONE);
        if(!fragmentClosed) {
            fragmentClosed = true;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(frag).commit();
        }
        loading.setVisibility(View.VISIBLE);
    }

    public String getTag() {
        return tagInput.getText().toString();
    }

}