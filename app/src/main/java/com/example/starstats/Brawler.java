package com.example.starstats;

public class Brawler {
    String name;
    int powerLevel, trophies, rank;
    public Brawler(String name, int powerLevel, int trophies, int rank) {
        this.name = name;
        this.powerLevel = powerLevel;
        this.trophies = trophies;
        this.rank = rank;
    }

    public String getBrawlerName() {   return name;  }

    public int getTrophies() {  return trophies; }

    public int getRank() {  return rank;  }

    public int getPowerLevel() {   return powerLevel;  }
}
