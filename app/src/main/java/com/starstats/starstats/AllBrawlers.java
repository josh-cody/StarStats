package com.starstats.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class AllBrawlers extends AppCompatActivity {

    private RecyclerView brawlers;
    private ArrayList<Brawler> brawlerList;
    private TextView brawlerDescriptionTitle;
    private ConstraintLayout allBrawlersBack;
    private BrawlerDescriptionFragment brawlerDescriptionFragment;
    private final AtomicBoolean isWindowOpen = new AtomicBoolean(false);
    private JSONArray rarityFromJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brawlers);

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

        AdView mAdView = findViewById(R.id.adViewBrawlers);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        brawlerDescriptionTitle = findViewById(R.id.brawlerDescriptionTitle); brawlers = findViewById(R.id.allBrawlersRecyclerView); allBrawlersBack = findViewById(R.id.allBrawlersBack);
        brawlerDescriptionTitle.setText(R.string.brawlerDescripTitle);
        ApiThread apiThread = new ApiThread(getApplicationContext(), 3);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(pref.getString("brawlerresponse","")); } catch (JSONException e) { e.printStackTrace();  }
        try { brawlerList = sortBrawlerList(brawlerList); } catch (JSONException e) { e.printStackTrace(); }

        setBrawlerAdapter();
        brawlers.setVerticalScrollBarEnabled(false);
        brawlers.setHorizontalScrollBarEnabled(false);
        allBrawlersBack.setOnClickListener(view -> {
            if(isWindowOpen.get()){
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(brawlerDescriptionFragment).commit();
                isWindowOpen.set(false);
            }
        });
        brawlers.setOnClickListener(view -> {
            if(isWindowOpen.get()){
                getSupportFragmentManager().beginTransaction().remove(brawlerDescriptionFragment).commit();
                isWindowOpen.set(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isWindowOpen.get()){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).remove(brawlerDescriptionFragment).commit();
            isWindowOpen.set(false);
        }
        else {
            super.onBackPressed();
            toMainActivity();
        }
    }

    private ArrayList<Brawler> sortBrawlerList(ArrayList<Brawler> brawlerList) throws JSONException {
        ArrayList<Brawler> tmp = new ArrayList<>();
        if(brawlerList.size() == 0) { return brawlerList; }
        for(int i=0; i <= brawlerList.size(); i++) {
            for(int j=0; j <= brawlerList.size()-1; j++) {
                if(brawlerList.get(j).name.toLowerCase().equals(rarityFromJSON.getString(i))) {
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
        AllBrawlers.BrawlersAdapter adapter = new AllBrawlers.BrawlersAdapter(brawlerList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3) {
            @Override
            public boolean canScrollVertically() {
                return !isWindowOpen.get();
            }
        };
        brawlers.setLayoutManager(layoutManager);
        brawlers.setAdapter(adapter);
    }

    private void populateBrawlerList(String RESPONSE_FROM_API) throws JSONException {
        JSONObject jsonObject = new JSONObject(RESPONSE_FROM_API);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for(int i = 0; i <= jsonArray.length(); i++) {
            JSONObject tmpBrawler = (JSONObject) jsonArray.get(i);
            String tmp = formatStringForFilename(tmpBrawler.getString("name").toLowerCase());
            int nameCheck = getApplicationContext().getResources().getIdentifier(tmp, "drawable", getPackageName());
            if(nameCheck != 0x0) {
                brawlerList.add(new Brawler(tmpBrawler.getString("name"), tmpBrawler.get("starPowers").toString(), tmpBrawler.get("gadgets").toString()));
            }
        }
    }

    private String formatStringForFilename(String input) {
        input = input.replaceAll(" ", "");
        input = input.replaceAll("\\.", "");
        input = input.replaceAll("-", "");
        input = input.replaceAll("8", "e");
        return input;
    }

    class BrawlersAdapter extends RecyclerView.Adapter<AllBrawlers.BrawlersAdapter.ViewHolder> {

        private ArrayList<Brawler> brawlerList;
        public BrawlersAdapter(ArrayList<Brawler> brawlerList) {   this.brawlerList = brawlerList;  }
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView brawlerPortrait;
            private final ConstraintLayout layout;
            public ViewHolder(View view) {
                super(view);
                brawlerPortrait = view.findViewById(R.id.brawlerPortrait);
                layout = view.findViewById(R.id.layout);
            }
        }

        @NonNull
        @Override
        public AllBrawlers.BrawlersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.brawler_bare, parent, false);
            return new AllBrawlers.BrawlersAdapter.ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull AllBrawlers.BrawlersAdapter.ViewHolder holder, int position) {
            Brawler brawler = brawlerList.get(position);

            String fileNameString = formatStringForFilename(brawler.name.toLowerCase(Locale.ROOT));
            Context context = holder.brawlerPortrait.getContext();
            int id = context.getResources().getIdentifier(fileNameString, "drawable", context.getPackageName());
            if(id == 0x0) { return; }
            holder.brawlerPortrait.setImageResource(id);
            holder.brawlerPortrait.setOnClickListener(view -> {
                if(!isWindowOpen.get()) {
                    brawlerDescriptionFragment = BrawlerDescriptionFragment.newInstance(id, brawler.name, brawler.jsonStarpowers, brawler.jsonGadgets);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim).replace(R.id.fragmentContainerView2, brawlerDescriptionFragment).commit();
                    isWindowOpen.set(true);
                }
            });
        }

        @Override
        public int getItemCount() { return brawlerList.size(); }
    }
}