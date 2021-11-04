package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
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

    TextView name;
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
        name = findViewById(R.id.name);
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
        try {
            jsonObject = new JSONObject(RESPONSE_FROM_API);
            name.setText(jsonObject.getString("name"));
        } catch (JSONException e) { e.printStackTrace(); }
        /*toSearch.setOnClickListener(view -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });*/
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
}