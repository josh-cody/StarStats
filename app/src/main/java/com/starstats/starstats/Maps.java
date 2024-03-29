package com.starstats.starstats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Maps extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ThisMap> mapList;
    private Set<String> inViewHolder = new HashSet<>();
    private AtomicBoolean isWindowOpen = new AtomicBoolean(false);
    private MapZoomFragment mapZoomFragment;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAdView = findViewById(R.id.adViewMaps);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        recyclerView = findViewById(R.id.mapsList);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("def", Context.MODE_PRIVATE);

        ApiThread apiThread = new ApiThread(getApplicationContext(), 2);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        mapList = new ArrayList<>();
        try { populateMapList(pref.getString("mapresponse", "NO RESPONSE")); } catch (JSONException e) { e.printStackTrace(); }
        try { setMapAdapter(); } catch (JSONException e) { e.printStackTrace(); }
    }

    @Override
    public void onBackPressed() {
        if(isWindowOpen.get()){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(mapZoomFragment).commit();
            isWindowOpen.set(false);
        }
        else {
            super.onBackPressed();
            toMainActivity();
        }
    }

    private void toMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void populateMapList(String RESPONSE_FROM_API) throws JSONException {
        JSONArray jsonArray = new JSONArray(RESPONSE_FROM_API);
        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject t = (JSONObject) jsonArray.get(i);
            String eventStart = t.getString("startTime");
            String eventEnd = t.getString("endTime");

            JSONObject tmpMap = (JSONObject) t.get("event");
            if(!tmpMap.getString("mode").equals("duoShowdown") && !tmpMap.getString("mode").equals("roboRumble") && !tmpMap.getString("mode").equals("bossFight") && !tmpMap.getString("mode").equals("bigGame")) {
                mapList.add(new ThisMap(tmpMap.getString("map"), tmpMap.getString("mode"), eventStart, eventEnd, t.getInt("slotId")));
            }
        }
    }

    public void setMapAdapter() throws JSONException {
        MapAdapter mapAdapter = new MapAdapter(mapList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1){
            @Override
            public boolean canScrollVertically() {
                return !isWindowOpen.get();
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mapAdapter);
    }

    class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {
        ArrayList<ThisMap> mapList;
        Map<String, String> modes = new HashMap<>();

        public MapAdapter(ArrayList<ThisMap> mapList) { this.mapList = mapList; }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mapName, modeName, timeRemaining;
            private final ImageView map, backgroundImage;
            private final LinearLayout mapBack;
            public ViewHolder(View view) {
                super(view);
                timeRemaining = view.findViewById(R.id.timeRemaining); backgroundImage = view.findViewById(R.id.backgroundImage); mapBack = view.findViewById(R.id.mapBack); mapName = view.findViewById(R.id.mapName); modeName = view.findViewById(R.id.modeName); map = view.findViewById(R.id.map);
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
            modes.put("knockout", "Knockout");
            modes.put("basketBrawl", "Basket Brawl");
            modes.put("duels", "Duels");
            modes.put("wipeout","Wipeout");
            modes.put("botDrop", "Bot Drop");
            modes.put("hunters","Hunters");
            modes.put("unknown", "Unknown");
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ThisMap thisMap = mapList.get(position);

            java.util.Date eventStart = Date.from(Instant.parse(thisMap.eventStart));
            java.util.Date eventEnd = Date.from( Instant.parse(thisMap.eventEnd));
            java.util.Date localTime = Date.from(Instant.now());

            if(!localTime.before(eventStart) && thisMap.slotID < 20) {
                inViewHolder.add(thisMap.mode);

                long timeRemaining = eventEnd.getTime() - localTime.getTime();
                long difference_In_Hours= (timeRemaining / (1000 * 60 * 60) % 24);
                long difference_In_Minutes = (timeRemaining / (1000 * 60)) % 60;

                holder.timeRemaining.setText(MessageFormat.format("{0}h {1}m", difference_In_Hours, difference_In_Minutes));

                holder.mapName.setText(thisMap.map);
                holder.modeName.setText(modes.get(thisMap.mode));

                Context context1 = holder.mapBack.getContext();
                int id1 = context1.getResources().getIdentifier(thisMap.mode.toLowerCase(), "drawable", context1.getPackageName());
                holder.backgroundImage.setImageResource(id1);

                String filenameString = formatStringForFilename(thisMap.map.toLowerCase(Locale.ROOT));
                int id2 = context1.getResources().getIdentifier(filenameString, "drawable", context1.getPackageName());
                holder.map.setImageResource(id2);

                holder.map.setOnClickListener(view -> {
                    if(!isWindowOpen.get()) {
                        mapZoomFragment = MapZoomFragment.newInstance(id2, thisMap.map);
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).replace(R.id.fragmentContainerView3, mapZoomFragment).commit();
                        isWindowOpen.set(true);
                    }
                });

                holder.mapBack.setOnClickListener(view -> {
                    if(!isWindowOpen.get()) {
                        mapZoomFragment = MapZoomFragment.newInstance(id2, thisMap.map);
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).replace(R.id.fragmentContainerView3, mapZoomFragment).commit();
                        isWindowOpen.set(true);
                    }
                });
            }
            else {
                holder.mapBack.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.mapBack.getLayoutParams();
                params.height = 0;
                holder.mapBack.setLayoutParams(params);
            }
        }

        private String formatStringForFilename(String input) {
            input = input.replaceAll(" ", "");
            input = input.replaceAll("\\.", "");
            input = input.replaceAll("-", "");
            input = input.replaceAll("8", "e");
            input = input.replaceAll("'", "");
            input = input.replaceAll(":","");
            input = input.replaceAll("!","");
            return input;
        }

        @Override
        public int getItemCount() {
            return mapList.size();
        }
    }
}