package com.example.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class ProfilePage extends AppCompatActivity {

    TextView name, highestTrophies, currentTrophies;
    RecyclerView brawlers;
    FloatingActionButton toSearch;
    private ArrayList<Brawler> brawlerList;
    static volatile String RESPONSE_FROM_API = "Invalid code!";
    HttpsURLConnection connection;
    String tok = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6ImIwNGRhYTFiLTgxMDMtNGE2ZC05NjgxLWYzZGFlMzFlMjA0NSIsImlhdCI6MTYzNTQ2NTkxNCwic3ViIjoiZGV2ZWxvcGVyL2EzZWM3MTk2LTM3NDMtYTIyOS02ZjM2LWVkN2U1NGM1MWZjOCIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiMTczLjE2NS4zMi4yMSJdLCJ0eXBlIjoiY2xpZW50In1dfQ.Hw5ZIXewqQp-IGcC2iUoXap9KACNV656ei2aT0CFN93lews4EmgCEM0KSQhwdMm1jZbUAA5NsDJYCRfUJJij5A";
    String url = "https://api.brawlstars.com/v1/players/";
    Thread apiThread;
    String tag;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        this.getActionBar().hide();
        brawlers = findViewById(R.id.recyclerView); name = findViewById(R.id.name); highestTrophies = findViewById(R.id.highestTrophies); currentTrophies = findViewById(R.id.currentTrophies);
        //toSearch = findViewById(R.id.toSearch);
        Intent intent = getIntent();
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        tag = pref.getString("tag", "");
        apiThread = new ApiThread(tag);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        try { setValues(RESPONSE_FROM_API); } catch (JSONException e) { e.printStackTrace(); }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace();  }
        setBrawlerAdapter();
        /*toSearch.setOnClickListener(view -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });*/
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

    class ApiThread extends Thread implements Runnable {
        String tag;
        public ApiThread(String tag) {  this.tag = tag;  }

        @Override
        public void run() {
            System.out.println("API Thread has started running");
            try {
                connection = (HttpsURLConnection) new URL(url+ "%23" + this.tag).openConnection(); //creating the request
                connection.setRequestMethod("GET");
                connection.addRequestProperty("authorization", tok);
                connection.addRequestProperty("Accept", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(false);
                InputStream is = connection.getInputStream(); //getting inputstream
                RESPONSE_FROM_API = inputStreamToString(is);  //setting volatile variable to the response
            } catch (IOException e) { e.printStackTrace(); }
        }
        private String inputStreamToString(InputStream is) throws IOException {
            InputStreamReader isReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isReader);
            StringBuilder sb = new StringBuilder();
            String str;
            while((str = reader.readLine())!=null)
                sb.append(str);
            return sb.toString();
        }
    }

    class BrawlerAdapter extends RecyclerView.Adapter<BrawlerAdapter.ViewHolder> {

        private ArrayList<Brawler> brawlerList;

        public BrawlerAdapter(ArrayList<Brawler> brawlerList) {   this.brawlerList = brawlerList;  }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView powerLevel,brawlerTrophies,highestBrawler;
            private ImageView brawlerPortrait,rank,trophy,trophyHighest;
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

            //TODO: fix broken file names
            Context context = holder.brawlerPortrait.getContext();
            int id = context.getResources().getIdentifier(brawler.name.toLowerCase(Locale.ROOT), "drawable", context.getPackageName());
            try{holder.brawlerPortrait.setImageResource(id);} catch(Error e) {holder.brawlerPortrait.setImageResource(R.drawable.mortis);}

        }

        @Override
        public int getItemCount() {
            return brawlerList.size();
        }
    }
}