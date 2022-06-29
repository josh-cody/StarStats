package com.starstats.starstats;

public class Brawler {
    String name, jsonStarpowers, jsonGadgets;
    int powerLevel, trophies, rank, highestBrawler;
    String starpower1, starpower2, gadget1, gadget2;
    int speedgear, healthgear, damagegear, visiongear, shieldgear;

    public Brawler(String name, int powerLevel, int trophies, int rank, int highestBrawler, String starpower1, String starpower2, String gadget1, String gadget2, int speedgear, int healthgear, int damagegear, int visiongear, int shieldgear) {
        this.name = name;
        this.powerLevel = powerLevel;
        this.trophies = trophies;
        this.rank = rank;
        this.highestBrawler = highestBrawler;
        this.starpower1 = starpower1;
        this.starpower2 = starpower2;
        this.gadget1 = gadget1;
        this.gadget2 = gadget2;
        this.speedgear = speedgear;
        this.healthgear = healthgear;
        this.damagegear = damagegear;
        this.visiongear = visiongear;
        this.shieldgear = shieldgear;
    }

    public Brawler(String name, String jsonStarpowers, String jsonGadgets)
    {
        this.name = name;
        this.jsonGadgets = jsonGadgets;
        this.jsonStarpowers = jsonStarpowers;
    }
}
