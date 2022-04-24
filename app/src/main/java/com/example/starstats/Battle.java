package com.example.starstats;


public class Battle {
    private String result, mode;
    //private int trophyChange;
    public Battle(String result, String mode) {
        this.result = result;
        this.mode = mode;
        //this.trophyChange = trophyChange;
    }


    public String getMode() {
        return this.mode;
    }

    public String getResult() {
        return this.result;
    }
}
