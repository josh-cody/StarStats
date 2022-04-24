package com.example.starstats;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }


    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        musicSetting = v.findViewById(R.id.musicSetting);

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();



        if(pref.contains("musicSetting")) {
            if(pref.getBoolean("musicSetting", false)) {
                musicSetting.setChecked(true);
                 getActivity().startService(new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class));
            }
            else {
                musicSetting.setChecked(false);
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class));
            }
        }
        else {
            //close fragment
        }

        musicSetting.setOnCheckedChangeListener((compoundButton, b) ->
        {
            if(!pref.getBoolean("musicSetting",false)) {
                getActivity().startService(new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class));
                edit.putBoolean("musicSetting", true);
            }
            else {
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), BackgroundSoundService.class));
                edit.putBoolean("musicSetting", false);
            }
            edit.putBoolean("musicSetting", !pref.getBoolean("musicSetting", false)).apply();

        });


        return v;
    }

    public static ProfileFragment newInstance() { return new ProfileFragment(); }
}