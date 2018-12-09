package main;

import java.io.*;
import java.util.StringTokenizer;

public class Reader {

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
