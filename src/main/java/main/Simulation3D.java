package main;

import main.objects.GasLayer;
import main.objects.GasLayerAbstract;
import main.objects.Photon;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import scatterFunctions.RayleighScatter;

import java.util.ArrayList;

public class Simulation3D {

    public static void main(String[] args) {
        int nrPhotons = 10000;
        int layers = 1;

        //Initialize the gas layer
        GasLayer gasLayer = new GasLayer(100, 5, 0.0, new RayleighScatter());

        System.out.println("Optical depth: " + gasLayer.getOpticalDepth());

        ArrayList<Photon> photons = new ArrayList<Photon>(nrPhotons);

        //Create all the protons
        for (int i = 0; i < nrPhotons; i++) {
            photons.add(new Photon(layers));
        }

        int photonsPassed = 0;
        int nrBouncesLeft = Constants.maximumBounces;
        double totalWeight = 0;
        while (nrBouncesLeft > 0) {

            //Loop over all photon packets
            for (Photon photon : photons) {
                //If the weight of the proton is 0, then we no longer have to perform operations on it.
                if (photon.isEliminated()) {
                    continue;
                }
                performStep(gasLayer, photon);

                //Check if the photon has passed the gas layer, if so, set its weight to zero
                if (photon.getZ() > gasLayer.getGeometricalDepth()) {
                    photonsPassed++;
                    totalWeight += photon.getWeight();
                    photon.setZ(gasLayer.getGeometricalDepth());
                    photon.madeIt();
                }
                //Check if the photon has exited the gas layer on the opposite site
                if (photon.getZ() < 0) {
                    photon.setZ(0);
                    photon.eliminate();
                }

                photon.checkElimination();
            }
            nrBouncesLeft--;
        }

        System.out.println(photonsPassed);
        System.out.println(totalWeight);

        plotResult(nrPhotons, gasLayer, photons);

    }

    public static void plotResult(int nrPhotons, GasLayerAbstract gasLayer, ArrayList<Photon> photons) {
        Coord3d[] coordinates = new Coord3d[photons.size()];
        Color[] colors = new Color[photons.size()];

        //For each photon, first limit its dimensions and then transform it into a coordinate
        for (int i = 0; i < photons.size(); i++) {
            photons.get(i).limitDimensions1D(gasLayer.getGeometricalDepth() * 2);
            coordinates[i] = photons.get(i).toCoordinate();
            //If the photon has a weight of 0, set the color to red
            if (!photons.get(i).getMadeIt()) {
                colors[i] = Color.RED;
            }
            //If weight > 0, then set color to blue
            else {
                colors[i] = Color.BLUE;
            }
        }

        //This plotter will on init draw the plot with the given coordinates
        Plotter3D plotter3D = new Plotter3D(coordinates, colors);

        //Calculate the expected number of photons which will pass through the gaslayer using the Lambert-Beer Law
        double expected = nrPhotons * Math.exp(-gasLayer.getOpticalDepth());

        System.out.println("Nr of photons passed by formula: " + expected);

        //Calculate the error rate of my model vs the theoretical number
        //double error = expected / photonsPassed;
        //System.out.println("Error factor: " + error);
    }

    /**
     * Performs 1 step of a single photon through the atmosphere
     *
     * @param gasLayer The gaslayer through which the photon passes
     * @param photon   The photon which passes through the gaslayer
     * @return The old coordinate of the photon.
     */
    public static void performStep(GasLayerAbstract gasLayer, Photon photon) {
        //Get a random stepsize which is based on the optical depth
        double stepSize = -Math.log(MyRandom.random()) / gasLayer.getOpticalDepth();

        //System.out.println(stepSize);
        //Update the position of the photon using the stepsize.
        photon.updatePosition(stepSize);

        //newWeight = oldWeight - (absorption/opticalDepth)*oldWeight
        photon.setWeight(photon.getWeight() - photon.getWeight() * gasLayer.getAbsorption() / gasLayer.getOpticalDepth());

        double cosTheta;
        double g = gasLayer.getG();
        //If g = 0, we have isotropic scattering and can thus pick a random angle between -1 and 1

        //Calculate scatter angle using the phase function of gaslayer
        cosTheta = gasLayer.getScatterFunction().calculateAngle(g);

        double polarAngle = 2.0 * Math.PI * MyRandom.random();

        //Update the new cosine angles of the velocities
        photon.updateAngle(cosTheta, polarAngle);
    }
}
