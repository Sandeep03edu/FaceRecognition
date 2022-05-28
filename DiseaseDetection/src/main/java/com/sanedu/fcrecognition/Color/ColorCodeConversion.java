package com.sanedu.fcrecognition.Color;

import android.util.Log;

import com.sanedu.fcrecognition.Model.ColorCode;


/**
 * @author Sandeep
 * Java class to convert one color code to another
 * Reference - http://www.easyrgb.com/en/math.php
 */
public class ColorCodeConversion {

    private static final String TAG = ColorCodeConversion.class.getName() + "TAG";

    /**
     * Function to convert RGB color to CIE-LAB color
     * @param RgbColor - Rgb ColorCode class object
     * @return - ColorCode with CieLab values
     */
    public static ColorCode Rgb2CieLab(ColorCode RgbColor) {
        ColorCode Xyz = Rgb2Xyz(RgbColor);
        ColorCode CieLab = Xyz2CieLab(Xyz);
        return CieLab;
    }

    /**
     * Function to return differences between two colorCode
     * @param CieLab1 - Given color 1
     * @param CieLab2 - Given color 2
     * @return - Double value, difference between two colors
     */
    public static double Delta94Value(ColorCode CieLab1, ColorCode CieLab2) {
        double WtL = 1;
        double WtC = 1;
        double WtH = 1;

        double Cl1 = CieLab1.getA(), Ca1 = CieLab1.getB(), Cb1 = CieLab1.getC();
        double Cl2 = CieLab2.getA(), Ca2 = CieLab2.getB(), Cb2 = CieLab2.getC();

        double xC1 = Math.sqrt(Math.pow(Ca1, 2) + Math.pow(Cb1, 2));
        double xC2 = Math.sqrt(Math.pow(Ca2, 2) + Math.pow(Cb2, 2));
        double xDL = Cl2 - Cl1;
        double xDC = xC2 - xC1;
        double xDE = Math.sqrt(
                (Cl1 - Cl2) * (Cl1 - Cl2) +
                        (Ca1 - Ca2) * (Ca1 - Ca2) +
                        (Cb1 - Cb2) * (Cb1 - Cb2)
        );

        double xDH = (xDE * xDE) - (xDL * xDL) - (xDC * xDC);
        if (xDH > 0) {
            xDH = Math.sqrt(xDH);
        } else {
            xDH = 0;
        }

        double xSC = 1 + (0.045 * xC1);
        double xSH = 1 + (0.015 * xC1);

        xDL /= WtL;
        xDC /= WtC * xSC;
        xDH /= WtH * xSH;

        double Del94 = Math.sqrt(Math.pow(xDL, 2) + Math.pow(xDC, 2) + Math.pow(xDH, 2));
        return Del94;
    }

    /**
     * Function to Convert Rgb colorCode to Xyz colorCode
     * @param RgbColor - Given ColorCode
     * @return - ColorCode with Xyz values
     */
    private static ColorCode Rgb2Xyz(ColorCode RgbColor) {
        double sR = RgbColor.getA();
        double sG = RgbColor.getB();
        double sB = RgbColor.getC();
        double var_R = (sR / 255);
        double var_G = (sG / 255);
        double var_B = (sB / 255);

        if (var_R > 0.04045) {
            var_R = Math.pow((var_R + 0.055) / 1.055, 2.4);
        } else {
            var_R = var_R / 12.92;
        }
        if (var_G > 0.04045) {
            var_G = Math.pow((var_G + 0.055) / 1.055, 2.4);
        } else {
            var_G = var_G / 12.92;
        }
        if (var_B > 0.04045) {
            var_B = Math.pow((var_B + 0.055) / 1.055, 2.4);
        } else {
            var_B = var_B / 12.92;
        }

        var_R = var_R * 100;
        var_G = var_G * 100;
        var_B = var_B * 100;

        double X = (var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805);
        double Y = (var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722);
        double Z = (var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505);

        ColorCode XYZColorCode = new ColorCode(X, Y, Z);
        Log.d(TAG, "Rgb2Xyz: XYZ: " + XYZColorCode);
        return XYZColorCode;
    }

    /**
     * Function to convert Xyz colorCode to CieLab colorCode
     * @param XYZColorCode - Given Xyz colorCode
     * @return - ColorCode with CieLab values
     */
    private static ColorCode Xyz2CieLab(ColorCode XYZColorCode) {
        double X = XYZColorCode.getA();
        double Y = XYZColorCode.getB();
        double Z = XYZColorCode.getC();

        double Ref_X = 94.811;
        double Ref_Y = 100.00;
        double Ref_Z = 107.304;

        double var_X = X / Ref_X;
        double var_Y = Y / Ref_Y;
        double var_Z = Z / Ref_Z;

        if (var_X > 0.008856) {
            var_X = Math.pow(var_X, (1 / 3));
        } else {
            var_X = (7.787 * var_X) + (16 / 116);
        }
        if (var_Y > 0.008856) {
            var_Y = Math.pow(var_Y, (1 / 3));
        } else {
            var_Y = (7.787 * var_Y) + (16 / 116);
        }
        if (var_Z > 0.008856) {
            var_Z = Math.pow(var_Z, (1 / 3));
        } else {
            var_Z = (7.787 * var_Z) + (16 / 116);
        }

        double CIE_L = (116 * var_Y) - 16;
        double CIE_a = 500 * (var_X - var_Y);
        double CIE_b = 200 * (var_Y - var_Z);

        ColorCode CieLabColorCode = new ColorCode(CIE_L, CIE_a, CIE_b);
        Log.d(TAG, "Xyz2CieLab: CIE ColorCode: " + CieLabColorCode);
        return CieLabColorCode;
    }
}