package com.example.starstats;


import java.util.ArrayList;

public class Battle {
    private String result, mode;
    private ArrayList<Battle> powerLeagueBattles;
    //private int trophyChange;

    public Battle(String result, String mode) {
        this.result = result;
        this.mode = mode;
    }

    public Battle(String result, String mode, ArrayList<Battle> powerLeagueBattles) {
        this.result = result;
        this.mode = mode;
        this.powerLeagueBattles = powerLeagueBattles;
        //this.trophyChange = trophyChange;
    }

    public ArrayList<Battle> getPowerLeagueBattles() {
        return this.powerLeagueBattles;
    }

    public String getMode() {
        return this.mode;
    }

    public String getResult() {
        return this.result;
    }
}
