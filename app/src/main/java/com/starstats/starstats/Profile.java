package com.starstats.starstats;

public class Profile { // TO HOLD PROFILE VALUES

    private String tag, role, name, id, nameColor;
    private int trophies;

    public Profile(String name, String tag, String role, String nameColor, String id, int trophies) {
        this.name = name;
        this.tag = tag;
        this.role = role;
        this.nameColor = nameColor;
        this.id = id;
        this.trophies = trophies;
    }


    public Profile(String name, String tag, int trophies, String id, String nameColor) {
        this.name = name;
        this.tag = tag;
        this.trophies = trophies;
        this.id = id;
        this.nameColor = nameColor;
    }

    public String getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTag() {
        return this.tag;
    }

    public String getRole() {
        return this.role;
    }

    public int getTrophies() {
        return this.trophies;
    }

    public String getNameColor() {
        return this.nameColor;
    }
}
