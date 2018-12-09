package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class Utility {
    private Utility(){}

    public static double reduceDecimals(double x){
        DecimalFormat numberFormat = new DecimalFormat("#.0000000000000");
        return Double.parseDouble(numberFormat.format(x));
    }

    public static void pdfToCdf(String filename) {
        try {
            FileReader file = new FileReader(new File(filename));
            BufferedReader br = new BufferedReader(file);
            String temp;
            String res = "";
            double total = 0.0;
            FileWriter out = new FileWriter("700nm_cdf.txt");
            while (true) {
                temp = br.readLine();
                if (temp == null){
                    break;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(temp);
                String angle = stringTokenizer.nextToken();
                total += Double.parseDouble(stringTokenizer.nextToken());
                res = angle + " " + total + "\n";
                out.append(res);
            }
            System.out.println(res);

            out.close();
        }
        catch (Exception e){e.printStackTrace();}
    }
}
