package main.objects;

import main.Constants;
import org.jzy3d.maths.Coord3d;

public class Photon {

    //Position parameters
    private double x;
    private double y;
    private double z;

    private int currentLayer;

    //Directional parameters, this is the direction cosine
    private double v_x;
    private double v_y;
    private double v_z;

    //Other data
    private double weight;
    private int nrBounced;
    private boolean eliminated;
    private boolean madeIt;

    //Constructor including all the fields
    public Photon(double x, double y, double z, int currentLayer, double v_x, double v_y, double v_z, double weight, int nrBounced, boolean eliminated) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.currentLayer = currentLayer;
        this.v_x = v_x;
        this.v_y = v_y;
        this.v_z = v_z;
        this.weight = weight;
        this.nrBounced = nrBounced;
        this.eliminated = eliminated;
        this.madeIt = false;
    }

    //Default constructor for 1D analysis, only in the z direction
    public Photon(int currentLayer) {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.currentLayer = currentLayer;
        this.v_x = 0;
        this.v_y = 0;
        this.v_z = 1;
        this.weight = Constants.startingWeight;
        this.nrBounced = 0;
        this.eliminated = false;
        this.madeIt = false;
    }

    //Constructor for 2D analysis for bend gas layer
    public Photon(int currentLayer, int radius, int omega) {
        this.x = -Math.sin(Math.toRadians(omega))*radius;
        this.y = Math.cos(Math.toRadians(omega))*radius;
        this.z = 0.0;
        this.currentLayer = currentLayer;
        this.v_x = 1.0;
        this.v_y = 0.0;
        this.v_z = 0.0;
        this.weight = Constants.startingWeight;
        this.nrBounced = 0;
        this.eliminated = false;
        this.madeIt = false;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int layer) {
        currentLayer = layer;
    }

    public double getZ() {
        return this.z;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isEliminated() {
        return this.eliminated;
    }

    public void eliminate() {
        this.eliminated = true;
    }

    public void madeIt() {
        if(!this.eliminated){
            this.madeIt =true;
            this.eliminate();
        }
    }

    public boolean getMadeIt(){
        return this.madeIt;
    }

    public void updatePosition(double delta) {
        this.x = x + delta * v_x;
        this.y = y + delta * v_y;
        this.z = z + delta * v_z;
    }

    //Calculate the new cosine angles for the velocity
    public void updateAngle(double cosTheta, double polarAngle) {
        //Some values so that we only have to calculate them once:
        //cos^2 + sin^2 = 1
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
        double cosPolar = Math.cos(polarAngle);
        double sinPolar = Math.sin(polarAngle);

        //Special case for v_z = 1
        if (v_z == 1.0) {
            v_x = sinTheta * cosPolar;
            v_y = sinTheta * sinPolar;
            v_z = cosTheta;
        } else {
            //Special case for v_z = -1
            if (v_z == -1.0) {
                v_x = sinTheta * cosPolar;
                v_y = -sinTheta * sinPolar;
                v_z = -cosTheta;
            } else {
                //Calculate new values, default case
                double v_xNew = (sinTheta * (v_x * v_y * cosPolar - v_y * sinPolar) / (Math.sqrt(1.0 - v_z * v_z))) + v_x * cosTheta;
                double v_yNew = (sinTheta * (v_y * v_z * cosPolar + v_x * sinPolar) / (Math.sqrt(1.0 - v_z * v_z))) + v_y * cosTheta;
                double v_zNew = -((Math.sqrt(1.0 - v_z * v_z)) * sinTheta * cosPolar) + v_z * cosTheta;

                //Set the new values
                v_x = v_xNew;
                v_y = v_yNew;
                v_z = v_zNew;
            }
        }
    }


    //This is a temporary elimination function which should be replaced at some point.
    public void checkElimination() {
        if (this.weight < Constants.eliminationConstant) {
            //System.out.println("main.objects.Photon eliminated");
            this.weight = 0;
            this.eliminate();
        }
    }

    public Coord3d toCoordinate() {
        Coord3d res = new Coord3d();
        res.set((float) this.x, (float) this.y, (float) this.z);
        return res;
    }

    //Limits the dimensions of the photon
    public void limitDimensions1D(double size) {
        if (x < -size) {
            x = -size;
        } else if (x > size) {
            x = size;
        }
        if (y < -size) {
            y = -size;
        } else if (y > size) {
            y = size;
        }
        //Z is already bounded when it exits the gas layer
    }

    /**
     * This mehtod calculates the radius of the center of mars to the photon
     *
     * @return the radius of the center of mars to the photon
     */
    public double calculateR() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }


    /**
     * Calculates the angle of the photon to the center of mars (located at 0,0)
     * It goes in a COUNTER CLOCKWISE manner, with North = 0*, East = 90* etc.
     * @return the angle between the photon and Northpole of mars with the center of mars being (0,0)
     */
    public double calculateOmega() {
        double thetaClockwise =  Math.atan2(this.x, -this.y);
        if (thetaClockwise < 0.0){
            thetaClockwise += 2*Math.PI;
        }
        double theta = Math.toDegrees(thetaClockwise) - 180;
        if (theta < 0.0){
            theta += 360;
        }
        return theta;
    }


    /**
     * This method will return a value indicative of if the photon is in the gaslayer or not.
     * This method will return a 0 if the photon exited the gaslayer on the "high" side of the gaslayer
     * This method will return a 1 if the photon is still in the gaslayer.
     * This method will return a 2 if the photon exited the gaslayer on the "low" side of the gaslayer
     * This method will return a 3 if the photon exited the gaslayer on the "left" side of the gaslayer
     * This method will return a 4 if the photon exited the gaslayer on the "right" side of the gaslayer
     *
     * NOTE: if the photon is both outside of the gaslayer on either the left or right side, but as well on the high or low side,
     * the high or low side will have priority over the side.
     *
     * If this method returned a -1 something went wrong
     * @return
     */
    public int isInGasLayer(GasLayerBend2D gaslayer) {
        double r = this.calculateR();
        double omega = this.calculateOmega();
        //Check if the radius of the photon is within the radius of the gaslayer
        if (gaslayer.getInnerR() < r && r < gaslayer.getOuterR()) {
            //Check if the angle of the photon is within the angles of the gaslayer
            if (gaslayer.getRightOmega() < gaslayer.getLeftOmega()) {
                if (gaslayer.getRightOmega() < omega && r < gaslayer.getLeftOmega()) {
                    return 1;
                }
            }
            else {
                if ((omega < gaslayer.getRightOmega() && omega < gaslayer.getLeftOmega()) || (omega > gaslayer.getRightOmega() && omega > gaslayer.getLeftOmega())){
                    return 1;
                }
            }
        }
        //Check if the photon has exited on the low side of the gaslayer
        if (r < gaslayer.getInnerR()) {
            return 2;
        }
        //Check if the photon has exited on the high side of the gaslayer
        if (r > gaslayer.getOuterR()){
            return 0;
        }
        return checkAngle(gaslayer, omega);
    }

    /**
     * NOTE: THIS METHOD HAS NOT THOROUGHLY BEEN TESTED, BUGS MIGHT BE PRESENT
     *
     * This method will return a 3 if the photon exited the gaslayer on the "left" side of the gaslayer
     * This method will return a 4 if the photon exited the gaslayer on the "right" side of the gaslayer
     * @return integer representative of the photons exit position
     */
    public int checkAngle(GasLayerBend2D gaslayer, double omega) {
        //TODO check if this is correct
        // The opposite angle here is the the angle 180 degrees from the angle in the middle of the two omega angles
        double oppositeAngle;
        double leftOmega = gaslayer.getLeftOmega();
        double rightOmega = gaslayer.getRightOmega();
        //Check if there is a wraparound
        if (leftOmega < rightOmega) {
            //If no wraparound, then the opposite angle is the half of the two angles, plus 180 degrees % 360
            oppositeAngle = ((leftOmega + rightOmega) / 2 + 180) % 360;
            //If the opposite angle is bigger than the the left omega, we use the left omega to check the location
            if (oppositeAngle > leftOmega){
                //If the photon is in between the left omega and the opposite angle, it has exited the gaslayer on the left
                if (omega < oppositeAngle && omega > leftOmega){
                    return 3;
                }
                //If the photon is not in between those angles, it exited the gaslayer on the right
                else return 4;
            }
            else {
                if (omega > oppositeAngle && omega < rightOmega){
                    return 3;
                }
                else return 4;
            }
        }
        else{
            //If there is a wraparound the opposite angle is defined as leftomega + rightomega / 2
            // (Note: 360 is redundant I think, but I left it just in case)
            oppositeAngle = ((leftOmega + rightOmega) / 2);
            //If the photon is in between the opposite angle and the left omega, then it has exited on the left
            if (omega < oppositeAngle && omega > leftOmega){
                return 3;
            }
            //If the photon is not in between those angles, it exited the gaslayer on the right
            else return 4;
        }
    }

}
