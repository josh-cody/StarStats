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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class AllBrawlers extends AppCompatActivity {

    private RecyclerView brawlers;
    private ArrayList<Brawler> brawlerList;
    private TextView brawlerDescriptionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brawlers);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        brawlerDescriptionTitle = findViewById(R.id.brawlerDescriptionTitle); brawlers = findViewById(R.id.allBrawlersRecyclerView);
        brawlerDescriptionTitle.setText("Tap on a brawler to learn more!");
        ApiThread apiThread = new ApiThread(getApplicationContext(), 3);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(pref.getString("brawlerresponse","")); } catch (JSONException e) { e.printStackTrace();  }
        setBrawlerAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainActivity();
    }

    private void toMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
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
            brawlerList.add(new Brawler(tmpBrawler.getString("name"), tmpBrawler.get("starPowers").toString(), tmpBrawler.get("gadgets").toString()));
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


        private void goToDescription(Context context, int nameID, String name, String starpowers, String gadgets) {
            Intent i = new Intent(context, BrawlerDescription.class);
            i.putExtra("nameID", nameID);
            i.putExtra("starpowers", starpowers);
            i.putExtra("gadgets", gadgets);
            i.putExtra("name", name);
            startActivity(i);
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull AllBrawlers.BrawlersAdapter.ViewHolder holder, int position) {
            Brawler brawler = brawlerList.get(position);

            String fileNameString = formatStringForFilename(brawler.name.toLowerCase(Locale.ROOT));
            Context context = holder.brawlerPortrait.getContext();
            int id = context.getResources().getIdentifier(fileNameString, "drawable", context.getPackageName());
            try{ holder.brawlerPortrait.setImageResource(id); } catch(Error e) { holder.brawlerPortrait.setImageResource(R.drawable.bs_logo); }

            holder.brawlerPortrait.setOnClickListener(view -> {
                goToDescription(holder.brawlerPortrait.getContext(), id, brawler.name, brawler.jsonStarpowers, brawler.jsonGadgets);
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
        public int getItemCount() {
            return brawlerList.size();
        }
    }
}