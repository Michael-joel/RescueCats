package com.example.rescuecats.Model;

public class Bomb {

    int x;
    double y;

    public Bomb(int x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void moveBombForward(double bombSpeed) {
         y+=bombSpeed;
    }
}
