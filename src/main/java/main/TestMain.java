package main;

import main.objects.GasLayerBend2D;
import main.objects.Photon;
import scatterFunctions.HenyeyGreensteinScatter;
import scatterFunctions.RayleighScatter;
import scatterFunctions.ScatterFunction;

public class TestMain {

    /** src/main/resources/<FILENAME>
     *
     */
    public static void main(String[] args){
        scatterTest();
    }

    private static void databankTest(){
        DataBank dataBank = new DataBank("src/main/resources/400nm_cdf.txt");
        System.out.println(dataBank.getAngle(238742));
    }

    private static void backtrackTest(){
        Photon photon = new Photon(-21.398631044570052, -6.82137225735025, -3.8029701420042796, true);
        photon.setOldCoordinate(new Coordinate(-18.731939427385893, -5.428530163937989, -2.238152582293153));
        System.out.println(photon.backTrack(new GasLayerBend2D(20, 10, 360, 0, 5, 0, new HenyeyGreensteinScatter())));
    }

    private static void scatterTest(){
        int count = 100000;
        double sum = 0.0;
        ScatterFunction scatter = new RayleighScatter();
        for(int i = 0; i< count; i++){
            sum += scatter.calculateAngle(0.0);
            //System.out.println(scatter.calculateAngle(0.0));
        }
        System.out.println(sum);
    }
}
