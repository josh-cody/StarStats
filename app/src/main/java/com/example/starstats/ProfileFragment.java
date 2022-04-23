package com.example.starstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    private EditText myTag;
    private TextView currentTag;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        myTag = v.findViewById(R.id.myTag); currentTag = v.findViewById(R.id.currentTag);

        SharedPreferences pref = getActivity().getSharedPreferences("def", Context.MODE_PRIVATE);
        if(!pref.getString("myTag","").equals("")) { currentTag.setText("Current tag: " + pref.getString("myTag", "")); }
        else { currentTag.setText("No current tag"); }
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences pref = getActivity().getSharedPreferences("def", Context.MODE_PRIVATE);
        if(!myTag.getText().toString().equals("")) { pref.edit().putString("myTag", myTag.getText().toString()).apply(); }
    }

    public static ProfileFragment newInstance() { return new ProfileFragment(); }
}