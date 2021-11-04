package com.example.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ProfilePage extends AppCompatActivity {

    TextView name, highestTrophies;
    RecyclerView brawlers;
    FloatingActionButton toSearch;
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
        name = findViewById(R.id.name); highestTrophies = findViewById(R.id.highestTrophies);
        //toSearch = findViewById(R.id.toSearch);
        Intent intent = getIntent();
        SharedPreferences pref = getSharedPreferences("def", Context.MODE_PRIVATE);
        if(intent.getStringExtra("tag")!=null || intent.getStringExtra("tag").equals(""))
            tag = intent.getStringExtra("tag");
        else
            tag = pref.getString("tag", "");
        System.out.println("tag: "+ tag);
        apiThread = new ApiThread(tag);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace();  }
        try { setValues(RESPONSE_FROM_API); } catch (JSONException e) { e.printStackTrace(); }
        /*toSearch.setOnClickListener(view -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });*/

    }

    public void setValues(String RESPONSE_FROM_API) throws JSONException {
        jsonObject = new JSONObject(RESPONSE_FROM_API);
        name.setText(jsonObject.getString("name"));
        name.setTextColor(Color.RED);
        highestTrophies.setText("Highest trophies: " + jsonObject.getString("highestTrophies"));

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

    /*class BrawlerAdapter extends RecyclerView.Adapter<BrawlerAdapter.ViewHolder> {


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public ViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.);
            }

            public TextView getTextView() {
                return textView;
            }
        }

        @NonNull
        @Override
        public BrawlerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
            return new BrawlerAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }*/
}