package com.starstats.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClubPage extends AppCompatActivity {

    private ArrayList<Profile> profileList = new ArrayList<>();
    private ApiThread apiThread;
    private AdView mAdView;
    private String RESPONSE_FROM_API, clubTag;
    private JSONObject jsonObject, iconMapping;
    private TextView clubNamePage, clubTagPage, clubDescPage, clubTrophiesPage;
    private RecyclerView clubRecycleView;
    private ScrollView clubMembersScroll;
    private ClubAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_page);


        mAdView = findViewById(R.id.adViewClubPage);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        clubMembersScroll = findViewById(R.id.clubMembersScroll); clubRecycleView = findViewById(R.id.clubRecycleView); clubNamePage = findViewById(R.id.clubNamePage); clubTagPage = findViewById(R.id.clubTag); clubDescPage = findViewById(R.id.clubDesc); clubTrophiesPage = findViewById(R.id.clubTrophies);

        try {
            InputStream is = getAssets().open("iconmapping.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            iconMapping = new JSONObject(jsonString);
        } catch (IOException | JSONException e) { e.printStackTrace(); }

        clubTag = pref.getString("tag", "");

        apiThread = new ApiThread(getApplicationContext(), clubTag, 4);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }

        RESPONSE_FROM_API = pref.getString("clubresponse","");

        try { setValues(); } catch (JSONException e) { e.printStackTrace(); }
        try { populatePlayerList(); } catch (JSONException e) { e.printStackTrace(); }

        setClubAdapter();
    }

    private void setClubAdapter() {
        adapter = new ClubAdapter(profileList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        clubRecycleView.setLayoutManager(layoutManager);
        clubRecycleView.setAdapter(adapter);

    }

    private void setValues() throws JSONException {
        jsonObject = new JSONObject(RESPONSE_FROM_API);
        clubNamePage.setText(jsonObject.getString("name"));
        clubTagPage.setText(clubTag);
        clubDescPage.setText(jsonObject.getString("description"));
        clubTrophiesPage.setText(jsonObject.getString("trophies"));
    }

    private void populatePlayerList() throws JSONException {
        JSONArray members = new JSONArray(jsonObject.get("members").toString());
        for(int i = 0; i < members.length(); i++) {
            JSONObject tmp = new JSONObject(members.get(i).toString());
            JSONObject tmpID = tmp.getJSONObject("icon");
            profileList.add(new Profile(tmp.getString("name"), tmp.getString("tag"), tmp.getString("role"), tmp.getString("nameColor"), String.valueOf(tmpID.getInt("id")), tmp.getInt("trophies")));
        }
    }

    @Override
    public void onBackPressed() {
            goToMainActivity();
    }

    private void goToMainActivity() {
        Intent toMainActivity = new Intent(this, MainActivity.class);
        startActivity(toMainActivity);
    }

    class ClubAdapter extends RecyclerView.Adapter<ClubPage.ClubAdapter.ViewHolder> {

        private final ArrayList<Profile> profileList;
        public ClubAdapter(ArrayList<Profile> profileList) {   this.profileList = profileList;  }
        Map<String, String> ranks = new HashMap<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView name, trophies, role, clubMemberTag, clubRank;
            private ImageView profilePicture;
            public ViewHolder(View view) {
                super(view);
                profilePicture = view.findViewById(R.id.clubMemberProfilePicture); clubRank = view.findViewById(R.id.clubRank); name = view.findViewById(R.id.playerName); trophies = view.findViewById(R.id.playerTrophies); role = view.findViewById(R.id.playerRole); clubMemberTag = view.findViewById(R.id.clubMemberTag);
            }
        }

        @NonNull
        @Override
        public ClubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_member, parent, false);

            ranks.put("member","Member");
            ranks.put("senior", "Senior");
            ranks.put("vicePresident", "Vice President");
            ranks.put("president", "President");

            return new ClubPage.ClubAdapter.ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Profile tmpProfile = profileList.get(position);

            holder.name.setText(tmpProfile.getName());

            String tmp = tmpProfile.getNameColor();

            tmp = tmp.substring(2);

            int color = Color.parseColor("#"+tmp);

            holder.name.setTextColor(color);

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

            if(id1 == 0x0) { holder.profilePicture.setImageResource(R.drawable.shelly); }
            else { holder.profilePicture.setImageResource(id1); }

            holder.role.setText(ranks.get(tmpProfile.getRole()));
            holder.trophies.setText(Integer.toString(tmpProfile.getTrophies()));
            holder.clubMemberTag.setText(tmpProfile.getTag());
            holder.clubRank.setText(Integer.toString(position + 1));

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