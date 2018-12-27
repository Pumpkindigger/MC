package main.objects;

import scatterFunctions.ScatterFunction;

public class GasLayer extends GasLayerAbstract {


    /**
     * Constructor for a 3D non-bend gaslayer
     * @param geometricalDepth the geometrical depth of the gaslayer
     * @param k the density coefficient of the gaslayer
     * @param g the coefficient used by the scatterfuntion
     * @param scatterFunction the scatterfunction
     */
    public GasLayer(double geometricalDepth, double k, double g, ScatterFunction scatterFunction){
        super(k, g, scatterFunction);
        setGeometricalDepth(geometricalDepth);
        setOpticalDepth(k/getGeometricalDepth());
        setAbsorption(getOpticalDepth()/2);
    }

}
