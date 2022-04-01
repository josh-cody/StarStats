package com.example.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Maps extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ThisMap> mapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        recyclerView = findViewById(R.id.mapsList);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("def", Context.MODE_PRIVATE);

        ApiThread apiThread = new ApiThread(getApplicationContext(), 2);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        mapList = new ArrayList<>();
        try { populateMapList(pref.getString("mapresponse", "NO RESPONSE")); } catch (JSONException e) { e.printStackTrace(); }
        try { setMapAdapter(); } catch (JSONException e) { e.printStackTrace(); }
    }

    public void populateMapList(String RESPONSE_FROM_API) throws JSONException {
        JSONArray jsonArray = new JSONArray(RESPONSE_FROM_API);
        for(int i = 0; i < jsonArray.length()-1; i++){
            JSONObject t = (JSONObject) jsonArray.get(i);
            JSONObject tmpMap = (JSONObject) t.get("event");
            mapList.add(new ThisMap(tmpMap.getString("map"), tmpMap.getString("mode")));
        }
    }

    public void setMapAdapter() throws JSONException {
        MapAdapter mapAdapter = new MapAdapter(mapList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        for(int a = 0; a < 20; a++) { recyclerView.addItemDecoration(itemDecoration); } //TEMPORARY SPACING: INEFFICIENT
        recyclerView.setAdapter(mapAdapter);
    }

    class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {
        ArrayList<ThisMap> mapList;
        Map<String, String> modes = new HashMap<>();

        public MapAdapter(ArrayList<ThisMap> mapList) {this.mapList = mapList;}

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mapName, modeName;
            private final ConstraintLayout mapBack;
            private final ImageView map;
            public ViewHolder(View view) {
                super(view);
                mapName = view.findViewById(R.id.mapName); mapBack = view.findViewById(R.id.mapBack); modeName = view.findViewById(R.id.modeName); map = view.findViewById(R.id.map);
            }
        }

        @NonNull
        @Override
        public MapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map, parent, false);
            modes.put("gemGrab","Gem Grab");
            modes.put("soloShowdown", "Showdown");
            modes.put("brawlBall","Brawl Ball");
            modes.put("heist", "Heist");
            modes.put("bounty","Bounty");
            modes.put("payload","Payload");
            modes.put("hotZone","Hot Zone");
            modes.put("basketBrawl", "Basket Brawl");
            return new ViewHolder(v);
        }

        public void goToMapZoom(int ID, String mapName, Context context) {
            Intent i = new Intent(context, MapZoom.class);
            i.putExtra("mapID", ID);
            i.putExtra("mapName", mapName);
            startActivity(i);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            ThisMap thisMap = mapList.get(position);
            if(!thisMap.mode.equals("duoShowdown") && !thisMap.mode.equals("roboRumble") && !thisMap.mode.equals("bossFight")) {
                holder.mapName.setText(thisMap.map);

                holder.modeName.setText(modes.get(thisMap.mode));

                Context context1 = holder.mapBack.getContext();
                int id1 = context1.getResources().getIdentifier(thisMap.mode.toLowerCase(), "drawable", context1.getPackageName());
                holder.mapBack.setBackgroundResource(id1);


                String filenameString = formatStringForFilename(thisMap.map.toLowerCase(Locale.ROOT));
                int id2 = context1.getResources().getIdentifier(filenameString, "drawable", context1.getPackageName());
                holder.map.setImageResource(id2);

                holder.map.setOnClickListener(view -> goToMapZoom(id2, thisMap.map, context1));
                holder.mapBack.setOnClickListener(view -> goToMapZoom(id2, thisMap.map, context1));
            }
            else { holder.mapBack.setVisibility(View.GONE); holder.mapBack.setMaxHeight(0); }
        }

        private String formatStringForFilename(String input) {
            input = input.replaceAll(" ", "");
            input = input.replaceAll("\\.", "");
            input = input.replaceAll("-", "");
            input = input.replaceAll("8", "e");
            return input;
        }

        @Override
        public int getItemCount() {
            return mapList.size();
        }
    }
}