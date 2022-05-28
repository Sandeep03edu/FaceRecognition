package com.sanedu.fcrecognition.Model;

/**
 * @author Sandeep
 * Edge class
 */
public class Edges {
    private double leftCoord;
    private double rightCoord;
    private double topCoord;
    private double bottomCoord;

    /**
     * Constructor
     * @param leftCoord - double - left most coordinate of rectangle
     * @param rightCoord - double - right most coordinate of rectangle
     * @param topCoord - double - top most coordinate of rectangle
     * @param bottomCoord - double - bottom most coordinate of rectangle
     */
    public Edges(double leftCoord, double rightCoord, double topCoord, double bottomCoord) {
        this.leftCoord = leftCoord;
        this.rightCoord = rightCoord;
        this.topCoord = topCoord;
        this.bottomCoord = bottomCoord;
    }

    public boolean isValidEdge(){
        return leftCoord>=0 && rightCoord>=0 && topCoord>=0 && bottomCoord>=0 && getWidth()>=0 && getHeight()>=0;
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

    /**
     * Getting width of Edge i.e., rectangle
     * @return width of edge
     */
    public int getWidth() {
        return (int) (this.rightCoord - this.leftCoord + 0.5);
    }

    /**
     * Getting Height of Edge i.e., rectangle
     * @return height of edge
     */
    public int getHeight() {
        return (int) (this.topCoord - this.bottomCoord + 0.5);
    }

    /**
     * Converting edge object to string
     * @return - String - Concatenated string with object parameters
     */
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