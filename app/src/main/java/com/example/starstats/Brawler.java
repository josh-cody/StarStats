package com.example.starstats;

public class Brawler {
    String name, jsonStarpowers, jsonGadgets;
    int powerLevel, trophies, rank, highestBrawler;

    public Brawler(String name, int powerLevel, int trophies, int rank, int highestBrawler) {
        this.name = name;
        this.powerLevel = powerLevel;
        this.trophies = trophies;
        this.rank = rank;
        this.highestBrawler = highestBrawler;
    }

    public Brawler(String name, String jsonStarpowers, String jsonGadgets)
    {
        this.name = name;
        this.jsonGadgets = jsonGadgets;
        this.jsonStarpowers = jsonStarpowers;
    }
}
