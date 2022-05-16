package com.example.starstats;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class BrawlerWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.brawler_widget);

        SharedPreferences pref = context.getSharedPreferences("def", Context.MODE_PRIVATE);

        ApiThread apiThread = new ApiThread(context, pref.getString("widgetPlayerTag","LQL"), 5);
        apiThread.start();
        String RESPONSE_FROM_API = pref.getString("widgetresponse", "");

        /*HashMap<Integer, String> idToNameMap = new HashMap<>();
        idToNameMap.put(28000087, "mortis");
        int id = context.getResources().getIdentifier(idToNameMap.get(jsonObject.getJSONObject("icon").getInt("id")),"drawable", context.getPackageName());*/




        try {
            JSONObject jsonObject = new JSONObject(RESPONSE_FROM_API);
            views.setTextViewText(R.id.playerNameWidget, jsonObject.getString("name"));
            views.setTextViewText(R.id.brawlerTrophiesWidget, jsonObject.getString("trophies"));
            views.setImageViewResource(R.id.brawlerImageWidget, R.mipmap.star_icon_background);
        } catch (JSONException e) {
            views.setTextViewText(R.id.playerNameWidget, "NAME");
            views.setTextViewText(R.id.brawlerTrophiesWidget, "TROPH");
            views.setImageViewResource(R.id.brawlerImageWidget, R.drawable.shelly);;
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them


        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        /*RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.brawler_widget);

        SharedPreferences pref = context.getSharedPreferences("def", Context.MODE_PRIVATE);

        ApiThread apiThread = new ApiThread(context, pref.getString("widgetPlayerTag","LQL"), 5);
        apiThread.start();
        String RESPONSE_FROM_API = pref.getString("widgetresponse", "");

        try {
            JSONObject jsonObject = new JSONObject(RESPONSE_FROM_API);
            views.setTextViewText(R.id.playerNameWidget, jsonObject.getString("name"));
            views.setTextViewText(R.id.brawlerTrophiesWidget, "TROPH");
            views.setTextViewText(R.id.brawlerTrophyChangeWidget, "CHANGE");
            views.setImageViewResource(R.id.brawlerImageWidget, R.drawable.shelly);
        } catch (JSONException e) {
            views.setTextViewText(R.id.playerNameWidget, "NAME");
            views.setTextViewText(R.id.brawlerTrophiesWidget, "TROPH");
            views.setTextViewText(R.id.brawlerTrophyChangeWidget, "CHANGE");
            views.setImageViewResource(R.id.brawlerImageWidget, R.drawable.shelly);;
        }*/

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}