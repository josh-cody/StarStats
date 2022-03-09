package com.example.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class Maps extends AppCompatActivity {

    private ScrollView container;
    private RecyclerView recyclerView;
    private JSONArray jsonArray;
    private ArrayList<ThisMap> mapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        recyclerView = findViewById(R.id.mapsList); container = findViewById(R.id.mapsScroll);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("def", Context.MODE_PRIVATE);

        if(pref.getString("mapresponse","").equals("")) { //TODO: USES STORED DATA, WILL NOT UPDATE ONCE CALLED ONCE. NEED TO IMPLEMENT 5MIN TIMER OR SOMETHING.
            ApiThread apiThread = new ApiThread(getApplicationContext(), 2);
            apiThread.start();
            try {   apiThread.join();  } catch (InterruptedException e) { e.printStackTrace();  }
        }
        mapList = new ArrayList<>();
        try { populateMapList(pref.getString("mapresponse", "NO RESPONSE")); } catch (JSONException e) { e.printStackTrace(); }
        try { setMapAdapter(); } catch (JSONException e) { e.printStackTrace(); }

    }

    public void populateMapList(String RESPONSE_FROM_API) throws JSONException {
        jsonArray = new JSONArray(RESPONSE_FROM_API);
        for(int i = 0; i < jsonArray.length()-1; i++){
            JSONObject t = (JSONObject) jsonArray.get(i);
            JSONObject tmpMap = (JSONObject) t.get("event");
            mapList.add(new ThisMap(tmpMap.getString("map"), tmpMap.getString("mode")));
        }
    }

    public void setMapAdapter() throws JSONException {
        MapAdapter mapAdapter = new MapAdapter(mapList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mapAdapter);
    }

    class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {
        ArrayList<ThisMap> mapList;
        public MapAdapter(ArrayList<ThisMap> mapList) {this.mapList = mapList;}

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mapName;
            private final ConstraintLayout mapBack;
            public ViewHolder(View view) {
                super(view);
                mapName = view.findViewById(R.id.mapName); mapBack = view.findViewById(R.id.mapBack);
            }
        }

        @NonNull
        @Override
        public MapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            ThisMap thisMap = mapList.get(position);
            holder.mapName.setText(thisMap.map);

            Context context1 = holder.mapBack.getContext();
            int id1 = context1.getResources().getIdentifier(thisMap.mode.toLowerCase(), "drawable", context1.getPackageName());
            holder.mapBack.setBackgroundResource(id1);
        }

        @Override
        public int getItemCount() {
            return mapList.size();
        }


    }
}