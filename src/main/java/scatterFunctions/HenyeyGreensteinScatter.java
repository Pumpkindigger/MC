package scatterFunctions;
import main.MyRandom;

public class HenyeyGreensteinScatter extends ScatterFunction{

    public double calculateAngle(double g){
        return (1.0 / (2.0 * g)) * (1.0 + Math.pow(g, 2.0) - Math.pow((1.0 - Math.pow(g, 2.0)) / (1.0 - g + 2.0 * g * MyRandom.random()), 2.0));
    }
}
