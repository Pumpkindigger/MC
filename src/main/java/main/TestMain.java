package main;

import main.objects.GasLayerBend2D;
import main.objects.Photon;
import scatterFunctions.HenyeyGreensteinScatter;

import java.util.ArrayList;

public class TestMain {

    public static void main(String[] args){
        Photon photon = new Photon(1, 1, 0);
        photon.setOldCoordinate(new Coordinate(0, 3, 0));
        GasLayerBend2D gasLayer = new GasLayerBend2D(5, 2, 90, 270, 2, 0, new HenyeyGreensteinScatter());
        ArrayList<Coordinate> intersections = photon.getIntersectionPoints(gasLayer);
        for (Coordinate coordinate: intersections) {
            System.out.println(coordinate.toString());
        }
    }


}
