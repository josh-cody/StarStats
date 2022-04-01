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

public class AllBrawlers extends AppCompatActivity {

    private RecyclerView brawlers;
    private ArrayList<Brawler> brawlerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brawlers);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        brawlers = findViewById(R.id.allBrawlersRecyclerView);
        ApiThread apiThread = new ApiThread(getApplicationContext(), 3);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(pref.getString("brawlerresponse","")); } catch (JSONException e) { e.printStackTrace();  }
        setBrawlerAdapter();
    }

    private void setBrawlerAdapter() {
        AllBrawlers.BrawlersAdapter adapter = new AllBrawlers.BrawlersAdapter(brawlerList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        brawlers.setLayoutManager(layoutManager);
        brawlers.setAdapter(adapter);
    }

    private void populateBrawlerList(String RESPONSE_FROM_API) throws JSONException {
        JSONObject jsonObject = new JSONObject(RESPONSE_FROM_API);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject tmpBrawler = (JSONObject) jsonArray.get(i);
            brawlerList.add(new Brawler(tmpBrawler.getString("name")));
        }
    }

    class BrawlersAdapter extends RecyclerView.Adapter<AllBrawlers.BrawlersAdapter.ViewHolder> {

        private ArrayList<Brawler> brawlerList;
        public BrawlersAdapter(ArrayList<Brawler> brawlerList) {   this.brawlerList = brawlerList;  }

        //Class to hold the view for creating the Brawler cards
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView brawlerPortrait;
            public ViewHolder(View view) {
                super(view);
                brawlerPortrait = view.findViewById(R.id.brawlerPortrait);
            }
        }

        @NonNull
        @Override
        public AllBrawlers.BrawlersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.brawler_bare, parent, false);
            return new AllBrawlers.BrawlersAdapter.ViewHolder(v);
        }


        private void goToDescription(Context context, String name) {
            Intent i = new Intent(context, BrawlerDescription.class);
            i.putExtra("name", name);
            startActivity(i);
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull AllBrawlers.BrawlersAdapter.ViewHolder holder, int position) {
            Brawler brawler = brawlerList.get(position);




            holder.brawlerPortrait.setOnClickListener(view -> {
                goToDescription(holder.brawlerPortrait.getContext(), brawler.name);
            });


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