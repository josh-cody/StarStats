package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;

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
    Button toMaps, toBrawlers;
    FloatingActionButton search, profile;
    EditText tagInput;
    TextView disclaimer, enterTagText, loading, hashtag;
    FragmentContainerView fragmentContainerView;
    ProfileFragment frag;
    boolean fragmentClosed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        setContentView(R.layout.activity_main);
        fragmentContainerView = findViewById(R.id.fragmentContainerView); profile = findViewById(R.id.profile); hashtag = findViewById(R.id.hashtag); loading = findViewById(R.id.loading); enterTagText = findViewById(R.id.enterTagText); toBrawlers =  findViewById(R.id.toBrawlers); buttonsLayout = findViewById(R.id.buttonsLayout); toMaps = findViewById(R.id.toMaps); mainLayout = findViewById(R.id.enterID); search = findViewById(R.id.search); tagInput = findViewById(R.id.tagInput); disclaimer = findViewById(R.id.disclaimer);
        loading.setVisibility(View.GONE);
        disclaimer.setText("This content is not affiliated with, endorsed, sponsored, or specifically approved by Supercell and Supercell is not responsible for it. For more information see Supercell’s Fan Content Policy: www.supercell.com/fan-content-policy.");


        profile.setOnClickListener(view -> {
            hideKeyboard();
            if(fragmentClosed) {
                frag = ProfileFragment.newInstance();
                //tagInput.setFocusable(false);
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
            buttonsLayout.setVisibility(View.GONE);
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

            edit.putString("tag", tag).apply();
            goToProfile();
            finish();
        });

        toMaps.setOnClickListener(view -> {
            hideKeyboard();
            buttonsLayout.setVisibility(View.GONE);
            enterTagText.setVisibility(View.GONE);
            disclaimer.setVisibility(View.GONE);
            hashtag.setVisibility(View.GONE);
            profile.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            tagInput.setVisibility(View.GONE);
            if(!fragmentClosed) {
                fragmentClosed = true;
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(frag).commit();
            }
            loading.setVisibility(View.VISIBLE);

            goToMaps();
            finish();
        });
        toBrawlers.setOnClickListener(view -> {
            hideKeyboard();
            buttonsLayout.setVisibility(View.GONE);
            enterTagText.setVisibility(View.GONE);
            disclaimer.setVisibility(View.GONE);
            hashtag.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            profile.setVisibility(View.GONE);
            tagInput.setVisibility(View.GONE);
            if(!fragmentClosed) {
                fragmentClosed = true;
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(frag).commit();
            }
            loading.setVisibility(View.VISIBLE);

            goToBrawlers();
            finish();
        });
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


    public String getTag() {
        return tagInput.getText().toString();
    }

}