package com.example.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

    TextView name, highestTrophies, currentTrophies;
    RecyclerView brawlers;
    ScrollView scrollView;
    BattleLogFragment frag;
    BrawlerAdapter adapter;
    private ArrayList<Brawler> brawlerList;
    ApiThread apiThread;
    Button raritySort, trophySort, battleLog, thisIsMe;
    AtomicBoolean isWindowOpen = new AtomicBoolean(false);
    String tag;
    JSONObject jsonObject;
    private final List<String> rarityOrder = Arrays.asList("shelly","nita","colt","bull","jessie","brock","dynamike","bo","tick","8-bit","emz","stu","el primo","barley","poco","rosa","rico","darryl","penny","carl","jacky","piper","pam","frank","bibi","bea","nani","edgar","griff","grom","mortis","tara","gene","max","mr. p", "sprout", "byron", "squeak","spike","crow","leon","sandy","amber","meg","gale","surge","colette","lou","colonel ruffs","belle","buzz","ash","lola","fang","eve", "janet");

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        tag = pref.getString("tag", "default");
        this.getActionBar().hide();
        apiThread = new ApiThread(getApplicationContext(), tag, 1);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        thisIsMe = findViewById(R.id.thisIsMe); battleLog = findViewById(R.id.battleLog); scrollView = findViewById(R.id.brawlersScroll); trophySort = findViewById(R.id.trophySort); raritySort = findViewById(R.id.raritySort); brawlers = findViewById(R.id.recyclerView); name = findViewById(R.id.name); highestTrophies = findViewById(R.id.highestTrophies); currentTrophies = findViewById(R.id.currentTrophies);

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
        battleLog.setOnClickListener( view -> {
            frag = BattleLogFragment.newInstance(tag);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).replace(R.id.fragmentContainerViewProfile, frag).commit();
            isWindowOpen.set(true);
        });

        thisIsMe.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Widget information updated", Toast.LENGTH_SHORT).show();
            edit.putString("widgetPlayerTag", tag).apply();
            edit.putString("widgetresponse", jsonObject.toString()).apply();
        });

        battleLog.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
            if(isWindowOpen.get()){
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(frag).commit();
                isWindowOpen.set(false);
            }
            else {
                super.onBackPressed();
                toMainActivity();
            }
    }

    @SuppressLint("NewApi")
    private ArrayList<Brawler> sortBrawlerListTrophy(ArrayList<Brawler> inputBrawlerList) throws ArrayIndexOutOfBoundsException {
        inputBrawlerList.sort(Comparator.comparingInt((Brawler brawler) -> brawler.trophies).reversed());
        return inputBrawlerList;
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        brawlers.setLayoutManager(layoutManager);
        brawlers.setItemAnimator(new DefaultItemAnimator());
        brawlers.setAdapter(adapter);
    }

    private void populateBrawlerList() throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("brawlers");
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject tmpBrawler = (JSONObject) jsonArray.get(i);
            brawlerList.add(new Brawler(tmpBrawler.getString("name"), tmpBrawler.getInt("power"), tmpBrawler.getInt("trophies"), tmpBrawler.getInt("rank"), tmpBrawler.getInt("highestTrophies")));
        }
    }

    @SuppressLint("SetTextI18n")
    public void setValues(String RESPONSE_FROM_API) throws JSONException {
        jsonObject = new JSONObject(RESPONSE_FROM_API);
        name.setText(jsonObject.getString("name"));
        name.setTextColor(Color.BLACK);
        highestTrophies.setText("Highest trophies: " + jsonObject.getString("highestTrophies"));
        currentTrophies.setText("Current trophies: " + jsonObject.getString("trophies"));
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
            holder.brawlerPortrait.setMinimumHeight((width/2)-36);
            try{ holder.brawlerPortrait.setImageResource(id); } catch(Error e) { holder.brawlerPortrait.setImageResource(R.drawable.bs_logo); }
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
            return brawlerList.size();
        }
    }
}