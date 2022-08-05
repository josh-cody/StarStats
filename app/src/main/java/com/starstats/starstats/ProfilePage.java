package com.starstats.starstats;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfilePage extends AppCompatActivity {

    TextView highestTrophies, currentTrophies, threeWins, soloWins, duoWins, playerTag, playerName, level, clubName, brawlersUnlocked, raritySortText;
    RecyclerView brawlers;
    ScrollView scrollView;
    FloatingActionButton sortButton;
    BrawlerAdapter adapter;
    WidgetInstructionFragment instr;
    ConstraintLayout con;
    private ArrayList<Brawler> brawlerList;
    BrawlerPowers brawlerPowers;
    ApiThread apiThread;
    Button follow;
    AtomicBoolean isBrawlerWindowOpen = new AtomicBoolean(false);
    AtomicBoolean isInstructionWindowOpen = new AtomicBoolean(false);
    String tag, tmpsp1, tmpsp2, tmpg1, tmpg2;
    JSONObject jsonObject, tmpStar1, tmpStar2, tmpGad1, tmpGad2;
    int tmpspeed, tmphealth, tmpdamage, tmpvision, tmpshield = 0;
    JSONArray jsonStarpowers, rarityFromJSON, jsonGadgets, jsonGears;
    JSONObject iconMapping;
    ImageView profilePicture;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        try {
            InputStream is = getAssets().open("rarityorder.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            rarityFromJSON = new JSONArray(jsonString);
        } catch (IOException | JSONException e) {
            toMainActivity();
            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
        }

        AdView mAdView = findViewById(R.id.adViewProfile);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        tag = pref.getString("tag", "default");

        apiThread = new ApiThread(getApplicationContext(), tag, 1);
        apiThread.start();
        try { apiThread.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        raritySortText = findViewById(R.id.raritySortText); currentTrophies = findViewById(R.id.currentTrophiesExpanded); highestTrophies = findViewById(R.id.highestTrophiesExpanded); threeWins = findViewById(R.id.threeWins); soloWins = findViewById(R.id.soloWins); duoWins = findViewById(R.id.duoWins); playerTag = findViewById(R.id.playerTagExpanded); playerName = findViewById(R.id.playerNameExpanded); level = findViewById(R.id.playerLevel); clubName = findViewById(R.id.clubName); brawlersUnlocked = findViewById(R.id.brawlersUnlocked);
        profilePicture = findViewById(R.id.profilePicture); sortButton = findViewById(R.id.sortButton); con = findViewById(R.id.brawlersCon); follow = findViewById(R.id.follow); scrollView = findViewById(R.id.brawlersScroll); brawlers = findViewById(R.id.recyclerView);

        brawlers.setVerticalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        try {
            InputStream is = getAssets().open("iconmapping.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            iconMapping = new JSONObject(jsonString);
        } catch (IOException | JSONException e) { e.printStackTrace(); }

        try {
            setValues(pref.getString("response", ""));
                if(pref.contains("profileRecents")) {
                    Set<String> fetch = pref.getStringSet("profileRecents", new HashSet<>());
                    HashSet<String> profileRecents = new HashSet<>();
                    profileRecents.addAll(fetch);
                    if(profileRecents.size() >= 3) {
                        Object[] tmp = profileRecents.toArray();
                        ArrayList<String> tmp2 = new ArrayList<>();
                        for(Object o : tmp) { tmp2.add((String) o); }
                        tmp2.remove(0);
                        tmp2.add(tag);
                        HashSet<String> toAdd = new HashSet<>();
                        toAdd.addAll(tmp2);
                        edit.putStringSet("profileRecents", toAdd).apply();
                    }
                    else {
                        profileRecents.add(tag);
                        edit.putStringSet("profileRecents", profileRecents).apply();
                    }
                } else {
                    Set<String> profileRecents = new HashSet<>();
                    profileRecents.add(tag);
                    edit.putStringSet("profileRecents", profileRecents).apply();
                }
        } catch (JSONException e) {
            toMainActivity();
            Toast.makeText(getApplicationContext(),"Make sure you put in a correct tag!", Toast.LENGTH_SHORT).show();
        }

        try {
            JSONObject tmpID = jsonObject.getJSONObject("icon");
            String id = String.valueOf(tmpID.getInt("id"));
            String toSearch = iconMapping.getString(id);
            char[] chars = toSearch.toCharArray();
            for (char c : chars) {
                if (Character.isDigit(c)) {
                    toSearch = "i" + toSearch;
                    break;
                }
            }
            int id1 = getApplicationContext().getResources().getIdentifier(toSearch, "drawable", getApplicationContext().getPackageName());
            profilePicture.setImageResource(id1);
        } catch (JSONException e) { e.printStackTrace(); }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace(); }

        if(!pref.contains("sort")) { edit.putString("sort", "r").apply(); }

        if(pref.getString("sort","").equals("r")) {
            raritySortText.setText("R");
            try { brawlerList = sortBrawlerListRarity(brawlerList);
            } catch(JSONException a) {
                brawlerList = new ArrayList<>();
                try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace(); }
            }
        }
        else if(pref.getString("sort", "").equals("tr")) {
            raritySortText.setText("TR");
            try{ sortBrawlerListTrophy(brawlerList);
            } catch (ArrayIndexOutOfBoundsException a) {
                brawlerList = new ArrayList<>();
                try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace(); }
            }
        }

        setBrawlerAdapter();

        con.setOnClickListener(view -> {
            if(isInstructionWindowOpen.get()) {
                getSupportFragmentManager().beginTransaction().remove(instr).commit();
                isInstructionWindowOpen.set(false);
            }
        });

        clubName.setOnClickListener( view -> {
            if(!clubName.getText().equals("NO NAME")) {
                try {
                    goToClub();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        sortButton.setOnClickListener(view -> {
            if(pref.getString("sort", "").equals("r")) {
                sortBrawlerListTrophy(brawlerList);
                adapter.notifyChanges();
                edit.putString("sort", "tr").apply();
                raritySortText.setText("TR");
            }
            else if(pref.getString("sort", "").equals("tr")) {
                ArrayList<Brawler> tmp = null;
                try { tmp = sortBrawlerListRarity(brawlerList); } catch (JSONException e) { e.printStackTrace(); }
                brawlerList.clear();
                assert tmp != null;
                brawlerList.addAll(tmp);
                adapter.notifyChanges();
                edit.putString("sort", "r").apply();
                raritySortText.setText("R");
            }
        });

        follow.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Widget information updated", Toast.LENGTH_SHORT).show();

            if(pref.getString("widgetPlayerTag", "").equals("") && !isInstructionWindowOpen.get()) {
                instr = WidgetInstructionFragment.newInstance();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).replace(R.id.instructionFragment, instr).commit();
                isInstructionWindowOpen.set(true);
            }

            edit.putString("widgetPlayerTag", tag).apply();
            edit.putString("widgetresponse", jsonObject.toString()).apply();
            follow.setBackgroundColor(0);
            follow.setText(R.string.following);
            follow.setClickable(false);

            Intent updateWidget = new Intent(this, BrawlerWidget.class);
            updateWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            if(pref.getInt("widgetID",-1) != -1) {
                int id = pref.getInt("widgetID", -1);
                updateWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
                sendBroadcast(updateWidget);
            }
        });

        if(tag.equals(pref.getString("widgetPlayerTag",""))) {
            follow.setText(R.string.following);
            follow.setBackgroundColor(0);
            follow.setClickable(false);
        }
    }

    private void goToClub() throws JSONException {
        Intent toClub = new Intent(this, ClubPage.class);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        JSONObject clubInfo = jsonObject.getJSONObject("club");
        edit.putString("tag", clubInfo.getString("tag").replace("#","")).apply();
        startActivity(toClub);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
            if(isBrawlerWindowOpen.get()){
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_pop_enter_anim, R.anim.nav_default_exit_anim).remove(brawlerPowers).commit();
                isBrawlerWindowOpen.set(false);
            }
            else if(isInstructionWindowOpen.get()) {
                getSupportFragmentManager().beginTransaction().remove(instr).commit();
                isInstructionWindowOpen.set(false);
            }
            else if(pref.getBoolean("fromLeaderboards", false)) {
                edit.putBoolean("fromLeaderboards", false).apply();
                goToLeaderboards();
            }
            else if(pref.getBoolean("fromClubPage", false)) {
                edit.putBoolean("fromClubPage", false).apply();
                try { goToClub(); }
                catch (JSONException e) { e.printStackTrace(); }
            }
            else {
                super.onBackPressed();
                toMainActivity();
            }
    }

    private void goToLeaderboards() {
        Intent toLeaderboards = new Intent(this, Leaderboards.class);
        startActivity(toLeaderboards);
    }

    @SuppressLint("NewApi")
    private void sortBrawlerListTrophy(ArrayList<Brawler> inputBrawlerList) throws ArrayIndexOutOfBoundsException {
        inputBrawlerList.sort(Comparator.comparingInt((Brawler brawler) -> brawler.trophies).reversed());
    }

    private ArrayList<Brawler> sortBrawlerListRarity(ArrayList<Brawler> brawlerList) throws JSONException {
        ArrayList<Brawler> tmp = new ArrayList<>();
        if(brawlerList.size() == 0) { return brawlerList; }
        for(int i=0; i <= rarityFromJSON.length()-1; i++) {
            for(int j=0; j <= brawlerList.size()-1; j++) {
                if(brawlerList.get(j).name.toLowerCase(Locale.ROOT).equals(rarityFromJSON.getString(i))) {
                    tmp.add(brawlerList.get(j));
                    j = brawlerList.size()-1;
                }
            }
        }
        return tmp;
    }

    private void toMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void setBrawlerAdapter() {
        adapter = new BrawlerAdapter(brawlerList);
        adapter.setHasStableIds(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2) {
            @Override
            public boolean canScrollVertically() {
                return !isBrawlerWindowOpen.get();
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };
        brawlers.setLayoutManager(layoutManager);
        brawlers.setItemAnimator(new DefaultItemAnimator());
        brawlers.setAdapter(adapter);
    }

    private void populateBrawlerList() throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("brawlers");
        for(int i = 0; i < jsonArray.length(); i++) {
            tmpspeed = 0;
            tmphealth = 0;
            tmpdamage = 0;
            tmpvision = 0;
            tmpshield = 0;

            tmpsp1 = "locked";
            tmpsp2 = "locked";

            tmpg1 = "locked";
            tmpg2 = "locked";

            JSONObject tmpBrawler = (JSONObject) jsonArray.get(i);

            try{jsonGears = new JSONArray(String.valueOf(tmpBrawler.getJSONArray("gears")));}
            catch (JSONException e) {
                tmpspeed = 0;
                tmphealth = 0;
                tmpdamage = 0;
                tmpvision = 0;
                tmpshield = 0;
            }

            try { jsonStarpowers = new JSONArray(String.valueOf(tmpBrawler.getJSONArray("starPowers")));}
            catch(JSONException e) { tmpsp1 = "locked"; tmpsp2 = "locked"; }

            try { jsonGadgets = new JSONArray(String.valueOf(tmpBrawler.getJSONArray("gadgets")));}
            catch (JSONException e) { tmpg1 = "locked"; tmpg2 = "locked"; }

            try {
                tmpStar1 = new JSONObject(String.valueOf(jsonStarpowers.get(0)));
                tmpsp1 = tmpStar1.getString("name");
            } catch (JSONException e) { tmpsp1 = "locked"; }

            try {
                tmpStar2 = new JSONObject(String.valueOf(jsonStarpowers.get(1)));
                tmpsp2 = tmpStar2.getString("name");
            } catch (JSONException e) { tmpsp2 = "locked"; }

            try {
                tmpGad1 = new JSONObject(String.valueOf(jsonGadgets.get(0)));
                tmpg1 = tmpGad1.getString("name");
            } catch (JSONException e) { tmpg1 = "locked"; }

            try {
                tmpGad2 = new JSONObject(String.valueOf(jsonGadgets.get(1)));
                tmpg2 = tmpGad2.getString("name");
            } catch (JSONException e) { tmpg2 = "locked"; }

            for(int j=0; j < jsonGears.length(); j++) {
                JSONObject tmp = new JSONObject(String.valueOf(jsonGears.getJSONObject(j)));
                switch (tmp.getString("name")) {
                    case "SPEED":
                        tmpspeed = tmp.getInt("level");
                        break;
                    case "HEALTH":
                        tmphealth = tmp.getInt("level");
                        break;
                    case "DAMAGE":
                        tmpdamage = tmp.getInt("level");
                        break;
                    case "VISION":
                        tmpvision = tmp.getInt("level");
                        break;
                    case "SHIELD":
                        tmpshield = tmp.getInt("level");
                        break;
                }
            }
            brawlerList.add(new Brawler(tmpBrawler.getString("name"), tmpBrawler.getInt("power"), tmpBrawler.getInt("trophies"), tmpBrawler.getInt("rank"), tmpBrawler.getInt("highestTrophies"), tmpsp1, tmpsp2, tmpg1, tmpg2, tmpspeed, tmphealth, tmpdamage, tmpvision, tmpshield));
        }
    }

    @SuppressLint("SetTextI18n")
    public void setValues(String RESPONSE_FROM_API) throws JSONException {
        jsonObject = new JSONObject(RESPONSE_FROM_API);
        playerName.setText(jsonObject.getString("name"));

        String tmp = jsonObject.getString("nameColor");
        tmp = tmp.substring(2);
        playerName.setTextColor(Color.parseColor("#"+tmp));

        playerTag.setText(jsonObject.getString("tag"));
        currentTrophies.setText(jsonObject.getString("trophies"));
        highestTrophies.setText(jsonObject.getString("highestTrophies"));
        soloWins.setText(jsonObject.getString("soloVictories"));
        duoWins.setText(jsonObject.getString("duoVictories"));
        level.setText(jsonObject.getString("expLevel"));
        threeWins.setText(jsonObject.getString("3vs3Victories"));
        try {
            JSONObject jsonObject2 = (JSONObject) jsonObject.get("club");
            clubName.setText(jsonObject2.getString("name"));
        } catch (JSONException e) {  clubName.setText("NO CLUB"); }

        JSONArray brawlers = jsonObject.getJSONArray("brawlers");
        brawlersUnlocked.setText(brawlers.length() + " / " + rarityFromJSON.length());
    }

    class BrawlerAdapter extends RecyclerView.Adapter<BrawlerAdapter.ViewHolder> {

        private final ArrayList<Brawler> brawlerList;
        public BrawlerAdapter(ArrayList<Brawler> brawlerList) {   this.brawlerList = brawlerList;  }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView powerLevel;
            private final TextView brawlerTrophies;
            private final TextView highestBrawler;
            private final ImageView brawlerPortrait, rank, trophy, trophyHighest;
            public ViewHolder(View view) {
                super(view);
                powerLevel = view.findViewById(R.id.powerLevel); brawlerTrophies = view.findViewById(R.id.brawlerTrophies); highestBrawler = view.findViewById(R.id.highestBrawler);
                brawlerPortrait = view.findViewById(R.id.brawlerPortrait); rank = view.findViewById(R.id.rank); trophy = view.findViewById(R.id.trophy); trophyHighest = view.findViewById(R.id.trophyHighest);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void notifyChanges() {
            try{super.notifyDataSetChanged();}
            catch (Error e ) {e.printStackTrace();}
        }

        @NonNull
        @Override
        public BrawlerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.brawler, parent, false);
            return new ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Brawler brawler = brawlerList.get(position);

            holder.highestBrawler.setText(Integer.toString(brawler.highestBrawler));
            holder.brawlerTrophies.setText(Integer.toString(brawler.trophies));
            holder.powerLevel.setText(brawler.name.replace("\n"," ") + " " + brawler.powerLevel);
            holder.trophyHighest.setImageResource(R.drawable.htt_summer_game_mode_icons_800x800);
            holder.trophy.setImageResource(R.drawable.trophy);

            Context context1 = holder.rank.getContext();
            String temp = "rank"+brawler.rank;
            int id1 = context1.getResources().getIdentifier(temp, "drawable", context1.getPackageName());

            holder.rank.setImageResource(id1);

            String filenameString = formatStringForFilename(brawler.name.toLowerCase());
            Context context = holder.brawlerPortrait.getContext();
            int id = context.getResources().getIdentifier(filenameString, "drawable", context.getPackageName());
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            holder.brawlerPortrait.setCropToPadding(true);
            holder.brawlerPortrait.setMinimumWidth((width/2)-36);
            holder.brawlerPortrait.setMaxWidth((width/2)-36);
            holder.brawlerPortrait.setMaxHeight(holder.brawlerPortrait.getWidth());
            try{ holder.brawlerPortrait.setImageResource(id); } catch(Error e) { holder.brawlerPortrait.setImageResource(R.drawable.bs_logo); }

            holder.itemView.setOnClickListener(view -> {
                if(!isBrawlerWindowOpen.get()) {
                    brawlerPowers = BrawlerPowers.newInstance(brawler.starpower1, brawler.starpower2, brawler.gadget1, brawler.gadget2, brawler.speedgear, brawler.healthgear, brawler.damagegear, brawler.visiongear, brawler.shieldgear);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_botton, R.anim.exit_to_bottom).replace(R.id.brawlerPowers, brawlerPowers).commit();
                    isBrawlerWindowOpen.set(true);
                }
                else {
                    getSupportFragmentManager().beginTransaction().remove(brawlerPowers).commit();
                    isBrawlerWindowOpen.set(false);
                }
            });
        }

        private String formatStringForFilename(String input) {
            input = input.replaceAll(" ", "");
            input = input.replaceAll("\\.", "");
            input = input.replaceAll("-", "");
            input = input.replaceAll("8", "e");
            input = input.replaceAll("\n", "");
            return input;
        }

        @Override
        public long getItemId(int position) {
            return brawlerList.get(position).hashCode();
        }

        @Override
        public int getItemCount() {
            return brawlerList.size();
        }
    }
}