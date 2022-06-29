package com.starstats.starstats;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class BrawlerDescriptionFragment extends Fragment {

    private static final String ARG_PARAM1 = "nameID";
    private static final String ARG_PARAM2 = "name";
    private static final String ARG_PARAM3 = "starpowers";
    private static final String ARG_PARAM4 = "gadgets";

    private ImageButton closeDesc;
    private ImageView brawlerPortraitDescriptiom, sp1image_desc, sp2image_desc, g1image_desc, g2image_desc;
    private TextView brawlerName, starpower1, starpower2, description, gadget1, gadget2, starpower1desc, starpower2desc, gadget1desc, gadget2desc;
    private String name, starpowers, tmpSP1, tmpSP2, tmpG1, tmpG2, gadgets;
    private int nameID;
    private JSONArray starpowersList, gadgetsList;
    private JSONObject jsonObject, starpowerdescrip, gadgetdescrip, descriptions;
    private ImageButton starpowerDropdown, gadgetDropdown;
    private CardView cardView, cardView2;
    private LinearLayout linearLayout, linearLayout2, linID, linID2;


    public BrawlerDescriptionFragment() {}

    public static BrawlerDescriptionFragment newInstance(int nameID, String name, String starpowers, String gadgets) {
        BrawlerDescriptionFragment fragment = new BrawlerDescriptionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, nameID);
        args.putString(ARG_PARAM2, name);
        args.putString(ARG_PARAM3, starpowers);
        args.putString(ARG_PARAM4, gadgets);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_brawler_description, container, false);
        g2image_desc = v.findViewById(R.id.g2image_desc); g1image_desc = v.findViewById(R.id.g1image_desc); sp2image_desc = v.findViewById(R.id.sp2image_desc); sp1image_desc = v.findViewById(R.id.sp1image_desc); closeDesc = v.findViewById(R.id.closeDesc); starpower1desc = v.findViewById(R.id.starpower1desc); starpower2desc = v.findViewById(R.id.starpower2desc); gadget1desc = v.findViewById(R.id.gadget1desc); gadget2desc = v.findViewById(R.id.gadget2desc); linID2 = v.findViewById(R.id.linID2); linID = v.findViewById(R.id.linID); linearLayout2 = v.findViewById(R.id.linearLayout2); cardView2 = v.findViewById(R.id.cardView2); gadgetDropdown = v.findViewById(R.id.gadgetDropdown); linearLayout = v.findViewById(R.id.linearLayout); cardView = v.findViewById(R.id.cardView); starpowerDropdown = v.findViewById(R.id.starpowerDropdown); gadget1 = v.findViewById(R.id.gadget1); gadget2 = v.findViewById(R.id.gadget2); description =  v.findViewById(R.id.description); brawlerPortraitDescriptiom = v.findViewById(R.id.brawlerPortraitDescription); brawlerName = v.findViewById(R.id.brawlerName); starpower1 = v.findViewById(R.id.starpower1); starpower2 = v.findViewById(R.id.starpower2);
        assert getArguments() != null;
        nameID = getArguments().getInt(ARG_PARAM1);
        name = getArguments().getString(ARG_PARAM2);
        starpowers = getArguments().getString(ARG_PARAM3);
        gadgets = getArguments().getString(ARG_PARAM4);

        try {
            InputStream is = requireContext().getAssets().open("starpowers.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            starpowerdescrip = new JSONObject(jsonString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        try {
            InputStream is = requireContext().getAssets().open("gadgets.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            gadgetdescrip = new JSONObject(jsonString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = requireContext().getAssets().open("descriptions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            descriptions = new JSONObject(jsonString);
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        }

        brawlerPortraitDescriptiom.setImageResource(nameID);
        brawlerName.setText(name);
        try { starpowersList = new JSONArray(starpowers); } catch (JSONException e) { e.printStackTrace(); }
        try { gadgetsList = new JSONArray(gadgets); } catch (JSONException e) { e.printStackTrace(); }
        setValues();

        starpower1.setText(tmpSP1);
        try { starpower1desc.setText(starpowerdescrip.getString(tmpSP1)); } catch (JSONException e) { starpower1desc.setText(R.string.coming_soon); }
        starpower2.setText(tmpSP2);
        try { starpower2desc.setText(starpowerdescrip.getString(tmpSP2)); } catch (JSONException e) { starpower2desc.setText(R.string.coming_soon); }

        gadget1.setText(tmpG1);
        try { gadget1desc.setText(gadgetdescrip.getString(tmpG1)); } catch (JSONException e) { gadget1desc.setText(R.string.coming_soon); }
        gadget2.setText(tmpG2);
        try { gadget2desc.setText(gadgetdescrip.getString(tmpG2)); } catch (JSONException e) { gadget2desc.setText(R.string.coming_soon); }


        starpowerDropdown.setOnClickListener(view -> {
            if(linearLayout.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                linearLayout.setVisibility(View.GONE);
                starpowerDropdown.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                linearLayout.setVisibility(View.VISIBLE);
                starpowerDropdown.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });

        linID.setOnClickListener(view -> {
            if(linearLayout.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                linearLayout.setVisibility(View.GONE);
                starpowerDropdown.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                linearLayout.setVisibility(View.VISIBLE);
                starpowerDropdown.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });

        gadgetDropdown.setOnClickListener(view -> {
            if(linearLayout2.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                linearLayout2.setVisibility(View.GONE);
                gadgetDropdown.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                linearLayout2.setVisibility(View.VISIBLE);
                gadgetDropdown.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });

        linID2.setOnClickListener(view -> {
            if(linearLayout2.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                linearLayout2.setVisibility(View.GONE);
                gadgetDropdown.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView2, new AutoTransition());
                linearLayout2.setVisibility(View.VISIBLE);
                gadgetDropdown.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });

        closeDesc.setOnClickListener(view -> {
            requireActivity().onBackPressed();
        });
        brawlerName.setOnClickListener(view -> {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            brawlerName.setTextColor(color);
        });
        brawlerName.setOnLongClickListener(view -> {
            brawlerName.setTextColor(Color.argb(255, 255, 255, 255));
            return true;
        });
        return v;
    }

    private void setValues() { //Try catch instead of exception thrown so that the rest of the values will still be populated
        try {
            jsonObject = (JSONObject) starpowersList.get(0);
            tmpSP1 = jsonObject.getString("name");
        } catch (JSONException e) { e.printStackTrace(); starpower1.setVisibility(View.GONE); starpower1desc.setVisibility(View.GONE);}
        try {
            jsonObject = (JSONObject) starpowersList.get(1);
            tmpSP2 = jsonObject.getString("name");
        } catch (JSONException e) { e.printStackTrace(); starpower2.setVisibility(View.GONE); starpower2desc.setVisibility(View.GONE);}
        try {
            jsonObject = (JSONObject) gadgetsList.get(0);
            tmpG1 = jsonObject.getString("name");
        } catch (JSONException e) { e.printStackTrace(); gadget1.setVisibility(View.GONE); gadget1desc.setVisibility(View.GONE); }
        try {
            jsonObject = (JSONObject) gadgetsList.get(1);
            tmpG2 = jsonObject.getString("name");
        } catch (JSONException e) { e.printStackTrace(); gadget2.setVisibility(View.GONE); gadget2desc.setVisibility(View.GONE); }
        try {
            description.setText((CharSequence) descriptions.get(name.toLowerCase()));
        } catch (JSONException e) { e.printStackTrace(); description.setVisibility(View.GONE); }

        String filenameString = "";
        int id = 0;

        try {filenameString = formatStringForFilename(tmpSP1.toLowerCase());
        id = requireContext().getResources().getIdentifier(filenameString, "drawable", requireContext().getPackageName());
        sp1image_desc.setImageResource(id); } catch (Exception ignored) {}

        try {filenameString = formatStringForFilename(tmpSP2.toLowerCase());
        id = requireContext().getResources().getIdentifier(filenameString, "drawable", requireContext().getPackageName());
        sp2image_desc.setImageResource(id);} catch (Exception ignored) {}

        try {filenameString = formatStringForFilename(tmpG1.toLowerCase());
        id = requireContext().getResources().getIdentifier(filenameString, "drawable", requireContext().getPackageName());
        g1image_desc.setImageResource(id);} catch (Exception ignored) {}

        try {filenameString = formatStringForFilename(tmpG2.toLowerCase());
        id = requireContext().getResources().getIdentifier(filenameString, "drawable", requireContext().getPackageName());
        g2image_desc.setImageResource(id);} catch (Exception ignored) {}
    }
    private String formatStringForFilename(String input) {
        input = input.replaceAll(" ", "");
        input = input.replaceAll("\\.", "");
        input = input.replaceAll("-", "");
        input = input.replaceAll("!", "");
        input = input.replaceAll(":", "");
        input = input.replaceAll("4", "four");
        input = input.replaceAll("'", "");

        return input;
    }
}