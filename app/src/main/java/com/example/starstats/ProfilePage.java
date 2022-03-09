package com.example.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ProfilePage extends AppCompatActivity {

    TextView name, highestTrophies, currentTrophies;
    RecyclerView brawlers;
    private ArrayList<Brawler> brawlerList;
    ApiThread apiThread;
    String tag;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        tag = pref.getString("tag", "default");
        this.getActionBar().hide();
        brawlers = findViewById(R.id.recyclerView); name = findViewById(R.id.name); highestTrophies = findViewById(R.id.highestTrophies); currentTrophies = findViewById(R.id.currentTrophies);
        apiThread = new ApiThread(getApplicationContext(), tag, 1);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }

        try { setValues(pref.getString("response", "")); } catch (JSONException e) { e.printStackTrace(); }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace();  }
        for(Brawler b: brawlerList) { System.out.println("name: "+b.name);}
        setBrawlerAdapter();
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
        for(int i = 0; i < jsonArray.length() - 1; i++) {
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

        private ArrayList<Brawler> brawlerList;
        public BrawlerAdapter(ArrayList<Brawler> brawlerList) {   this.brawlerList = brawlerList;  }

        //Class to hold the view for creating the Brawler cards
        public class ViewHolder extends RecyclerView.ViewHolder {
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
            holder.powerLevel.setText(new StringBuilder().append(brawler.name).append(" ").append(Integer.toString(brawler.powerLevel)).toString());

            holder.trophyHighest.setImageResource(R.drawable.trophy);
            holder.trophy.setImageResource(R.drawable.trophy);

            Context context1 = holder.rank.getContext();
            String temp = "rank"+brawler.rank;
            int id1 = context1.getResources().getIdentifier(temp, "drawable", context1.getPackageName());

            holder.rank.setImageResource(id1);

            Context context = holder.brawlerPortrait.getContext();
            int id = context.getResources().getIdentifier(brawler.name.toLowerCase(Locale.ROOT), "drawable", context.getPackageName());
            try{ holder.brawlerPortrait.setImageResource(id); } catch(Error e) { holder.brawlerPortrait.setImageResource(R.drawable.bs_logo); }

            switch (brawler.name.toLowerCase(Locale.ROOT)) { //TODO: REMOVE SPECIAL CASES BY FORMATTING STRINGS
                case "el primo":
                    holder.brawlerPortrait.setImageResource(R.drawable.primo);
                    break;
                case "8-bit":
                    holder.brawlerPortrait.setImageResource(R.drawable.ebit);
                    break;
                case "mr. p":
                    holder.brawlerPortrait.setImageResource(R.drawable.mrp);
                    break;
                case "colonel ruffs":
                    holder.brawlerPortrait.setImageResource(R.drawable.colonelruffs);
                    break;
            }
        }
        @Override
        public int getItemCount() {
            return brawlerList.size();
        }
    }
}