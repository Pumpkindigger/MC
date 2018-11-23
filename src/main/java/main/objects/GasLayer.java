package main.objects;

import scatterFunctions.ScatterFunction;

public class GasLayer extends GasLayerAbstract {


    public GasLayer(double geometricalDepth, double k, double g, ScatterFunction scatterFunction){
        super(k, g, scatterFunction);
        setGeometricalDepth(geometricalDepth);
        setOpticalDepth(k/getGeometricalDepth());
        setAbsorption(getOpticalDepth()/2);
    }

}
