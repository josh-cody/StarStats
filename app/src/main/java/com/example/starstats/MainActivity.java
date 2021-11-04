package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements Runnable {


    public MainActivity() {}
    public MainActivity(String tag) {this.tag = tag;}

    ConstraintLayout mainLayout;
    Handler handler;
    String tag;
    FloatingActionButton search;
    TextView title, stats;
    EditText tagInput;
    HttpsURLConnection connection;
    String tok = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6ImIwNGRhYTFiLTgxMDMtNGE2ZC05NjgxLWYzZGFlMzFlMjA0NSIsImlhdCI6MTYzNTQ2NTkxNCwic3ViIjoiZGV2ZWxvcGVyL2EzZWM3MTk2LTM3NDMtYTIyOS02ZjM2LWVkN2U1NGM1MWZjOCIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiMTczLjE2NS4zMi4yMSJdLCJ0eXBlIjoiY2xpZW50In1dfQ.Hw5ZIXewqQp-IGcC2iUoXap9KACNV656ei2aT0CFN93lews4EmgCEM0KSQhwdMm1jZbUAA5NsDJYCRfUJJij5A";
    String url = "https://api.brawlstars.com/v1/players/";
    Thread apiThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.enterID); search = findViewById(R.id.search); title = findViewById(R.id.starTitle); tagInput = findViewById(R.id.tagInput); stats = findViewById(R.id.stats);
        handler = new Handler();
        search.setOnClickListener(view -> {
            tag = getTag();
            MainActivity m = new MainActivity(tag);
            apiThread = new Thread(m);
            apiThread.start();
        });
    }
    public String getTag() {  return tagInput.getText().toString();  }

    @Override
    public void run() {
        System.out.println("API Thread has started running");
        try {
                connection = (HttpsURLConnection) new URL(url+ "%23" + this.tag).openConnection();
                connection.setRequestMethod("GET");
                connection.addRequestProperty("authorization", tok);
                connection.addRequestProperty("Accept", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(false);
                InputStream is = connection.getInputStream();
                System.out.println(inputStreamToString(is));
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