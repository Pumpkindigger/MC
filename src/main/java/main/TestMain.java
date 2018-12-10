package main;

import main.objects.GasLayerBend2D;
import main.objects.Photon;
import scatterFunctions.HenyeyGreensteinScatter;

public class TestMain {

    /** src/main/resources/<FILENAME>
     *
     */
    public static void main(String[] args){
        backtrackTest();
    }

    private static void databankTest(){
        DataBank dataBank = new DataBank("src/main/resources/400nm_cdf.txt");
        System.out.println(dataBank.getAngle(238742));
    }

    private static void backtrackTest(){
        Photon photon = new Photon(-4.56632929756349, 2.036820253781204, 0);
        photon.setOldCoordinate(new Coordinate(-4.715312630145214, 2.085530369552514, 0));
        System.out.println(photon.backTrack(new GasLayerBend2D(10, 5, 360, 0, 5, 0, new HenyeyGreensteinScatter())));
    }
}
