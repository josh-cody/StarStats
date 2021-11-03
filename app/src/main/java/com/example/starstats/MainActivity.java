package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements Runnable {

    ConstraintLayout mainLayout;
    FloatingActionButton search;
    TextView title;
    EditText tagInput;
    URLConnection connection;
    String tok = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6ImIwNGRhYTFiLTgxMDMtNGE2ZC05NjgxLWYzZGFlMzFlMjA0NSIsImlhdCI6MTYzNTQ2NTkxNCwic3ViIjoiZGV2ZWxvcGVyL2EzZWM3MTk2LTM3NDMtYTIyOS02ZjM2LWVkN2U1NGM1MWZjOCIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiMTczLjE2NS4zMi4yMSJdLCJ0eXBlIjoiY2xpZW50In1dfQ.Hw5ZIXewqQp-IGcC2iUoXap9KACNV656ei2aT0CFN93lews4EmgCEM0KSQhwdMm1jZbUAA5NsDJYCRfUJJij5A";
    String url = "https://api.brawlstars.com/v1/players/%23";
    Thread apiThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.enterID); search = findViewById(R.id.search); title = findViewById(R.id.starTitle); tagInput = findViewById(R.id.tagInput);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Implement API calls", Snackbar.LENGTH_LONG).show();
                MainActivity m = new MainActivity();
                apiThread = new Thread(m);
                apiThread.start();
            }
        });
    }

    @Override
    public void run() {
        System.out.println("API Thread has started running");
                String tag = tagInput.getText().toString();
                try {
                    connection = new URL(url+tag).openConnection();
                    connection.setRequestProperty("authorization", tok);
                    //InputStream is = connection.getInputStream();
                    System.out.println(connection.getContent());
                } catch (IOException e) {
                    System.out.println("Connection did not work");
                    e.printStackTrace();
                }
    }
}