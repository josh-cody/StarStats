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
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class ProfilePage extends AppCompatActivity {

    TextView name, highestTrophies, currentTrophies;
    RecyclerView brawlers;
    private ArrayList<Brawler> brawlerList;
    static volatile String RESPONSE_FROM_API = "Invalid code!";
    HttpURLConnection connection;
    Thread apiThread;
    String tag;
    volatile Boolean flag;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        tag = pref.getString("tag", "");
        System.out.println("tag: "+tag);
        /*if(tag.equals("") || RESPONSE_FROM_API.equals("Invalid code!")) {
            Intent noTag = new Intent(this, MainActivity.class);
            startActivity(noTag);
        }*/
        this.getActionBar().hide();
        brawlers = findViewById(R.id.recyclerView); name = findViewById(R.id.name); highestTrophies = findViewById(R.id.highestTrophies); currentTrophies = findViewById(R.id.currentTrophies);
        apiThread = new ApiThread(tag);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        //if(flag) {Intent errorToHome = new Intent(this, MainActivity.class); startActivity(errorToHome);}
        try { setValues(RESPONSE_FROM_API); } catch (JSONException e) { e.printStackTrace(); }
        brawlerList = new ArrayList<>();
        try { populateBrawlerList(); } catch (JSONException e) { e.printStackTrace();  }
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

    //Thread to make the API call
    class ApiThread extends Thread implements Runnable {
        String tag = getSharedPreferences("def", Context.MODE_PRIVATE).getString("tag", "");
        public ApiThread(String tag) {  this.tag = tag;  }
        //This content is not affiliated with, endorsed, sponsored, or specifically approved by Supercell and Supercell is not responsible for it. For more information see Supercell’s Fan Content Policy: www.supercell.com/fan-content-policy.
        @Override
        public void run() {
            System.out.println("API Thread has started running");
            try {
                //Creating request to API
                String sendingData = "tag="+tag;
                byte[] postTag = sendingData.getBytes(StandardCharsets.UTF_8);
                connection = (HttpURLConnection) new URL("http://140.82.61.171:8080/call").openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(postTag);

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                System.out.println("response: "+ RESPONSE_FROM_API);
                //flag = false;
            } catch (IOException e) {
                Looper.prepare();
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                //flag = true;
            }
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

            //TODO: fix broken brawler pictures
            Context context = holder.brawlerPortrait.getContext();
            int id = context.getResources().getIdentifier(brawler.name.toLowerCase(Locale.ROOT), "drawable", context.getPackageName());
            try{ holder.brawlerPortrait.setImageResource(id); } catch(Error e) { holder.brawlerPortrait.setImageResource(R.drawable.bs_logo); }

            switch (brawler.name.toLowerCase(Locale.ROOT)) {
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