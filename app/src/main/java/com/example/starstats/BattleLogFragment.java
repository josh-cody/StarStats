package com.example.starstats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BattleLogFragment extends Fragment {

    private String tag;
    private JSONObject rawData;
    ArrayList<Battle> plBattles;
    private JSONArray battles;
    private ArrayList<Battle> battleList;
    private RecyclerView battleRecyclerView;

    public BattleLogFragment() {}

    public static BattleLogFragment newInstance(String tag) {
        BattleLogFragment fragment = new BattleLogFragment();
        Bundle args = new Bundle();
        args.putString("tag", tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tag = getArguments().getString("tag");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_battle_log, container, false);
        battleRecyclerView = v.findViewById(R.id.battleRecyclerView);
        ApiThread apiThread = new ApiThread(getContext(), this.tag, 4);
        apiThread.start();
        try {
            apiThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedPreferences pref = getActivity().getSharedPreferences("def", Context.MODE_PRIVATE);
        String RESPONSE_FROM_API = pref.getString("battleresponse","NO_RESPONSE");

        battleList = new ArrayList<>();
        try { createBattles(RESPONSE_FROM_API); }  catch (JSONException e) { e.printStackTrace(); }
        setBattleAdapter();

        return v;
    }

    public void createBattles(String RESPONSE_FROM_API) throws JSONException {
        boolean plFlag = false;
        rawData = new JSONObject(RESPONSE_FROM_API);
        battles = rawData.getJSONArray("items");
        System.out.println("ITEMS: "+battles);
        for(int i = 0; i < battles.length()-1; i++) {
            JSONObject t = (JSONObject) battles.get(i);
            if(t.has("battle")) {
                JSONObject tmp1 = (JSONObject) t.get("battle");
                if(tmp1.has("type")) {
                    if(tmp1.getString("type").equals("soloRanked") && !plFlag) {
                        plBattles = new ArrayList<>();
                        plFlag = true;
                        plBattles.add(new Battle(tmp1.getString("result"), tmp1.getString("mode")));
                    }
                }

                if(plFlag) {
                    if(!tmp1.getString("type").equals("soloRanked")) {
                        plFlag = false;
                        String res;
                        String mode = plBattles.get(0).getMode();
                        int v = 0;
                        int l = 0;
                        for(Battle b : plBattles) {
                            if(b.getResult().equals("victory")) {
                                v++;
                            }
                            else { l++; }
                        }
                        if((float)(v/(v+l)) < .5) {
                            res = "victory";
                        } else {  res="loss"; }
                        battleList.add(new Battle(res, "powerleague", plBattles));
                    }
                }
                else if(tmp1.has("result")) {
                    battleList.add(new Battle(tmp1.getString("result"), tmp1.getString("mode")));
                }

            }
        }
    }

    public void setBattleAdapter() {
        BattleAdapter battleAdapter = new BattleAdapter(battleList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1);
        battleRecyclerView.setLayoutManager(layoutManager);
        battleRecyclerView.setAdapter(battleAdapter);
    }

    static class BattleAdapter extends RecyclerView.Adapter<BattleLogFragment.BattleAdapter.ViewHolder> {

        private final ArrayList<Battle> battleList;
        Map<String, String> modes = new HashMap<>();

        public BattleAdapter(ArrayList<Battle> battleList) {   this.battleList = battleList;  }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout linearLayout;
            private TextView textView, modeNameBattle;
            private ImageView battleBack;
            public ViewHolder(View view) {
                super(view);
                battleBack = view.findViewById(R.id.backgroundBattle);
                modeNameBattle = view.findViewById(R.id.modeNameBattle);
                linearLayout = view.findViewById(R.id.battleBack);
                textView = view.findViewById(R.id.battleText);
            }
        }

        @NonNull
        @Override
        public BattleLogFragment.BattleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.battle, parent, false);
            modes.put("gemGrab","Gem Grab");
            modes.put("soloShowdown", "Showdown");
            modes.put("brawlBall","Brawl Ball");
            modes.put("heist", "Heist");
            modes.put("bounty","Bounty");
            modes.put("payload","Payload");
            modes.put("hotZone","Hot Zone");
            modes.put("knockout", "Knockout");
            modes.put("basketBrawl", "Basket Brawl");
            modes.put("duels", "Duels");

            return new BattleLogFragment.BattleAdapter.ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull BattleLogFragment.BattleAdapter.ViewHolder holder, int position) {
            Battle battle = battleList.get(position);
            if(battle.getResult().equals("victory")) {
                holder.textView.setText("victory");
                holder.battleBack.setBackgroundColor(Color.argb(0,0,255,0));
            }
            else {
                holder.battleBack.setBackgroundColor(Color.argb(0,255,0,0));
                holder.textView.setText("loss");
            }
            holder.modeNameBattle.setText(modes.get(battle.getMode()));
        }
        @Override
        public int getItemCount() {
            return battleList.size();
        }
    }
}