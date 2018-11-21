package main.objects;

import scatterFunctions.ScatterFunction;

public class GasLayer {

    private double geometricalDepth;
    private double k;
    private double opticalDepth;
    private double absorption;
    private double g;
    private ScatterFunction scatterFunction;

    public GasLayer(double geometricalDepth, double k, double g, ScatterFunction scatterFunction){
        this.geometricalDepth = geometricalDepth;
        this.k = k;
        this.opticalDepth = k/geometricalDepth;
        this.absorption = this.opticalDepth/2;
        this.g = g;
        this.scatterFunction = scatterFunction;
    }

    public double getGeometricalDepth() {
        return geometricalDepth;
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
}
