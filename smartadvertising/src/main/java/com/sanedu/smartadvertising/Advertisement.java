package com.sanedu.smartadvertising;

import java.util.ArrayList;

/**
 * gender
     *  0 -> Neutral
     *  1 -> Male
     * -1 -> Female
 */

public class Advertisement {
    private long id;
    private ArrayList<String> ageGroup;
    private int gender;
    private String advUrl;
    private long lastDisplayed;

    public Advertisement() {
    }

    public Advertisement(ArrayList<String> ageGroup, int gender, String advUrl, long lastDisplayed) {
        this.ageGroup = ageGroup;
        this.gender = gender;
        this.advUrl = advUrl;
        this.lastDisplayed = lastDisplayed;
    }

    public ArrayList<String> getAgeGroup() {
        return ageGroup;
    }

    public int getGender() {
        return gender;
    }

    public String getAdvUrl() {
        return advUrl;
    }

    public long getLastDisplayed() {
        return lastDisplayed;
    }

    public void setAgeGroup(ArrayList<String> ageGroup) {
        this.ageGroup = ageGroup;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setAdvUrl(String advUrl) {
        this.advUrl = advUrl;
    }

    public void setLastDisplayed(long lastDisplayed) {
        this.lastDisplayed = lastDisplayed;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "ageGroup=" + ageGroup +
                ", gender=" + gender +
                ", advUrl='" + advUrl + '\'' +
                ", lastDisplayed=" + lastDisplayed +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
