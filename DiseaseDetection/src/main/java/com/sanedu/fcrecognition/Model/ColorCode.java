package com.sanedu.fcrecognition.Model;

public class ColorCode {
    double a;
    double b;
    double c;

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

    @Override
    public String toString() {
        return "ColorCode{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
