package scatterFunctions;

import main.MyRandom;

/**
 * TODO: implement rayleigh scattering
 * http://orbit.dtu.dk/files/6314521/3D9A1d01.pdf
 */
public class RayleighScatter extends ScatterFunction {

    public double calculateAngle(double omega) {
        double cosTheta;
        do {
            cosTheta = 2 * MyRandom.random() - 1;
        }
        while (MyRandom.random() > 0.5 * (1 + cosTheta*cosTheta));
        return cosTheta;
    }
}
