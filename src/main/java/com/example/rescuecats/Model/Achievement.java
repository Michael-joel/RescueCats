package com.example.rescuecats.Model;

public class Achievement {

    private int achievement_ID;
    private String achievement_name;
    private boolean unlocked;

    public Achievement(int achievement_ID, String achievement_name, boolean unlocked) {

        this.achievement_ID = achievement_ID;
        this.achievement_name = achievement_name;
        this.unlocked = unlocked;
    }

    public int getAchievement_ID() {
        return achievement_ID;
    }

    public void setAchievement_ID(int achievement_ID) {
        this.achievement_ID = achievement_ID;
    }

    public String getAchievement_name() {
        return achievement_name;
    }

    public void setAchievement_name(String achievement_name) {
        this.achievement_name = achievement_name;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }


}
