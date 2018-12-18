package main.objects;

import scatterFunctions.ScatterFunction;

public class GasLayerAbstract {

    private double k;
    private double g;
    private ScatterFunction scatterFunction;
    private double opticalDepth;
    private double geometricalDepth;
    private double absorption;

    public GasLayerAbstract(double k, double g, ScatterFunction scatterFunction) {
        this.k = k;
        this.g = g;
        this.scatterFunction = scatterFunction;
    }

    public double getOpticalDepth(){
        return opticalDepth;
    }

    public double getG(){
        return g;
    }

    public ScatterFunction getScatterFunction() {
        return scatterFunction;
    }

    public void setGeometricalDepth(double geometricalDepth) {
        this.geometricalDepth = geometricalDepth;
    }

    public void setOpticalDepth(double opticalDepth) {
        this.opticalDepth = opticalDepth;
    }

    public double getGeometricalDepth() {
        return geometricalDepth;
    }

    public double getAbsorption() {
        return absorption;
    }

    public void setAbsorption(double absorption) {
        this.absorption = absorption;
    }

    public void setK(double k){
        this.k = k;
        this.opticalDepth = k/getGeometricalDepth();
    }
}
