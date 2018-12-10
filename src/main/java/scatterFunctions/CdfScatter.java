package scatterFunctions;

import main.DataBank;
import main.MyRandom;

public class CdfScatter extends ScatterFunction{

    private DataBank dataBank;

    public CdfScatter(String fileName){
        this.dataBank = new DataBank(fileName);
    }

    @Override
    public double calculateAngle(double g) {
        return dataBank.getAngle(MyRandom.random()*dataBank.maxValue());
    }
}
