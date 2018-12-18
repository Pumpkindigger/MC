package main;

import main.objects.GasLayerBend2D;
import main.objects.Photon;
import org.jzy3d.maths.Pair;
import scatterFunctions.HenyeyGreensteinScatter;

import java.util.ArrayList;

public class SimulationBend {

    public static void main(String[] args) {
        int nrPhotons = 100000;


        //Initialize the arraylist of gaslayers
        ArrayList<GasLayerBend2D> gasLayers = new ArrayList<GasLayerBend2D>();
        //Initialize the seperate gas layer and add them to the list

        //GasLayerBend2D gasLayer1 = new GasLayerBend2D(10, 5, 360, 0, 2, 0.0, new HenyeyGreensteinScatter());
        //GasLayerBend2D gasLayer1 = new GasLayerBend2D(10, 5, 360, 0, 2, 0.0, new CdfScatter("src/main/resources/400nm_cdf.txt"));
        GasLayerBend2D gasLayer2 = new GasLayerBend2D(10, 5, 360, 0, 0.001, 0.0, new HenyeyGreensteinScatter());

        gasLayers.add(gasLayer2);
        //gasLayers.add(gasLayer1);

        int[] angles = new int[]{10, 20, 30, 45, 60, 75, 90, 120};
        double[] ks = new double[]{0.001, 0.005, 0.01, 0.05, 0.1, 0.5, 1,2,3,4, 5, 10};
        //-------For simulation 1 k value, for multiple angles-------//
        //double weight = simulateAngles(gasLayers, nrPhotons, new int[] {20, 30, 45, 60, 75, 90, 120});
        //System.out.println("Photon packets that made it: " + weight/Constants.startingWeight);

        ArrayList<Pair<Double, Double>> coordinates = multipleKs(gasLayers, nrPhotons, angles, ks);

        plotResults2D(coordinates);

    }

    public static void plotResults2D(ArrayList<Pair<Double, Double>> coordinates){
        final Plotter plotter = new Plotter("Title", new String[]{"Nr of photons packets"});
        for (Pair<Double, Double> coordinate : coordinates){
            plotter.addData(coordinate.a, coordinate.b, 1);
        }
        plotter.plot();
    }

    public static ArrayList<Pair<Double, Double>> multipleKs(ArrayList<GasLayerBend2D> gasLayers, int nrPhotons, int[] angles, double[] ks){
        ArrayList<Pair<Double, Double>> coordinates = new ArrayList<>();
        for (double k : ks){
            for (GasLayerBend2D gasLayer : gasLayers){
                gasLayer.setK(k);
            }
            Double weight = simulateAngles(gasLayers, nrPhotons, angles) / Constants.startingWeight;
            coordinates.add(new Pair<>(k, weight));
        }
        return coordinates;
    }

    public static double simulateAngles(ArrayList<GasLayerBend2D> gasLayers, int nrPhotons, int[] angles){
        double totalWeight = 0.0;
        for (int angle: angles) {
            ArrayList<Photon> photons = new ArrayList<Photon>(nrPhotons);

            //Create all the protons
            //TODO: Magic number in radius of photon creation, fix this
            for (int i = 0; i < nrPhotons; i++) {
                photons.add(new Photon(gasLayers.size(), 10, angle));
            }

            //---------------------NOTE!!!!------------------------------//
            //For now im making the assumption that all layers cover 360 degrees.
            for (Photon photon : photons){
                photon.stepInside();
                simulateOnePhoton(photon, gasLayers);
            }
//            if (angle == 20){
//                plotResult(nrPhotons, gasLayers.get(gasLayers.size()-1), photons);
//            }


            totalWeight += weightBetweenAngles(photons, 355, 350);
        }
        return totalWeight;
    }

    public static void simulateOnePhoton(Photon photon, ArrayList<GasLayerBend2D> gasLayers) {
        int maxLayer = gasLayers.size();
        while (!photon.isEliminated()) {

            //Get the current gaslayer
            GasLayerBend2D currentGasLayer = gasLayers.get(photon.getCurrentLayer()-1);

            //Perform a step
            Simulation3D.performStep(currentGasLayer, photon);

            //Backtrack the position and get a positionEnum back
            PositionEnum position = photon.backTrack(currentGasLayer);

            //Check the position of the photon
            switch (position) {
                //If above, then the photon has exited the gaslayer on the high side, we either move it to a higher gaslayer or eliminate it
                case ABOVE:
                    //If there are no higher gaslayers, then eliminate the photon
                    if (photon.getCurrentLayer() == maxLayer){
                        photon.eliminate();
                    }
                    //If there are higher gaslayers, set the current gaslayer of the photon to 1 higher
                    else{
                        photon.setCurrentLayer(photon.getCurrentLayer()+1);
                        photon.stepOutside();
                    }
                    break;
                //If inside, then the photon is still in the gaslayer, so we update the angle and the weight and check if we should eliminate the photon
                case INSIDE:
                    //update the weight of the photon
                    photon.updateWeight(currentGasLayer);

                    //Update the angle of the photon
                    photon.updateAngle(currentGasLayer);

                    //Check if the photon is eliminated
                    photon.checkElimination();
                    break;
                //If under, then the photon has exited the gaslayer on the low side, we check if it should be eliminated and set the current layer to += -1
                case UNDER:
                    //If the photon is in the lowest layer, it has made it to the surface
                    if(photon.getCurrentLayer() == 1){
                        photon.madeIt();
                    }
                    //Else we move the photon to the layer below
                    else {
                        photon.setCurrentLayer(photon.getCurrentLayer() - 1);
                        photon.stepInside();
                    }
                    break;
                //If left, then the photon exited the gaslayer on the left side, for now we eliminate the photon
                case LEFT:
                    System.out.println("eft");
                    photon.eliminate();
                    break;
                //if right, then the photon exited the gaslayer on the right side, for now we eliminate the photon
                case RIGHT:
                    System.out.println("right");
                    photon.eliminate();
                    break;
            }
        }
    }


    public static double weightBetweenAngles(ArrayList<Photon> photons, double left, double right) {
        double res = 0.0;
        for (Photon photon : photons) {
            if (photon.getMadeIt()) {
                if (photon.checkInsideAngle(left, right, photon.calculateOmega(photon.getCurrentCoordinate()))){
                    res += photon.getWeight();
                }
            }
        }
        return res;
    }
}
