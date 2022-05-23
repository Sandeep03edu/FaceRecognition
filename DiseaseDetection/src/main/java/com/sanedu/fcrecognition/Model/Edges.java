package com.sanedu.fcrecognition.Model;

public class Edges {
    private double leftCoord;
    private double rightCoord;
    private double topCoord;
    private double bottomCoord;

    public Edges(double leftCoord, double rightCoord, double topCoord, double bottomCoord) {
        this.leftCoord = leftCoord;
        this.rightCoord = rightCoord;
        this.topCoord = topCoord;
        this.bottomCoord = bottomCoord;
    }

    public int getLeftCoord() {
        return (int) (leftCoord + 0.5);
    }

    public void setLeftCoord(double leftCoord) {
        this.leftCoord = leftCoord;
    }

    public int getRightCoord() {
        return (int) (rightCoord + 0.5);
    }

    public void setRightCoord(double rightCoord) {
        this.rightCoord = rightCoord;
    }

    public int getTopCoord() {
        return (int) (topCoord + 0.5);
    }

    public void setTopCoord(double topCoord) {
        this.topCoord = topCoord;
    }

    public int getBottomCoord() {
        return (int) (bottomCoord + 0.5);
    }

    public void setBottomCoord(double bottomCoord) {
        this.bottomCoord = bottomCoord;
    }

    public int getWidth() {
        return (int) (this.rightCoord - this.leftCoord + 0.5);
    }

    public int getHeight() {
        return (int) (this.topCoord - this.bottomCoord + 0.5);
    }

    @Override
    public String toString() {
        return "Edges{" +
                "leftCoord=" + leftCoord +
                ", rightCoord=" + rightCoord +
                ", topCoord=" + topCoord +
                ", bottomCoord=" + bottomCoord +
                '}';
    }
}