package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class Utility {
    private Utility(){}

    /**
     * Limits the number of decimals
     * @param x the number of which to limit the number of decimals
     * @return the same number but with reduced decimals
     */
    public static double reduceDecimals(double x){
        DecimalFormat numberFormat = new DecimalFormat("#.0000000000000");
        return Double.parseDouble(numberFormat.format(x));
    }

    /**
     * This method is used when I want to create a cdf function from a pdf function,
     * this only has to be called once since the cdf file can be saved for later use
     * @param filename
     */
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
