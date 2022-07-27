package com.starstats.starstats;

import androidx.annotation.ColorLong;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Leaderboards extends AppCompatActivity {

    private RecyclerView leaderboardRecyclerView;
    private ArrayList<Profile> profileList;
    private String RESPONSE_FROM_API;
    private LeaderboardAdapter adapter;
    private JSONObject jsonObject, iconMapping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView);

        if(pref.getBoolean("fromLeaderboards", false)) {
            edit.putBoolean("fromLeaderboards", false).apply();
        }
        else {
            ApiThread apiThread = new ApiThread(getApplicationContext(), 6);
            apiThread.start();
            try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        RESPONSE_FROM_API = pref.getString("leaderboardresponse", "");

        try { jsonObject = new JSONObject(RESPONSE_FROM_API); } catch (JSONException e) { e.printStackTrace(); }

        try {
            InputStream is = getAssets().open("iconmapping.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            iconMapping = new JSONObject(jsonString);
        } catch (IOException | JSONException e) { e.printStackTrace(); }


        profileList = new ArrayList<>();
        try { populatePlayerList(); } catch (JSONException e) { e.printStackTrace(); }
        setLeaderboardAdapter();
    }

    private void goToProfile() {
        Intent toProfile = new Intent(this, ProfilePage.class);
        startActivity(toProfile);
    }

    private void setLeaderboardAdapter() {
        adapter = new Leaderboards.LeaderboardAdapter(profileList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        leaderboardRecyclerView.setLayoutManager(layoutManager);
        leaderboardRecyclerView.setAdapter(adapter);

    }

    private void populatePlayerList() throws JSONException {
        JSONArray players = new JSONArray(jsonObject.get("items").toString());

        for(int i = 0; i < players.length(); i++) {
            JSONObject tmp = new JSONObject(players.get(i).toString());
            JSONObject tmpID = tmp.getJSONObject("icon");
            profileList.add(new Profile(tmp.getString("name"), tmp.getString("tag"), tmp.getInt("trophies"), String.valueOf(tmpID.getInt("id")), tmp.getString("nameColor")));
        }
    }

    @Override
    public void onBackPressed() {
        toMainActivity();
    }

    private void toMainActivity() {
        Intent toMainActivity = new Intent(this, MainActivity.class);
        startActivity(toMainActivity);
    }

    class LeaderboardAdapter extends RecyclerView.Adapter<Leaderboards.LeaderboardAdapter.ViewHolder> {

        private final ArrayList<Profile> profileList;
        public LeaderboardAdapter(ArrayList<Profile> profileList) {   this.profileList = profileList;  }
        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView name, trophies, tag, rank;
            private ConstraintLayout leaderboardBackground;
            private ImageView leaderboardPicture;
            public ViewHolder(View view) {
                super(view);
                leaderboardPicture = view.findViewById(R.id.leaderboardPicture); leaderboardBackground = view.findViewById(R.id.leaderboardPlayerBackground); rank = view.findViewById(R.id.clubRank); name = view.findViewById(R.id.playerName); trophies = view.findViewById(R.id.playerTrophies); tag = view.findViewById(R.id.clubMemberTag);
            }
        }

        @NonNull
        @Override
        public Leaderboards.LeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_player, parent, false);
            return new Leaderboards.LeaderboardAdapter.ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Leaderboards.LeaderboardAdapter.ViewHolder holder, int position) {
            Profile tmpProfile = profileList.get(position);

            System.out.println(tmpProfile.getID());
            holder.name.setText(tmpProfile.getName());

            String tmp = tmpProfile.getNameColor();

            tmp = tmp.substring(2);

            int color = Color.parseColor("#"+tmp);


            holder.name.setTextColor(color);
            holder.trophies.setText(Integer.toString(tmpProfile.getTrophies()));
            holder.tag.setText(tmpProfile.getTag());
            holder.rank.setText(Integer.toString(position + 1));


            String toSearch = "";
            try { toSearch = iconMapping.getString(tmpProfile.getID()); } catch (JSONException e) { e.printStackTrace(); }

            char[] chars = toSearch.toCharArray();

            for (char c : chars) {
                if (Character.isDigit(c)) {
                    toSearch = "i" + toSearch;
                    break;
                }
            }
            int id1 = getApplicationContext().getResources().getIdentifier(toSearch, "drawable", getApplicationContext().getPackageName());

            if(id1 == 0x0) {
                holder.leaderboardPicture.setImageResource(R.drawable.shelly);
            }
            else {
                holder.leaderboardPicture.setImageResource(id1);
            }
            holder.leaderboardBackground.setOnClickListener(view -> {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("def", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("tag", tmpProfile.getTag().replace("#", "")).apply();
                edit.putBoolean("fromLeaderboards", true).apply();
                goToProfile();
            });
        }

        @Override
        public long getItemId(int position) {
            return profileList.get(position).hashCode();
        }

        @Override
        public int getItemCount() {
            return profileList.size();
        }
    }

}