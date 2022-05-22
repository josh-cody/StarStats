package com.example.starstats;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 */
public class BrawlerWidget extends AppWidgetProvider {



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences pref = context.getSharedPreferences("def", Context.MODE_PRIVATE);
        if(pref.getInt("widgetID", -1) != appWidgetId) {
            return;
        }
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.brawler_widget);

        ApiThread apiThread = new ApiThread(context, pref.getString("widgetPlayerTag","NONE"), 5);
        apiThread.start();
        try { apiThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        String RESPONSE_FROM_API = pref.getString("widgetresponse", "");

        try {
            JSONObject jsonObject = new JSONObject(RESPONSE_FROM_API);
            views.setTextViewText(R.id.playerNameWidget, jsonObject.getString("name"));
            views.setTextViewText(R.id.brawlerTrophiesWidget, jsonObject.getString("trophies"));
            views.setImageViewResource(R.id.brawlerImageWidget, R.mipmap.star_icon_background);
        } catch (JSONException e) {
            views.setTextViewText(R.id.playerNameWidget, "NAME");
            views.setTextViewText(R.id.brawlerTrophiesWidget, "TROPH");
            views.setImageViewResource(R.id.brawlerImageWidget, R.mipmap.star_icon_background);
        }

        if(pref.getString("widgetPlayerTag","NONE").equals("NONE")) {
            views.setTextViewText(R.id.brawlerTrophiesWidget, "NO TAG");
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = context.getSharedPreferences("def", Context.MODE_PRIVATE);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.brawler_widget);

        if(intent.getExtras() != null) {
            String RESPONSE_FROM_API = pref.getString("widgetresponse", "");
            try {
                JSONObject jsonObject = new JSONObject(RESPONSE_FROM_API);
                views.setTextViewText(R.id.playerNameWidget, jsonObject.getString("name"));
                views.setTextViewText(R.id.brawlerTrophiesWidget, jsonObject.getString("trophies"));
                views.setImageViewResource(R.id.brawlerImageWidget, R.mipmap.star_icon_background);
            } catch (JSONException e) {
                views.setTextViewText(R.id.playerNameWidget, "NAME");
                views.setTextViewText(R.id.brawlerTrophiesWidget, "TROPH");
                views.setImageViewResource(R.id.brawlerImageWidget, R.mipmap.star_icon_background);
            }
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,-1), views);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update only the first one to keep calls to the API down
        SharedPreferences pref = context.getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        if(pref.getInt("widgetID",-1) == -1) {
            edit.putInt("widgetID", appWidgetIds[0]).apply();
        }


        updateAppWidget(context, appWidgetManager, appWidgetIds[0]);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        SharedPreferences pref = context.getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("widgetID", -1).apply();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        SharedPreferences pref = context.getSharedPreferences("def", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("widgetID", -1).apply();
    }
}