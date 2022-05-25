package com.example.starstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//Thread to make the API call
class ApiThread extends Thread implements Runnable {
    String tag;
    int req;
    HttpURLConnection connection;
    static String RESPONSE_FROM_API = "Invalid code!";
    Context context;

    public ApiThread(Context context, String tag, int req) {  this.context = context; this.tag = tag; this.req = req;  }
    public ApiThread(Context context, int req) {  this.context = context;  this.req = req; }

    @Override
    public void run() {
        SharedPreferences pref = context.getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        System.out.println("Network Thread has started running");
        if(this.req == 1) {
            //create request for player data
            try {
                URL server = new URL("http://192.168.1.51:5000/player"); //NOT RUNNING ON SERVER, NEEDS DEPLOYED. APP MUST BE USED ON SAME WIFI NETWORK.
                connection = (HttpURLConnection) server.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postTag = URLEncoder.encode(this.tag, "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(postTag);
                wr.flush();

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                System.out.println(RESPONSE_FROM_API);
                edit.putString("response", RESPONSE_FROM_API).apply();
            } catch (IOException e) {
                Looper.prepare();
                e.printStackTrace();
                Toast.makeText(context.getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            }
        }
        else if(this.req == 2) {
            //Create request for map data
            try {
                URL server = new URL("http://192.168.1.51:5000/maps");
                connection = (HttpURLConnection) server.openConnection();
                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                edit.putString("mapresponse", RESPONSE_FROM_API).apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (this.req == 3) {
            try{
                URL server = new URL("http://192.168.1.51:5000/brawlers");
                connection = (HttpURLConnection) server.openConnection();
                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                edit.putString("brawlerresponse", RESPONSE_FROM_API).apply();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        else if (this.req == 4) {
            try{
                URL server = new URL("http://192.168.1.51:5000/battles");
                connection = (HttpURLConnection) server.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postTag = URLEncoder.encode(this.tag, "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(postTag);
                wr.flush();

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                edit.putString("battleresponse", RESPONSE_FROM_API).apply();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        else if(this.req == 5) {
            try {
                URL server = new URL("http://192.168.1.51:5000/widget");
                connection = (HttpURLConnection) server.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postTag = URLEncoder.encode(this.tag, "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(postTag);
                wr.flush();

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                edit.putString("widgetresponse", RESPONSE_FROM_API).apply();
            } catch (IOException e) {
                Looper.prepare();
                e.printStackTrace();
                Toast.makeText(context.getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            }
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