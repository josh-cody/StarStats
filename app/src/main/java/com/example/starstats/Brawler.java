package com.example.starstats;

public class Brawler {
    String name;
    int powerLevel, trophies, rank, highestBrawler;
    public Brawler(String name, int powerLevel, int trophies, int rank, int highestBrawler) {
        this.name = name;
        this.powerLevel = powerLevel;
        this.trophies = trophies;
        this.rank = rank;
        this.highestBrawler = highestBrawler;
    }
    public Brawler(String name) {this.name = name;}

    public void setBrawlerName(String name) { this.name = name; }

    public String getBrawlerName() {   return name;  }

    public int getTrophies() {  return trophies; }

    public void setTrophies(int trophies) { this.trophies = trophies; }

    public int getRank() {  return rank;  }

    public void setRank(int rank) { this.rank = rank; }

    public int getPowerLevel() {   return powerLevel;  }

    public void setPowerLevel(int powerLevel) { this.powerLevel = powerLevel; }

    public int getHighestBrawler() { return highestBrawler;  }

    public void setHighestBrawler(int highestBrawler) { this.highestBrawler = highestBrawler;  }
}
