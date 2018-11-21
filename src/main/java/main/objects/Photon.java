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
    }

    //Default constructor for 1D analysis, only in the z direction
    public Photon(int currentLayer){
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.currentLayer = currentLayer;
        this.v_x = 0;
        this.v_y = 0;
        this.v_z = 1;
        this.weight = 1;
        this.nrBounced = 0;
        this.eliminated = false;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int layer){ currentLayer = layer; }

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

    public boolean isEliminated(){return this.eliminated;}
    public void eliminate() {
        this.eliminated = true;
    }

    public void updatePosition(double delta) {
        this.x = x + delta*v_x;
        this.y = y + delta*v_y;
        this.z = z + delta*v_z;
    }

    //Calculate the new cosine angles for the velocity
    public void updateAngle(double cosTheta, double polarAngle){
        //Some values so that we only have to calculate them once:
        //cos^2 + sin^2 = 1
        double sinTheta = Math.sqrt(1.0-cosTheta*cosTheta);
        double cosPolar = Math.cos(polarAngle);
        double sinPolar = Math.sin(polarAngle);

        //Special case for v_z = 1
        if(v_z == 1.0){
            v_x = sinTheta*cosPolar;
            v_y = sinTheta*sinPolar;
            v_z = cosTheta;
        }
        else{
            //Special case for v_z = -1
            if (v_z == -1.0){
                v_x = sinTheta*cosPolar;
                v_y = -sinTheta*sinPolar;
                v_z = -cosTheta;
            }
            else {
                //Calculate new values, default case
                double v_xNew = (sinTheta*(v_x*v_y*cosPolar - v_y*sinPolar) / (Math.sqrt(1.0-v_z*v_z))) + v_x*cosTheta;
                double v_yNew = (sinTheta*(v_y*v_z*cosPolar + v_x*sinPolar) / (Math.sqrt(1.0-v_z*v_z))) + v_y*cosTheta;
                double v_zNew = -((Math.sqrt(1.0-v_z*v_z)) * sinTheta * cosPolar) + v_z*cosTheta;

                //Set the new values
                v_x = v_xNew;
                v_y = v_yNew;
                v_z = v_zNew;
            }
        }
    }


    //This is a temporary elimination function which should be replaced at some point.
    public void checkElimination(){
        if (this.weight < Constants.eliminationConstant){
            //System.out.println("main.objects.Photon eliminated");
            this.weight = 0;
            this.eliminate();
        }
    }

    public Coord3d toCoordinate() {
        Coord3d res = new Coord3d();
        res.set((float)this.x, (float)this.y, (float)this.z);
        return res;
    }

    //Limits the dimensions of the photon
    public void limitDimensions(double size) {
        if (x < -size){
            x = -size;
        }
        else if(x > size){
            x = size;
        }
        if (y < -size){
            y = -size;
        }
        else if(y > size){
            y = size;
        }
        //Z is already bounded when it exits the gas layer
    }


}
