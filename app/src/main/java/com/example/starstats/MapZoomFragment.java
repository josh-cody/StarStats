package com.example.starstats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MapZoomFragment extends Fragment {

    private static final String ARG_PARAM1 = "mapID";
    private static final String ARG_PARAM2 = "mapName";

    private int mapID;
    private String mapName;

    private ImageButton close;
    private TextView mapNameTextView;
    private ImageView mapZoom;


    public MapZoomFragment() {}

    public static MapZoomFragment newInstance(int mapID, String mapName) {
        MapZoomFragment fragment = new MapZoomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, mapID);
        args.putString(ARG_PARAM2, mapName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mapID = getArguments().getInt(ARG_PARAM1);
            mapName = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_zoom, container, false);
        close = v.findViewById(R.id.close); mapZoom = v.findViewById(R.id.mapZoom); mapNameTextView = v.findViewById(R.id.mapNameTextView);
        mapNameTextView.setText(mapName);
        mapZoom.setImageResource(mapID);
        close.setOnClickListener(view -> {
            getActivity().onBackPressed();
        });
        return v;
    }

}