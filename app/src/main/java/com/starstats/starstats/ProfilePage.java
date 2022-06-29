package com.starstats.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfilePage extends AppCompatActivity {

    TextView highestTrophies, currentTrophies, threeWins, soloWins, duoWins, playerTag, playerName, level, clubName, brawlersUnlocked;
    RecyclerView brawlers;
    ScrollView scrollView;
    BrawlerAdapter adapter;
    WidgetInstructionFragment instr;
    ConstraintLayout con;
    private ArrayList<Brawler> brawlerList;
    BrawlerPowers brawlerPowers;
    ApiThread apiThread;
    Button raritySort, trophySort, follow;
    AtomicBoolean isBrawlerWindowOpen = new AtomicBoolean(false);
    AtomicBoolean isInstructionWindowOpen = new AtomicBoolean(false);
    String tag, tmpsp1, tmpsp2, tmpg1, tmpg2;
    JSONObject jsonObject, tmpStar1, tmpStar2, tmpGad1, tmpGad2;
    private AdView mAdView;
    private final List<String> rarityOrder = Arrays.asList("shelly","nita","colt","bull","jessie","brock","dynamike","bo","tick","8-bit","emz","stu","el primo","barley","poco","rosa","rico","darryl","penny","carl","jacky","piper","pam","frank","bibi","bea","nani","edgar","griff","grom", "bonnie", "mortis","tara","gene","max","mr. p", "sprout", "byron", "squeak","spike","crow","leon","sandy","amber","meg","gale","surge","colette","lou","colonel ruffs","belle","buzz","ash","lola","fang","eve","janet");

    int tmpspeed, tmphealth, tmpdamage, tmpvision, tmpshield = 0;

    JSONArray jsonStarpowers, jsonGadgets, jsonGears;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mAdView = findViewById(R.id.adViewProfile);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        tag = pref.getString("tag", "default");
        this.getActionBar().hide();
        apiThread = new ApiThread(getApplicationContext(), tag, 1);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        currentTrophies = findViewById(R.id.currentTrophiesExpanded); highestTrophies = findViewById(R.id.highestTrophiesExpanded); threeWins = findViewById(R.id.threeWins); soloWins = findViewById(R.id.soloWins); duoWins = findViewById(R.id.duoWins); playerTag = findViewById(R.id.playerTagExpanded); playerName = findViewById(R.id.playerNameExpanded); level = findViewById(R.id.playerLevel); clubName = findViewById(R.id.clubName); brawlersUnlocked = findViewById(R.id.brawlersUnlocked);
        con = findViewById(R.id.brawlersCon); follow = findViewById(R.id.follow); scrollView = findViewById(R.id.brawlersScroll); trophySort = findViewById(R.id.trophySort); raritySort = findViewById(R.id.raritySort); brawlers = findViewById(R.id.recyclerView);

        brawlers.setVerticalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        try {
            setValues(pref.getString("response", ""));
            if(!pref.contains("recent1")) { edit.putString("recent1", tag).apply(); }
            else if (!pref.contains("recent2")) { edit.putString("recent2", tag).apply(); }
            else if (!pref.contains("recent3")) { edit.putString("recent3", tag).apply(); }

            if(pref.contains("recent1") && pref.contains("recent2") && pref.contains("recent3") && !pref.getString("recent1","").equals(tag) && !pref.getString("recent2","").equals(tag) && !pref.getString("recent3","").equals(tag)) {
                edit.putString("recent3", pref.getString("recent2", "")).apply();
                edit.putString("recent2", pref.getString("recent1","")).apply();
                edit.putString("recent1", tag).apply();
            }
        } catch (JSONException e) {
            toMainActivity();
            Toast.makeText(getApplicationContext(),"Make sure you input a correct tag!", Toast.LENGTH_SHORT).show();
        }

        brawlerList = new ArrayList<>();
        try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace(); }
        try { brawlerList = sortBrawlerListRarity(brawlerList);
        } catch(ArrayIndexOutOfBoundsException a) {
            brawlerList = new ArrayList<>();
            try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace(); }
        }
        setBrawlerAdapter();

        con.setOnClickListener(view -> {
            if(isInstructionWindowOpen.get()) {
                getSupportFragmentManager().beginTransaction().remove(instr).commit();
                isInstructionWindowOpen.set(false);
            }
        });

        trophySort.setOnClickListener(view -> {
            sortBrawlerListTrophy(brawlerList);
            adapter.notifyChanges();

        });

        raritySort.setOnClickListener(view -> {
            ArrayList<Brawler> tmp = sortBrawlerListRarity(brawlerList);
            brawlerList.clear();
            brawlerList.addAll(tmp);
            adapter.notifyChanges();
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

    @Override
    public void onBackPressed() {
            if(isBrawlerWindowOpen.get()){
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_pop_enter_anim, R.anim.nav_default_exit_anim).remove(brawlerPowers).commit();
                isBrawlerWindowOpen.set(false);
            }
            else if(isInstructionWindowOpen.get()) {
                getSupportFragmentManager().beginTransaction().remove(instr).commit();
                isInstructionWindowOpen.set(false);
            }
            else {
                super.onBackPressed();
                toMainActivity();
            }
    }

    @SuppressLint("NewApi")
    private void sortBrawlerListTrophy(ArrayList<Brawler> inputBrawlerList) throws ArrayIndexOutOfBoundsException {
        inputBrawlerList.sort(Comparator.comparingInt((Brawler brawler) -> brawler.trophies).reversed());
    }

    private ArrayList<Brawler> sortBrawlerListRarity(ArrayList<Brawler> brawlerList) throws ArrayIndexOutOfBoundsException {
        ArrayList<Brawler> tmp = new ArrayList<>();
        if(brawlerList.size() == 0) { return brawlerList; }
        for(int i=0; i <= rarityOrder.size()-1; i++) {
            for(int j=0; j <= brawlerList.size()-1; j++) {
                if(brawlerList.get(j).name.toLowerCase(Locale.ROOT).equals(rarityOrder.get(i))) {
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

        jsonObject = new JSONObject(RESPONSE_FROM_API);
        playerName.setText(jsonObject.getString("name"));
        playerTag.setText(jsonObject.getString("tag"));
        currentTrophies.setText(jsonObject.getString("trophies"));
        highestTrophies.setText(jsonObject.getString("highestTrophies"));
        soloWins.setText(jsonObject.getString("soloVictories"));
        duoWins.setText(jsonObject.getString("duoVictories"));
        level.setText(jsonObject.getString("expLevel"));
        threeWins.setText(jsonObject.getString("3vs3Victories"));
        JSONObject jsonObject2 = (JSONObject) jsonObject.get("club");
        clubName.setText(jsonObject2.getString("name"));
        JSONArray brawlers = jsonObject.getJSONArray("brawlers");
        brawlersUnlocked.setText(brawlers.length() + " / 57");
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
            holder.powerLevel.setText(brawler.name + " " + brawler.powerLevel);
            holder.trophyHighest.setImageResource(R.drawable.trophy);
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