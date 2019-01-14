package scatterFunctions;

import main.MyRandom;

/**
 * http://orbit.dtu.dk/files/6314521/3D9A1d01.pdf
 * Baranoski’s rejection sampling
 */
public class RayleighScatter extends ScatterFunction {

    public double calculateAngle(double omega) {
        double cosTheta;
        do {
            cosTheta = Math.cos(Math.PI*MyRandom.random());
        }
        while (MyRandom.random() > 9/(4*Math.sqrt(6)) * (1+cosTheta*cosTheta) * Math.pow(1-cosTheta*cosTheta, 0.5));
        return cosTheta;
    }
}
