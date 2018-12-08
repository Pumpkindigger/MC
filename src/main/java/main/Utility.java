package main;

import java.text.DecimalFormat;

public class Utility {
    private Utility(){}

    public static double reduceDecimals(double x){
        DecimalFormat numberFormat = new DecimalFormat("#.0000000000000");
        return Double.parseDouble(numberFormat.format(x));
    }
}
