package main.objects;

import scatterFunctions.ScatterFunction;

public class GasLayerBend2D {

    private double outerR;
    private double innerR;
    private double leftOmega;
    private double rightOmega;
    private double geometricalDepth;
    private double k;
    private double opticalDepth;
    private double absorption;
    private double g;
    private ScatterFunction scatterFunction;

    public GasLayerBend2D(double outerR, double innerR, double leftOmega, double rightOmega, double k, double g, ScatterFunction scatterFunction) {
        this.outerR = outerR;
        this.innerR = innerR;
        this.leftOmega = leftOmega;
        this.rightOmega = rightOmega;
        this.k = k;
        this.g = g;
        this.geometricalDepth = outerR - innerR;
        this.opticalDepth = k/geometricalDepth;
        this.absorption = opticalDepth/2;
        this.scatterFunction = scatterFunction;
    }

    public double getOpticalDepth() {
        return opticalDepth;
    }

    public double getAbsorption() {
        return absorption;
    }

    public double getG() {
        return g;
    }

    public ScatterFunction getScatterFunction() {
        return scatterFunction;
    }

    public double getOuterR() {
        return outerR;
    }

    public double getInnerR() {
        return innerR;
    }

    public double getLeftOmega() {
        return leftOmega;
    }

    public double getRightOmega() {
        return rightOmega;
    }

    public double getGeometricalDepth() {
        return geometricalDepth;
    }
}
