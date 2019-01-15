package main;

public final class Constants {

    private Constants(){
    }
    //This constant is used when we want a set maximum number of bounces
    public static final int maximumBounces = 500;
    //If the weight of a photon package is lower than this constant, it should be eliminated
    public static final double eliminationConstant = 0.1;
    //Each photon packet starts with this weight
    public static final double startingWeight = 1000000;
    //Used in the final model
    public static final int radiusMars = 3390;
    //An error margin for the inaccuracy of doubles
    public static final double epsilon = 0.000000001;
}
