package com.sanedu.fcrecognition.Model;

/**
 * @author Sandeep
 * ColorCode class
 */
public class ColorCode {
    double a;
    double b;
    double c;

    /**
     *
     * @param a - Color code 1st parameter (R for RGB)
     * @param b - Color code 2nd parameter (G for RGB)
     * @param c - Color code 3rd parameter (B for RGB)
     */
    public ColorCode(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    /**
     *
     * @return String - ColorCode value as a string
     */
    @Override
    public String toString() {
        return "ColorCode{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
