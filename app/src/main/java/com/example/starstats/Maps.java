package com.example.starstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class Maps extends AppCompatActivity {

    private ScrollView container;
    private RecyclerView recyclerView;
    private ArrayList<ThisMap> maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        recyclerView = findViewById(R.id.mapsList); container = findViewById(R.id.mapsScroll);
    }

    class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {


        //TODO: MAKE API REQUEST TO SHOW MAPS

        private ArrayList<ThisMap> mapList;
        public MapAdapter(ArrayList<ThisMap> mapList) {
            this.mapList = mapList;
        }



        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView mode;
            private final TextView mapName;
            public ViewHolder(View view) {
                super(view);
                mode = view.findViewById(R.id.mode); mapName = view.findViewById(R.id.mapName);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ThisMap thisMap = mapList.get(position);
            holder.mapName.setText(thisMap.name);

            Context context1 = holder.mode.getContext();
            String temp = thisMap.name;
            int id1 = context1.getResources().getIdentifier(temp, "drawable", context1.getPackageName());

            holder.mode.setImageResource(id1);

        }

        @Override
        public int getItemCount() {
            return 0;
        }


    }
}