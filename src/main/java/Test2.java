import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

import java.util.ArrayList;

public class Test2 {

    public static void main(String[] args) {
        int nrPhotons = 10000;
        int layers = 1;

        //Initialize the gas layer
        GasLayer gasLayer = new GasLayer(100, 1, 0);

        System.out.println("Optical depth: " + gasLayer.getOpticalDepth());

        ArrayList<Photon> photons = new ArrayList<Photon>(nrPhotons);

        //Create all the protons
        for (int i = 0; i < nrPhotons; i++) {
            photons.add(new Photon(layers));
        }

        int photonsPassed = 0;
        int nrBouncesLeft = Constants.maximumBounces;
        while (nrBouncesLeft > 0) {

            // TODO: Find out if the stepsize is the same of different for each packet.
            // Stepsize = -ln(random)/opticalDepth
            //double stepSize = -Math.log(MyRandom.random())/gasLayer.getOpticalDepth();


            //Loop over all photon packets
            for (Photon photon : photons) {
                //If the weight of the proton is 0, then we no longer have to perform operations on it.
                if (photon.isEliminated()) {
                    continue;
                }
                //Get a random stepsize which is based on the optical depth
                double stepSize = -Math.log(MyRandom.random()) / gasLayer.getOpticalDepth();

                //Update the position of the photon using the stepsize.
                photon.updatePosition(stepSize);

                //newWeight = oldWeight - (absorption/opticalDepth)*oldWeight
                photon.setWeight(photon.getWeight() - photon.getWeight() * gasLayer.getAbsorption() / gasLayer.getOpticalDepth());

                double cosTheta;
                double g = gasLayer.getG();
                //If g = 0, we have isotropic scattering and can thus pick a random angle between -1 and 1
                if (g == 0) {
                    cosTheta = 1.0 - 2.0 * MyRandom.random();
                } else {
                    //Calculate scatter angle using Henyey-Greenstein phase function
                    cosTheta = (1.0 / (2.0 * g)) * (1.0 + Math.pow(g, 2.0) - Math.pow((1.0 - Math.pow(g, 2.0)) / (1.0 - g + 2.0 * g * MyRandom.random()), 2.0));
                }
                double polarAngle = 2.0 * Math.PI * MyRandom.random();

                //Update the new cosine angles of the velocities
                photon.updateAngle(cosTheta, polarAngle);

                //Check if the photon has passed the gas layer, if so, set its weight to zero
                if (photon.getZ() > gasLayer.getGeometricalDepth()) {
                    photonsPassed++;
                    photon.setZ(gasLayer.getGeometricalDepth());
                    photon.eliminate();
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

        Coord3d[] coordinates = new Coord3d[photons.size()];
        org.jzy3d.colors.Color[] colors = new org.jzy3d.colors.Color[photons.size()];

        //For each photon, first limit its dimensions and then transform it into a coordinate
        for (int i = 0; i < photons.size(); i++) {
            photons.get(i).limitDimensions(gasLayer.getGeometricalDepth()*2);
            coordinates[i] = photons.get(i).toCoordinate();
            //If the photon has a weight of 0, set the color to red
            if (photons.get(i).getWeight() == 0){
                colors[i] = org.jzy3d.colors.Color.RED;
            }
            //If weight > 0, then set color to blue
            else{
                colors[i] = Color.BLUE;
            }
        }

        //This plotter will on init draw the plot with the given coordinates
        Plotter3D plotter3D = new Plotter3D(coordinates, colors);

        //Calculate the expected number of photons which will pass through the gaslayer using the Lambert-Beer Law
        double expected = nrPhotons * Math.exp(-gasLayer.getOpticalDepth());

        System.out.println("Nr of photons passed by formula: " + expected);

        //Calculate the error rate of my model vs the theoretical number
        double error = expected / photonsPassed;
        System.out.println("Error factor: " + error);

    }
}
