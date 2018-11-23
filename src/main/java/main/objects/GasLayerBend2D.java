package main.objects;

import scatterFunctions.ScatterFunction;

public class GasLayerBend2D extends GasLayerAbstract{

    private double outerR;
    private double innerR;
    private double leftOmega;
    private double rightOmega;

    public GasLayerBend2D(double outerR, double innerR, double leftOmega, double rightOmega, double k, double g, ScatterFunction scatterFunction) {
        super(k, g, scatterFunction);
        setGeometricalDepth(outerR - innerR);
        setOpticalDepth(k/getGeometricalDepth());
        setAbsorption(getOpticalDepth()/2);
        this.outerR = outerR;
        this.innerR = innerR;
        this.leftOmega = leftOmega;
        this.rightOmega = rightOmega;
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

}
