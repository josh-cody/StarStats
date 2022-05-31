package com.example.starstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
        String e2 = "";
        try { e2 = URLEncoder.encode("", "UTF-8"); } catch (UnsupportedEncodingException e) { e.printStackTrace(); }

        String ipad = "http://192.168.1.53:5000/";

        System.out.println("Network Thread has started running");
        if(this.req == 1) {
            //create request for player data
            try {
                URL server = new URL(ipad+"player");
                connection = (HttpURLConnection) server.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postTag = URLEncoder.encode(this.tag, "UTF-8");
                String[] postData = {postTag, e2};
                JSONArray jsonArray = new JSONArray(postData);

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(URLEncoder.encode(jsonArray.toString(), "UTF-8"));
                wr.flush();

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                System.out.println(RESPONSE_FROM_API);
                edit.putString("response", RESPONSE_FROM_API).apply();
            } catch (IOException | JSONException e) {
                Looper.prepare();
                e.printStackTrace();
                Toast.makeText(context.getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            }
        }
        else if(this.req == 2) {
            //Create request for map data
            try {
                URL server = new URL(ipad+"maps");
                connection = (HttpURLConnection) server.openConnection();
                connection.setDoOutput(true);

                String[] postData = {e2};
                JSONArray jsonArray = new JSONArray(postData);


                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(URLEncoder.encode(jsonArray.toString(), "UTF-8"));
                wr.flush();

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                edit.putString("mapresponse", RESPONSE_FROM_API).apply();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        else if (this.req == 3) {
            //create request for brawler data
            try{
                URL server = new URL(ipad+"brawlers");
                connection = (HttpURLConnection) server.openConnection();
                connection.setDoOutput(true);

                String[] postData = {e2};
                JSONArray jsonArray = new JSONArray(postData);


                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(URLEncoder.encode(jsonArray.toString(), "UTF-8"));
                wr.flush();

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                edit.putString("brawlerresponse", RESPONSE_FROM_API).apply();
            }
            catch(IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        else if (this.req == 4) {
            //create request for battle log data
            try{
                URL server = new URL(ipad+"battles");
                connection = (HttpURLConnection) server.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postTag = URLEncoder.encode(this.tag, "UTF-8");
                String[] postData = {postTag, e2};
                JSONArray jsonArray = new JSONArray(postData);
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(URLEncoder.encode(jsonArray.toString(), "UTF-8"));
                wr.flush();

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                edit.putString("battleresponse", RESPONSE_FROM_API).apply();
            } catch(IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        else if(this.req == 5) {
            //create request for widget data
            try {
                URL server = new URL(ipad+"widget");
                connection = (HttpURLConnection) server.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postTag = URLEncoder.encode(this.tag, "UTF-8");
                String[] postData = {postTag, e2};
                JSONArray jsonArray = new JSONArray(postData);
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(URLEncoder.encode(jsonArray.toString(), "UTF-8"));
                wr.flush();

                InputStream is = connection.getInputStream();
                RESPONSE_FROM_API = inputStreamToString(is);
                edit.putString("widgetresponse", RESPONSE_FROM_API).apply();
            } catch (IOException | JSONException e) {
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