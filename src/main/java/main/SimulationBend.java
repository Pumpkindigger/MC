package main;

import main.objects.GasLayerBend2D;
import main.objects.Photon;
import org.jzy3d.maths.Pair;
import scatterFunctions.HenyeyGreensteinScatter;

import java.util.ArrayList;

public class SimulationBend {

    private static double leftAngle = 355;
    private static double rightAngle = 350;

    public static void main(String[] args) {
        int nrPhotons = 100000;


        ArrayList<ArrayList<GasLayerBend2D>> atmosphere = new ArrayList<>();

        //Initialize the arraylist of gaslayers
        ArrayList<GasLayerBend2D> gasLayers1 = new ArrayList<GasLayerBend2D>();
        ArrayList<GasLayerBend2D> gasLayers2 = new ArrayList<GasLayerBend2D>();

        //Initialize the separate gas layer and add them to the list
        //GasLayerBend2D gasLayer1 = new GasLayerBend2D(10, 5, 360, 0, 2, 0.0, new HenyeyGreensteinScatter());
        //GasLayerBend2D gasLayer1 = new GasLayerBend2D(10, 5, 360, 0, 2, 0.0, new CdfScatter("src/main/resources/400nm_cdf.txt"));
        //GasLayerBend2D gasLayer2 = new GasLayerBend2D(10, 5, 360, 0, 0.001, 0.0, new HenyeyGreensteinScatter());

        GasLayerBend2D gasLayer1 = new GasLayerBend2D(10, 5, 180, 0, 1, 0.0, new HenyeyGreensteinScatter());
        GasLayerBend2D gasLayer2 = new GasLayerBend2D(10, 5, 360, 180, 2, 0.0, new HenyeyGreensteinScatter());
        //-----------------Note: Add gaslayers in ascending left omega order---------------
        gasLayers1.add(gasLayer1);
        gasLayers1.add(gasLayer2);

        GasLayerBend2D gasLayer3 = new GasLayerBend2D(20, 10, 180, 0, 1, 0.0, new HenyeyGreensteinScatter());
        GasLayerBend2D gasLayer4 = new GasLayerBend2D(20, 10, 360, 180, 2, 0.0, new HenyeyGreensteinScatter());

        gasLayers2.add(gasLayer3);
        gasLayers2.add(gasLayer4);

        //------------Note: add lists of gaslayers in descending outerR---------------------
        atmosphere.add(gasLayers2);
        atmosphere.add(gasLayers1);

        ArrayList<Photon> photons = new ArrayList<>();
        for (int i = 0; i < nrPhotons; i++) {
            photons.add(new Photon(atmosphere.size(), 20, 45));
        }
        simulatePhotons(atmosphere, photons);
        Simulation3D.plotResult(nrPhotons, 20, photons);


//        int[] angles = new int[]{10, 20, 30, 45, 60, 75, 90, 120};
//        double[] ks = new double[]{0.001, 0.005, 0.01, 0.05, 0.1, 0.5, 1,2,3,4, 5, 10};
        //-------For simulation 1 k value, for multiple angles-------//
        //double weight = simulateAngles(gasLayers, nrPhotons, new int[] {20, 30, 45, 60, 75, 90, 120});
        //System.out.println("Photon packets that made it: " + weight/Constants.startingWeight);

//        ArrayList<Pair<Double, Double>> coordinates = multipleKs(atmosphere, nrPhotons, angles, ks);
//
//        plotResults2D(coordinates);

    }

    public static void plotResults2D(ArrayList<Pair<Double, Double>> coordinates){
        final Plotter plotter = new Plotter("Title", new String[]{"Nr of photons packets"});
        for (Pair<Double, Double> coordinate : coordinates){
            plotter.addData(coordinate.a, coordinate.b, 1);
        }
        plotter.plot();
    }

    public static ArrayList<Pair<Double, Double>> multipleKs(ArrayList<ArrayList<GasLayerBend2D>> gasLayers, int nrPhotons, int[] angles, double[] ks){
        ArrayList<Pair<Double, Double>> coordinates = new ArrayList<>();
        for (double k : ks){
            for (ArrayList<GasLayerBend2D> gasLayerList : gasLayers){
                for (GasLayerBend2D gasLayer : gasLayerList){
                    gasLayer.setK(k);
                }
            }
            Double weight = simulateAngles(gasLayers, nrPhotons, angles) / Constants.startingWeight;
            coordinates.add(new Pair<>(k, weight));
        }
        return coordinates;
    }

    public static double simulateAngles(ArrayList<ArrayList<GasLayerBend2D>> atmosphere, int nrPhotons, int[] angles){
        double totalWeight = 0.0;
        for (int angle: angles) {
            ArrayList<Photon> photons = new ArrayList<Photon>(nrPhotons);

            //Create all the protons
            //TODO: Magic number in radius of photon creation, fix this
            for (int i = 0; i < nrPhotons; i++) {
                photons.add(new Photon(atmosphere.size(), 10, angle));

            }

            //---------------------NOTE!!!!------------------------------//
            //For now im making the assumption that all layers cover 360 degrees.
            simulatePhotons(atmosphere, photons);


            totalWeight += weightBetweenAngles(photons, leftAngle, rightAngle);
        }
        return totalWeight;
    }

    public static void simulatePhotons(ArrayList<ArrayList<GasLayerBend2D>> gasLayers, ArrayList<Photon> photons) {
        for (Photon photon : photons){
            photon.calculateHorizontalIndex(gasLayers.get(0));
            photon.stepInside();
            simulateOnePhoton(photon, gasLayers);
        }
    }

    public static void simulateOnePhoton(Photon photon, ArrayList<ArrayList<GasLayerBend2D>> atmosphere) {
        int maxLayer = atmosphere.size();
        while (!photon.isEliminated()) {

            //Get the current gaslayer
            GasLayerBend2D currentGasLayer = atmosphere.get(photon.getCurrentLayer()-1).get(photon.getHorizontalIndex());

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
                        photon.calculateHorizontalIndex(atmosphere.get(photon.getCurrentLayer()-1));
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
                        photon.calculateHorizontalIndex(atmosphere.get(photon.getCurrentLayer()-1));
                    }
                    break;
                //If left, then the photon exited the gaslayer on the left side, for now we eliminate the photon
                case LEFT:
                    photon.setHorizontalIndex((photon.getHorizontalIndex()+1) % atmosphere.get(photon.getCurrentLayer()-1).size());
                    //System.out.println("left");
                    break;
                //if right, then the photon exited the gaslayer on the right side, for now we eliminate the photon
                case RIGHT:
                    photon.setHorizontalIndex(Math.abs(photon.getHorizontalIndex()-1 % atmosphere.get(photon.getCurrentLayer()-1).size()));
                    //System.out.println("right");
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
