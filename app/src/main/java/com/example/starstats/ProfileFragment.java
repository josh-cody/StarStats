package com.example.starstats;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    Switch musicSetting;
    EditText playerTag, brawlerToTrack;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        musicSetting = v.findViewById(R.id.musicSetting);
        playerTag = v.findViewById(R.id.playerTag);
        brawlerToTrack = v.findViewById(R.id.brawlerToTrack);

        if(pref.contains("musicSetting")) {
            if(pref.getBoolean("musicSetting", false)) {
                 musicSetting.setChecked(true);
                 getActivity().startService(new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class));
            }
            else {
                musicSetting.setChecked(false);
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class));
            }
        } else {
            edit.putBoolean("musicSetting", false);
            BackgroundSoundService.mediaPlayer.start();
        }

        musicSetting.setOnCheckedChangeListener((compoundButton, b) ->
        {
            if(!pref.getBoolean("musicSetting",false) && BackgroundSoundService.mediaPlayer != null) { //TODO: mediaPlayer is null
                    BackgroundSoundService.mediaPlayer.setVolume(.1f,.1f);
                edit.putBoolean("musicSetting", true).apply();
            }
            else if (BackgroundSoundService.mediaPlayer != null){
                BackgroundSoundService.mediaPlayer.setVolume(0f,0f);
                edit.putBoolean("musicSetting", false).apply();
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("widgetPlayerTag", playerTag.getText().toString()).apply();
        edit.putString("brawlerToTrack", brawlerToTrack.getText().toString()).apply();
        super.onDestroyView();
    }

    public static ProfileFragment newInstance() { return new ProfileFragment(); }
}