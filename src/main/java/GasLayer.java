public class GasLayer {

    private double geometricalDepth;
    private double k;
    private double opticalDepth;
    private double absorption;
    private double g;

    public GasLayer(double geometricalDepth, double k, double g){
        this.geometricalDepth = geometricalDepth;
        this.k = k;
        this.opticalDepth = k/geometricalDepth;
        this.absorption = this.opticalDepth/2;
        this.g = g;
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
}
