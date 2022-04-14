package com.example.starstats;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProfilePage extends AppCompatActivity {

    TextView name, highestTrophies, currentTrophies;
    RecyclerView brawlers;
    private ArrayList<Brawler> brawlerList;
    ApiThread apiThread;
    Button raritySort, trophySort;
    String tag;
    JSONObject jsonObject;
    private List<String> rarityOrder = Arrays.asList("shelly","nita","colt","bull","jessie","brock","dynamike","bo","tick","8-bit","emz","stu","el primo","barley","poco","rosa","rico","darryl","penny","carl","jacky","piper","pam","frank","bibi","bea","nani","edgar","griff","grom","mortis","tara","gene","max","mr. p", "sprout", "byron", "squeak","spike","crow","leon","sandy","amber","meg","gale","surge","colette","lou","colonel ruffs","belle","buzz","ash","lola","fang","eve");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        tag = pref.getString("tag", "default");
        this.getActionBar().hide();
        apiThread = new ApiThread(getApplicationContext(), tag, 1);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        trophySort = findViewById(R.id.trophySort); raritySort = findViewById(R.id.raritySort); brawlers = findViewById(R.id.recyclerView); name = findViewById(R.id.name); highestTrophies = findViewById(R.id.highestTrophies); currentTrophies = findViewById(R.id.currentTrophies);
        try { setValues(pref.getString("response", "")); } catch (JSONException e) { toMainActivity();
            Toast.makeText(getApplicationContext(),"Make sure you input a correct tag!", Toast.LENGTH_SHORT).show(); }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace();  }
        /*try { brawlerList = sortBrawlerListRarity(brawlerList); } catch(ArrayIndexOutOfBoundsException a){
            brawlerList = new ArrayList<>();
            try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace();  }
        }*/
        setBrawlerAdapter();

        trophySort.setOnClickListener(view -> {
            brawlerList = sortBrawlerListTrophy(brawlerList);
            setBrawlerAdapter();
        });
        raritySort.setOnClickListener(view -> {
            brawlerList = sortBrawlerListRarity(brawlerList);
            setBrawlerAdapter();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainActivity();
    }

    @SuppressLint("NewApi")
    private ArrayList<Brawler> sortBrawlerListTrophy(ArrayList<Brawler> inputBrawlerList) throws ArrayIndexOutOfBoundsException {
        inputBrawlerList.sort(Comparator.comparingInt((Brawler brawler) -> brawler.trophies).reversed());
        return inputBrawlerList;
    }

    private ArrayList<Brawler> sortBrawlerListRarity(ArrayList<Brawler> brawlerList) throws ArrayIndexOutOfBoundsException {
        ArrayList<Brawler> tmp = new ArrayList<>();
        if(brawlerList.size() == 0) { return brawlerList; }
        for(int i=0; i <= brawlerList.size()-1; i++) {
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
        BrawlerAdapter adapter = new BrawlerAdapter(brawlerList);
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

    static class BrawlerAdapter extends RecyclerView.Adapter<BrawlerAdapter.ViewHolder> {

        private final ArrayList<Brawler> brawlerList;
        public BrawlerAdapter(ArrayList<Brawler> brawlerList) {   this.brawlerList = brawlerList;  }

        //Class to hold the view for creating the Brawler cards
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView powerLevel;
            private final TextView brawlerTrophies;
            private final TextView highestBrawler;
            private final ImageView brawlerPortrait,rank,trophy,trophyHighest;
            public ViewHolder(View view) {
                super(view);
                powerLevel = view.findViewById(R.id.powerLevel); brawlerTrophies = view.findViewById(R.id.brawlerTrophies); highestBrawler = view.findViewById(R.id.highestBrawler);
                brawlerPortrait = view.findViewById(R.id.brawlerPortrait); rank = view.findViewById(R.id.rank); trophy = view.findViewById(R.id.trophy); trophyHighest = view.findViewById(R.id.trophyHighest);
            }
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

            String filenameString = formatStringForFilename(brawler.name.toLowerCase(Locale.ROOT));
            Context context = holder.brawlerPortrait.getContext();
            int id = context.getResources().getIdentifier(filenameString, "drawable", context.getPackageName());
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