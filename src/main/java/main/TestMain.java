package main;

import main.objects.GasLayerBend2D;
import main.objects.Photon;
import scatterFunctions.HenyeyGreensteinScatter;

import java.util.ArrayList;

public class TestMain {

    public static void main(String[] args){
        Photon photon = new Photon(5.0, 3, 0);
        photon.setOldCoordinate(new Coordinate(0, 3, 0));
        GasLayerBend2D gasLayer = new GasLayerBend2D(5, 2, 90, 350, 2, 0, new HenyeyGreensteinScatter());
        ArrayList<Coordinate> intersections = photon.getIntersectionPoints(gasLayer);
        for (Coordinate coordinate: intersections) {
            System.out.println(coordinate.toString());
            System.out.println(photon.checkInBetween(coordinate));
            double omega = photon.calculateOmega(coordinate);
            System.out.println(omega);
            System.out.println(photon.checkInsideAngle(gasLayer, omega));
            System.out.println();
        }
        System.out.println(photon.backTrack(gasLayer));
        System.out.println(photon.getCurrentCoordinate().toString());
    }


}
