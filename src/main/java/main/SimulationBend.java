package main;

import main.objects.GasLayerBend2D;
import main.objects.Photon;
import scatterFunctions.RayleighScatter;

import java.util.ArrayList;

import static main.Simulation3D.plotResult;

public class SimulationBend {

    public static void main(String[] args) {
        int nrPhotons = 50000;
        int layers = 1;

        //Initialize the arraylist of gaslayers
        ArrayList<GasLayerBend2D> gasLayers = new ArrayList<GasLayerBend2D>(layers);
        //Initialize the seperate gas layer and add them to the list
        GasLayerBend2D gasLayer1 = new GasLayerBend2D(10, 5, 180, 200, 0.01, 1, new RayleighScatter());
        //GasLayerBend2D gasLayer2 = new GasLayerBend2D(7.5, 5, 90, 0, 5, 0.0, new HenyeyGreensteinScatter());
        gasLayers.add(gasLayer1);
        //gasLayers.add(gasLayer2);

        ArrayList<Photon> photons = new ArrayList<Photon>(nrPhotons);

        //Create all the protons
        for (int i = 0; i < nrPhotons; i++) {
            photons.add(new Photon(layers, 10, 45));
        }

        for (GasLayerBend2D gasLayer : gasLayers) {
            photons = simulateOneGaslayer(gasLayer, photons);
            plotResult(nrPhotons, gasLayer, photons);
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
