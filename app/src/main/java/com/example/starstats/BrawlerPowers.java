package com.example.starstats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrawlerPowers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrawlerPowers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "starpower1";
    private static final String ARG_PARAM2 = "starpower2";
    private static final String ARG_PARAM3 = "gadget1";
    private static final String ARG_PARAM4 = "gadget2";
    private static final String ARG_PARAM5 = "speedgear";
    private static final String ARG_PARAM6 = "healthgear";
    private static final String ARG_PARAM7 = "damagegear";
    private static final String ARG_PARAM8 = "visiongear";
    private static final String ARG_PARAM9 = "shieldgear";

    // TODO: Rename and change types of parameters
    private String starpower1, starpower2, gadget1, gadget2;
    private int speedgear, healthgear, damagegear, visiongear, shieldgear;

    public BrawlerPowers() {
        // Required empty public constructor
    }

    private ImageView profileStarpower1, profileStarpower2, profileGadget1, profileGadget2, profileSpeedgear, profileHealthgear, profileDamagegear, profileVisiongear, profileShieldgear;

    // TODO: Rename and change types and number of parameters
    public static BrawlerPowers newInstance(String starpower1, String starpower2, String gadget1, String gadget2, int speedgear, int healthgear, int damagegear, int visiongear, int shieldgear) {
        BrawlerPowers fragment = new BrawlerPowers();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, starpower1);
        args.putString(ARG_PARAM2, starpower2);
        args.putString(ARG_PARAM3, gadget1);
        args.putString(ARG_PARAM4, gadget2);
        args.putInt(ARG_PARAM5, speedgear);
        args.putInt(ARG_PARAM6, healthgear);
        args.putInt(ARG_PARAM7, damagegear);
        args.putInt(ARG_PARAM8, visiongear);
        args.putInt(ARG_PARAM9, shieldgear);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_brawler_powers, container, false);

        profileStarpower1 = v.findViewById(R.id.profileStarpower1); profileStarpower2 = v.findViewById(R.id.profileStarpower2); profileGadget1 = v.findViewById(R.id.profileGadget1); profileGadget2 = v.findViewById(R.id.profileGadget2); profileSpeedgear = v.findViewById(R.id.speedgear); profileHealthgear = v.findViewById(R.id.healthgear); profileDamagegear = v.findViewById(R.id.damagegear); profileVisiongear = v.findViewById(R.id.visiongear); profileShieldgear = v.findViewById(R.id.shieldgear);

        if (getArguments() != null) {
            starpower1 = getArguments().getString(ARG_PARAM1);
            starpower2 = getArguments().getString(ARG_PARAM2);
            gadget1 = getArguments().getString(ARG_PARAM3);
            gadget2 = getArguments().getString(ARG_PARAM4);
            speedgear = getArguments().getInt(ARG_PARAM5);
            healthgear = getArguments().getInt(ARG_PARAM6);
            damagegear = getArguments().getInt(ARG_PARAM7);
            visiongear = getArguments().getInt(ARG_PARAM8);
            shieldgear = getArguments().getInt(ARG_PARAM9);
        }

        if(!Objects.equals(starpower1, "locked")) {
            String filenameString = formatStringForFilename(starpower1.toLowerCase());
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileStarpower1.setImageResource(id);
        }
        else {
            profileStarpower1.setImageResource(R.drawable.blankstarpower);
        }
        if(!Objects.equals(starpower2, "locked")) {
            String filenameString = formatStringForFilename(starpower2.toLowerCase());
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileStarpower2.setImageResource(id);
        }
        else {
            profileStarpower2.setImageResource(R.drawable.blankstarpower);
        }
        if(!Objects.equals(gadget1, "locked")) {
            String filenameString = formatStringForFilename(gadget1.toLowerCase());
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileGadget1.setImageResource(id);
        }
        else {
            profileGadget1.setImageResource(R.drawable.gadgetblank);
        }
        if(!Objects.equals(gadget2, "locked")) {
            String filenameString = formatStringForFilename(gadget2.toLowerCase());
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileGadget2.setImageResource(id);
        }
        else {
            profileGadget1.setImageResource(R.drawable.gadgetblank);
        }
        if(speedgear != 0) {
            String filenameString = String.format("speed%s", speedgear);
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileSpeedgear.setImageResource(id);
        }
        if(healthgear != 0) {
            String filenameString = String.format("health%s", healthgear);
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileHealthgear.setImageResource(id);
        }
        if(damagegear != 0) {
            String filenameString = String.format("damage%s", damagegear);
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileDamagegear.setImageResource(id);
        }
        if(visiongear != 0) {
            String filenameString = String.format("vision%s", visiongear);
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileVisiongear.setImageResource(id);
        }
        if(shieldgear != 0) {
            String filenameString = String.format("shield%s", shieldgear);
            int id = getContext().getResources().getIdentifier(filenameString, "drawable", getContext().getPackageName());
            profileShieldgear.setImageResource(id);
        }




        return v;
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