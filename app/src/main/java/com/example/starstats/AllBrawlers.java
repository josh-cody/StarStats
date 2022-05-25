package com.example.starstats;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class AllBrawlers extends AppCompatActivity {

    private RecyclerView brawlers;
    private ArrayList<Brawler> brawlerList;
    private TextView brawlerDescriptionTitle;
    private ConstraintLayout allBrawlersBack;
    private BrawlerDescriptionFragment brawlerDescriptionFragment;
    private AtomicBoolean isWindowOpen = new AtomicBoolean(false);
    private List<String> rarityOrder = Arrays.asList("shelly","nita","colt","bull","jessie","brock","dynamike","bo","tick","8-bit","emz","stu","el primo","barley","poco","rosa","rico","darryl","penny","carl","jacky","piper","pam","frank","bibi","bea","nani","edgar","griff","grom","mortis","tara","gene","max","mr. p", "sprout", "byron", "squeak","spike","crow","leon","sandy","amber","meg","gale","surge","colette","lou","colonel ruffs","belle","buzz","ash","lola","fang","eve", "janet");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brawlers);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        brawlerDescriptionTitle = findViewById(R.id.brawlerDescriptionTitle); brawlers = findViewById(R.id.allBrawlersRecyclerView); allBrawlersBack = findViewById(R.id.allBrawlersBack);
        brawlerDescriptionTitle.setText("Tap on a brawler to learn more!");
        ApiThread apiThread = new ApiThread(getApplicationContext(), 3);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(pref.getString("brawlerresponse","")); } catch (JSONException e) { e.printStackTrace();  }

        brawlerList = sortBrawlerList(brawlerList);
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

    private ArrayList<Brawler> sortBrawlerList(ArrayList<Brawler> brawlerList) throws ArrayIndexOutOfBoundsException {
        ArrayList<Brawler> tmp = new ArrayList<>();
        if(brawlerList.size() == 0) { return brawlerList; }
        for(int i=0; i <= brawlerList.size()-1; i++) {
            for(int j=0; j <= brawlerList.size()-1; j++) {
                if(brawlerList.get(j).name.toLowerCase().equals(rarityOrder.get(i))) {
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
        for(int i = 0; i < jsonArray.length(); i++) {
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
        public int getItemCount() {
            return brawlerList.size();
        }
    }
}