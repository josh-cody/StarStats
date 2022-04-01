package com.example.starstats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BrawlerDescription extends AppCompatActivity {

    private ImageView brawlerPortraitDescriptiom;
    private TextView brawlerName, starpower1, starpower2, description, gadget1, gadget2;
    private String name, starpowers, tmpSP1, tmpSP2, tmpG1, tmpG2, gadgets;
    private int nameID;
    private JSONArray starpowersList, gadgetsList;
    private JSONObject jsonObject;
    private ImageButton imageButton, imageButton2;
    private CardView cardView, cardView2;
    private LinearLayout linearLayout, linearLayout2, linID, linID2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brawler_description);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        nameID = i.getIntExtra("nameID", 0);
        starpowers = i.getStringExtra("starpowers");
        gadgets = i.getStringExtra("gadgets");
        linID2 = findViewById(R.id.linID2); linID = findViewById(R.id.linID); linearLayout2 = findViewById(R.id.linearLayout2); cardView2 = findViewById(R.id.cardView2); imageButton2 = findViewById(R.id.imageButton2); linearLayout = findViewById(R.id.linearLayout); cardView = findViewById(R.id.cardView); imageButton = findViewById(R.id.imageButton); gadget1 = findViewById(R.id.gadget1); gadget2 = findViewById(R.id.gadget2); description =  findViewById(R.id.description); brawlerPortraitDescriptiom = findViewById(R.id.brawlerPortraitDescription); brawlerName = findViewById(R.id.brawlerName); starpower1 = findViewById(R.id.starpower1); starpower2 = findViewById(R.id.starpower2);
        brawlerName.setText(name);
        brawlerPortraitDescriptiom.setImageResource(nameID);
        try { starpowersList = new JSONArray(starpowers); } catch (JSONException e) { e.printStackTrace(); }
        try { gadgetsList = new JSONArray(gadgets); } catch (JSONException e) { e.printStackTrace(); }
        try { setValues(); } catch (JSONException e) { e.printStackTrace(); }

        starpower1.setText(tmpSP1);
        starpower2.setText(tmpSP2);

        gadget1.setText(tmpG1);
        gadget2.setText(tmpG2);

        imageButton.setOnClickListener(view -> {
          if(linearLayout.getVisibility() == View.VISIBLE) {
              TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
              linearLayout.setVisibility(View.GONE);
              imageButton.setImageResource(R.drawable.ic_baseline_expand_more_24);
          }
          else {
              TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
              linearLayout.setVisibility(View.VISIBLE);
              imageButton.setImageResource(R.drawable.ic_baseline_expand_less_24);
          }
        });

        linID.setOnClickListener(view -> {
            if(linearLayout.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                linearLayout.setVisibility(View.GONE);
                imageButton.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                linearLayout.setVisibility(View.VISIBLE);
                imageButton.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });

        imageButton2.setOnClickListener(view -> {
            if(linearLayout2.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                linearLayout2.setVisibility(View.GONE);
                imageButton2.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                linearLayout2.setVisibility(View.VISIBLE);
                imageButton2.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });

        linID2.setOnClickListener(view -> {
            if(linearLayout2.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                linearLayout2.setVisibility(View.GONE);
                imageButton2.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                linearLayout2.setVisibility(View.VISIBLE);
                imageButton2.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });


        description.setText("BRAWLER DESCRIPTION");
    }
    private void setValues() throws JSONException {
        jsonObject = (JSONObject) starpowersList.get(0);
        tmpSP1 = jsonObject.getString("name");
        jsonObject = (JSONObject) starpowersList.get(1);
        tmpSP2 = jsonObject.getString("name");
        jsonObject = (JSONObject) gadgetsList.get(0);
        tmpG1 = jsonObject.getString("name");
        jsonObject = (JSONObject) gadgetsList.get(1);
        tmpG2 = jsonObject.getString("name");
    }
}