package main;

import main.objects.GasLayerBend2D;
import main.objects.Photon;
import scatterFunctions.HenyeyGreensteinScatter;
import scatterFunctions.RayleighScatter;

import java.util.ArrayList;

import static main.Simulation3D.plotResult;

public class SimulationBend {

    public static void main(String[] args) {
        int nrPhotons = 100000;


        //Initialize the arraylist of gaslayers
        ArrayList<GasLayerBend2D> gasLayers = new ArrayList<GasLayerBend2D>();
        //Initialize the seperate gas layer and add them to the list
        GasLayerBend2D gasLayer1 = new GasLayerBend2D(10, 5, 360, 0, 1, 1, new RayleighScatter());
        GasLayerBend2D gasLayer2 = new GasLayerBend2D(5, 2, 360, 0, 5, 0.0, new HenyeyGreensteinScatter());

        //gasLayers.add(gasLayer2);
        gasLayers.add(gasLayer1);

        int layers = gasLayers.size();

        ArrayList<Photon> photons = new ArrayList<Photon>(nrPhotons);

        //Create all the protons
        for (int i = 0; i < nrPhotons; i++) {
            photons.add(new Photon(layers, 10, 45));
        }

        /*
        for (GasLayerBend2D gasLayer : gasLayers) {
            photons = simulateOneGaslayer(gasLayer, photons);
            plotResult(nrPhotons, gasLayer, photons);
        }
        */

        //---------------------NOTE!!!!------------------------------
        //For now im making the assumption that all layers cover 360 degrees.
        for (Photon photon : photons){
            simulateOnePhoton(photon, gasLayers);
        }
        plotResult(nrPhotons, gasLayers.get(layers-1), photons);

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

            if (photon.calculateR(photon.getCurrentCoordinate()) > 10){
                System.out.println("sum ting wong");
            }

            //Check if the photon has passed the gas layer, if so, set its weight to zero
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
                    if(photon.getCurrentLayer() == 1){
                        photon.madeIt();
                    }
                    else {
                        photon.setCurrentLayer(photon.getCurrentLayer() - 1);
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

    public static ArrayList<Photon> simulateOneGaslayer(GasLayerBend2D gasLayer, ArrayList<Photon> photons) {
        int photonsPassed = 0;
        int nrBouncesLeft = Constants.maximumBounces;
        double totalWeight = 0;
        while (nrBouncesLeft > 0) {
            //Loop over all photon packets
            for (Photon photon : photons) {
                //If the proton is eliminated, then we no longer have to perform operations on it.
                if (photon.isEliminated()) {
                    continue;
                }
                Simulation3D.performStep(gasLayer, photon);

                PositionEnum position = photon.backTrack(gasLayer);

                //Check if the photon has passed the gas layer, if so, set its weight to zero
                switch (position) {
                    //If above, then the photon has exited the gaslayer on the high side, we for now eliminate the photon
                    case ABOVE:
                        photon.eliminate();
                        break;
                    //If inside, then the photon is still in the gaslayer, so we only have to check if we should eliminate the photon
                    case INSIDE:
                        photon.checkElimination();
                        break;
                    //If under, then the photon has exited the gaslayer on the low side, we check if it should be eliminated and set the current layer to += -1
                    case UNDER:
                        //TODO limit the dimensions of the photon to the outer layer of the gaslayer
                        photon.madeIt();
                        photon.setCurrentLayer(photon.getCurrentLayer() - 1);
                        totalWeight += photon.getWeight();
                        photonsPassed++;
                        break;
                    //If left, then the photon exited the gaslayer on the left side, for now we eliminate the photon
                    case LEFT:
                        photon.eliminate();
                        break;
                    //if right, then the photon exited the gaslayer on the right side, for now we eliminate the photon
                    case RIGHT:
                        photon.eliminate();
                        break;
                }
            }
            nrBouncesLeft--;
        }

        System.out.println(photonsPassed);
        System.out.println(totalWeight);
        return photons;
    }
}
