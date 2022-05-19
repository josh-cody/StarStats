package com.example.starstats;


import java.util.ArrayList;

public class Battle { //IN PROGRESS
    private String result, mode;
    private ArrayList<Battle> powerLeagueBattles;

    public Battle(String result, String mode) {
        this.result = result;
        this.mode = mode;
    }

    public Battle(String result, String mode, ArrayList<Battle> powerLeagueBattles) {
        this.result = result;
        this.mode = mode;
        this.powerLeagueBattles = powerLeagueBattles;
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
