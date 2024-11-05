package com.example.rescuecats.Model;

public class Bomb {

    int x,y;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void incrementY() {
         y+=2;
    }
}
