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
        GasLayerBend2D gasLayer1 = new GasLayerBend2D(10, 5, 90, 0, 10, 1, new RayleighScatter());
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

                //TODO: Backtrack
                photon.backTrack(gasLayer);

                //TODO perform correct position adjustments when photon is no longer in gaslayer
                //Check if the photon has passed the gas layer, if so, set its weight to zero
                PositionEnum position = PositionEnum.ABOVE;//photon.isInGasLayer(gasLayer);
                switch (position) {
                    //If 0, then the photon has exited the gaslayer on the high side, we for now eliminate the photon
                    case ABOVE:
                        photon.eliminate();
                        //TODO limit the dimensions of the photon to the outer layer of the gaslayer
                        break;
                    //If 1, then the photon is still in the gaslayer, so we only have to check if we should eliminate the photon
                    case INSIDE:
                        photon.checkElimination();
                        break;
                    //If 2, then the photon has exited the gaslayer on the low side, we check if it should be eliminated and set the current layer to += -1
                    case OUTSIDE:
                        //TODO limit the dimensions of the photon to the outer layer of the gaslayer
                        photon.checkElimination();
                        photon.setCurrentLayer(photon.getCurrentLayer()-1);
                        totalWeight += photon.getWeight();
                        photonsPassed++;
                        photon.madeIt();
                        break;

                    //TODO: implement these cases correctly
//                    case 3:
//                        photon.eliminate();
//                        break;
//                    case 4:
//                        photon.eliminate();
//                        break;
//                    default:
//                        System.out.println(position);
//                        throw new IllegalStateException("Photon is neither in a gaslayer, nor has exited it");
                }
            }
            nrBouncesLeft--;
        }

        System.out.println(photonsPassed);
        System.out.println(totalWeight);
        return photons;
    }
}
