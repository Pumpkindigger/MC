package main.objects;

import main.Constants;
import main.Coordinate;
import main.MyRandom;
import main.PositionEnum;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Pair;

import java.util.ArrayList;

public class Photon {

    //Position parameters
    private Coordinate oldCoordinate;
    private Coordinate currentCoordinate;

    private int currentLayer;
    private int horizontalIndex;

    //Directional parameters, this is the direction cosine
    private double v_x;
    private double v_y;
    private double v_z;

    //Other data
    private double weight;
    private boolean eliminated;
    private boolean madeIt;


    /**
     * Constructor used for Simulation3D class
     * @param currentLayer the starting layer of the photon
     */
    public Photon(int currentLayer) {
        this.oldCoordinate = null;
        this.currentCoordinate = new Coordinate(0.0, 0.0, 0.0);
        this.currentLayer = currentLayer;
        this.horizontalIndex = 0;
        this.v_x = 0;
        this.v_y = 0;
        this.v_z = 1;
        this.weight = Constants.startingWeight;
        this.eliminated = false;
        this.madeIt = false;
    }

    /**
     * Constructor used when we want to specify an x, y, and z coordinate.
     * Mostly used for testing
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Photon(double x, double y, double z, boolean testing) {
        this.oldCoordinate = null;
        this.currentCoordinate = new Coordinate(x, y, z);
        this.v_x = 0;
        this.v_y = 0;
        this.v_z = 1;
        this.weight = Constants.startingWeight;
        this.eliminated = false;
        this.madeIt = false;
    }

    public int getHorizontalIndex() {
        return horizontalIndex;
    }

    public void setHorizontalIndex(int horizontalIndex) {
        this.horizontalIndex = horizontalIndex;
    }

    /**
     * Constructor used in SimilationBend
     * z coordinate will be set to 0
     * @param currentLayer the starting layer of the photon
     * @param radius the initial radius of the photon
     * @param omega the angle counterclockwise to the north (positive y axis)
     */
    public Photon(int currentLayer, int radius, int omega) {
        this.oldCoordinate = null;
        this.currentCoordinate = new Coordinate(-Math.sin(Math.toRadians(omega)) * radius, Math.cos(Math.toRadians(omega)) * radius, 0.0);
        this.currentLayer = currentLayer;
        this.horizontalIndex = 0;
        this.v_x = 1.0;
        this.v_y = 0.0;
        this.v_z = 0.0;
        this.weight = Constants.startingWeight;
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
        return this.currentCoordinate.getZ();
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setZ(double z) {
        this.currentCoordinate.setZ(z);
    }

    public void setOldCoordinate(Coordinate oldCoordinate) {
        this.oldCoordinate = oldCoordinate;
    }

    public Coordinate getOldCoordinate() {
        return oldCoordinate;
    }

    public Coordinate getCurrentCoordinate() {
        return currentCoordinate;
    }

    public void setCurrentCoordinate(Coordinate currentCoordinate) {
        this.currentCoordinate = currentCoordinate;
    }

    public boolean isEliminated() {
        return this.eliminated;
    }

    public void eliminate() {
        this.eliminated = true;
    }

    /**
     * Sets the photon's madeIt field to true, and eliminates the photon
     */
    public void madeIt() {
        checkElimination();
        if (!this.eliminated) {
            this.madeIt = true;
            this.eliminate();
        }
    }

    public boolean getMadeIt() {
        return this.madeIt;
    }

    /**
     * Updates the current coordinate
     * @param delta the timestep to take
     */
    public void updatePosition(double delta) {
        oldCoordinate = new Coordinate(currentCoordinate.getX(), currentCoordinate.getY(), currentCoordinate.getZ());
        this.currentCoordinate.setX(oldCoordinate.getX() + delta * v_x);
        this.currentCoordinate.setY(oldCoordinate.getY() + delta * v_y);
        this.currentCoordinate.setZ(oldCoordinate.getZ() + delta * v_z);
    }

    /**
     * Calculate the new cosine angles for the velocity
     * @param cosTheta the angle from the computed from the scattering in the gaslayer
     * @param polarAngle the polar angle
     */
    public void updateCosineAngles(double cosTheta, double polarAngle) {
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
                double v_xNew = (sinTheta * (v_x * v_z * cosPolar - v_y * sinPolar) / (Math.sqrt(1.0 - v_z * v_z))) + v_x * cosTheta;
                double v_yNew = (sinTheta * (v_y * v_z * cosPolar + v_x * sinPolar) / (Math.sqrt(1.0 - v_z * v_z))) + v_y * cosTheta;
                double v_zNew = -(Math.sqrt(1.0 - v_z * v_z)) * sinTheta * cosPolar + v_z * cosTheta;

                //Set the new values
                v_x = v_xNew;
                v_y = v_yNew;
                v_z = v_zNew;
            }
        }
    }

    /**
     * Updates the weight of the photon
     * @param gaslayer the gaslayer from which to get the absorption factor
     */
    public void updateWeight(GasLayerAbstract gaslayer) {
        this.weight = (this.weight - this.weight * gaslayer.getAbsorption());
    }

    /**
     * Updates the angles of the photon
     * @param gaslayer the gaslayer from which to get the the scatterfuntion
     */
    public void updateAngle(GasLayerAbstract gaslayer) {
        double cosTheta;
        double g = gaslayer.getG();
        //Calculate scatter angle using the phase function of gaslayer
        cosTheta = gaslayer.getScatterFunction().calculateAngle(g);

        double polarAngle = 2.0 * Math.PI * MyRandom.random();

        updateCosineAngles(cosTheta, polarAngle);
    }

    //This is a temporary elimination function which should be replaced at some point.
    public void checkElimination() {
        if (this.weight < Constants.eliminationConstant) {
            this.weight = 0;
            this.eliminate();
        }
    }

    /**
     * Turns the x, y, and z coordinate of the photon into a Coord3d
     * @return a Coord3d representation of the position of the photon
     */
    public Coord3d toCoordinate() {
        Coord3d res = new Coord3d();
        res.set((float) this.currentCoordinate.getX(), (float) this.currentCoordinate.getY(), (float) this.currentCoordinate.getZ());
        return res;
    }

    //Limits the dimensions of the photon
    //This is only called when plotting the photons when the simulation is done
    public void limitDimensions1D(double size) {
        if (currentCoordinate.getX() < -size) {
            currentCoordinate.setX(-size);
        } else if (currentCoordinate.getX() > size) {
            currentCoordinate.setX(size);
        }
        if (currentCoordinate.getY() < -size) {
            currentCoordinate.setY(-size);
        } else if (currentCoordinate.getY() > size) {
            currentCoordinate.setY(size);
        }
        //Z is already bounded when it exits the gas layer
    }

    /**
     * This mehtod calculates the radius of the center of mars to the photon in a plane.
     * This means the Z-Coordinate is left out of the formula
     *
     * @return the radius of the center of mars to the photon
     */
    public double calculateR(Coordinate coordinate) {
        return Math.sqrt(coordinate.getX() * coordinate.getX() + coordinate.getY() * coordinate.getY());
    }


    /**
     * Calculates the angle of the photon to the center of mars (located at 0,0)
     * It goes in a COUNTER CLOCKWISE manner, with North = 0*, East = 90* etc.
     *
     * @return the angle between the photon and Northpole of mars with the center of mars being (0,0)
     */
    public double calculateOmega(Coordinate coordinate) {
        double thetaClockwise = Math.atan2(coordinate.getX(), -coordinate.getY());
        if (thetaClockwise < 0.0) {
            thetaClockwise += 2 * Math.PI;
        }
        double theta = Math.toDegrees(thetaClockwise) - 180;
        if (theta < 0.0) {
            theta += 360;
        }
        return theta;
    }


    /**
     * Checks if the photon is inside 2 angles
     * @param left the left angle which to check to
     * @param right the right angle which to check to
     * @param omega the angle of the photon
     * @return
     */
    public boolean checkInsideAngle(double left, double right , double omega) {
        if(left == 360 && right == 0){
            return true;
        }
        if (right < left) {
            return right < omega && omega < left;
        } else {
            return (omega < right && omega < left) || (omega > right && omega > left);
        }
    }

    /**
     * This method will check if the photon has passed a boundary of a gaslayer and put the currentCoordinate to the edge of the layer when it .
     * It will check the if of a line which goes through the old and new coordinate of the photon crosses the boundaries of the gaslayer in a 2D plane.
     *
     * @param gaslayer the gaslayer which the photon is currently traveling through
     */
    public PositionEnum backTrack(GasLayerBend2D gaslayer) {
        //First calculate all the intersection points
        ArrayList<Coordinate> intersections = getIntersectionPoints(gaslayer);

        //In order to see whether the photon has actually left the gaslayer,
        // we check for all of the intersection points lays in between the old and new coordinates of the photon.
        // If this is the case at least once, then the photon has left the gaslayer
        ArrayList<Pair<Double, Coordinate>> distancesIntersections = new ArrayList<Pair<Double, Coordinate>>();
        for (Coordinate intersection : intersections) {
            //If the intersection is (0,0) it means its a filler coordinate and does not have to be checked
            if (intersection.getY() == 0.0 && intersection.getX() == 0.0) {
                continue;
            }
            //If the intersection is in between the old and new position, the photon has passed the intersection
            if (checkInBetween(intersection)) {
                //Calculate the distance between the intersection point and the old position
                double distance = calculateXYDistance(oldCoordinate, intersection);
                //TODO: Decide whether to do or not do this
                //if (!(distance == 0)){
                distancesIntersections.add(new Pair<Double, Coordinate>(distance, intersection));
                //}
            }
        }

        //Now we calculate the closest intersection point
        PositionEnum res = null;
        Coordinate closestIntersection = null;
        if (!(distancesIntersections.size() == 0)) {
            double minDistance = Double.MAX_VALUE;
            for (Pair<Double, Coordinate> pair : distancesIntersections) {
                if (pair.a < minDistance) {
                    closestIntersection = pair.b;
                    minDistance = pair.a;
                }
            }
        }
        //If we have no intersection points, we know that we are inside the radius of the gaslayer
        else {
            closestIntersection = currentCoordinate;
            res = PositionEnum.INSIDE;
        }

        //If res is still null, that means that the photon is either above or below the gaslayer
        if (res == null) {
            if (calculateR(closestIntersection) <= gaslayer.getInnerR()+Constants.epsilon) {
                res = PositionEnum.UNDER;
            } else {
                res = PositionEnum.ABOVE;
            }
        }

        //Calculate the angle of the closest intersection point
        double omega = calculateOmega(closestIntersection);

        Coordinate finalPos = new Coordinate(closestIntersection.getX(), closestIntersection.getY(), closestIntersection.getZ());

        //Limit the angle to the angles of the gaslayer if necessary
        if (!checkInsideAngle(gaslayer.getLeftOmega(), gaslayer.getRightOmega(), omega)) {
            switch (leftOrRight(gaslayer, omega)) {
                case RIGHT:
                    finalPos = limitAngle(closestIntersection, gaslayer.getRightOmega(), gaslayer.getOuterR());
                    res = PositionEnum.RIGHT;
                    break;
                case LEFT:
                    finalPos = limitAngle(closestIntersection, gaslayer.getLeftOmega(), gaslayer.getOuterR());
                    res = PositionEnum.LEFT;
                    break;
            }
        }

        //TODO only adjust z when necessary
        adjustZ(finalPos);

        this.currentCoordinate.setX(finalPos.getX());
        this.currentCoordinate.setY(finalPos.getY());
        this.currentCoordinate.setZ(finalPos.getZ());

        return res;
    }

    /**
     * Calculates the intersection of the line from coordinate to the old position of the photon,
     * and the line from origin to the point at angle omega at distance r.
     * <p>
     * Implements finite line-line intersection described here:
     * https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
     *
     * @param coordinate the coordinate from which L1 goes to the old position
     * @param omega      the angle to which we want to limit
     * @param r          the distance of point x3,y3
     */
    public Coordinate limitAngle(Coordinate coordinate, double omega, double r) {
        double x1 = oldCoordinate.getX();
        double y1 = oldCoordinate.getY();
        double x2 = coordinate.getX();
        double y2 = coordinate.getY();
        double x3 = -Math.sin(Math.toRadians(omega)) * r;
        double y3 = Math.cos(Math.toRadians(omega)) * r;
        //Note that point 4 is (0,0), this simplifies the equation

        double x = ((x1 * y2 - y1 * x2) * (x3)) / ((x1 - x2) * (y3) - (y1 - y2) * (x3));
        double y = ((x1 * y2 - y1 * x2) * (y3)) / ((x1 - x2) * (y3) - (y1 - y2) * (x3));

        return new Coordinate(x, y, 0.0);
    }

    /**
     * Takes in 2 coordinates and calculates the distance between them on the XY plane
     *
     * @param coordinate1 the first coordinate
     * @param coordinate2 the second coordinate
     * @return the distance between coordinate1 and coordinate2
     */
    public double calculateXYDistance(Coordinate coordinate1, Coordinate coordinate2) {
        return Math.sqrt(Math.pow((coordinate1.getX() - coordinate2.getX()), 2) + Math.pow((coordinate1.getY() - coordinate2.getY()), 2));
    }

    /**
     * Adjusts the Z value of the input coordinate to be in line with the old and current Coordinate of the photon
     *
     * @param coordinate The coordinate of which the Z must be adjusted
     */
    public void adjustZ(Coordinate coordinate) {
        double distanceOldToCoord = calculateXYDistance(oldCoordinate, coordinate);
        double distanceOldToNew = calculateXYDistance(oldCoordinate, currentCoordinate);

        double dZ = currentCoordinate.getZ() - oldCoordinate.getZ();
        double newZ = oldCoordinate.getZ() + dZ * distanceOldToCoord / distanceOldToNew;

        coordinate.setZ(newZ);
    }

    /**
     * Checks if the intersection point lays in between the old and new coordinates of the photon
     * https://stackoverflow.com/questions/11907947/how-to-check-if-a-point-lies-on-a-line-between-2-other-points
     *
     * @param coordinate the point of intersection
     * @return true if the intersection lays between the old and new position, false if not
     */
    public boolean checkInBetween(Coordinate coordinate) {
        double dxl = currentCoordinate.getX() - oldCoordinate.getX();
        double dyl = currentCoordinate.getY() - oldCoordinate.getY();

        boolean horizontal = Math.abs(dxl) >= Math.abs(dyl);

        if (horizontal) {
            return dxl > 0 ? (oldCoordinate.getX() <= coordinate.getX() + Constants.epsilon && coordinate.getX() <= currentCoordinate.getX() + Constants.epsilon)
                    : (currentCoordinate.getX() <= coordinate.getX() + Constants.epsilon && coordinate.getX() <= oldCoordinate.getX() + Constants.epsilon);
        } else {
            return dyl > 0 ? (oldCoordinate.getY() <= coordinate.getY() + Constants.epsilon && coordinate.getY() <= currentCoordinate.getY() + Constants.epsilon)
                    : (currentCoordinate.getY() <= coordinate.getY() + Constants.epsilon && coordinate.getY() <= oldCoordinate.getY() + Constants.epsilon);
        }
    }

    /**
     * This method uses the intersection method described here:
     * http://mathworld.wolfram.com/Circle-LineIntersection.html
     *
     * @param gaslayer the gaslayer with which we check an intersection
     * @return a list of intersection coordinates
     */
    public ArrayList<Coordinate> getIntersectionPoints(GasLayerBend2D gaslayer) {
        //Calculate necessary variables
        double dx = currentCoordinate.getX() - oldCoordinate.getX();
        double dy = currentCoordinate.getY() - oldCoordinate.getY();
        double dr = Math.sqrt(dx * dx + dy * dy);
        double d = oldCoordinate.getX() * currentCoordinate.getY() - currentCoordinate.getX() * oldCoordinate.getY();

        double sgn;
        if (dy < 0) {
            sgn = -1;
        } else {
            sgn = 1;
        }

        //Check intersection with the outer layer
        //Note that we always have 2 intersections with the outer layer.

        //Define some variables to prevent redundant calculations
        double dr2 = dr * dr;
        double rootOuter = Math.sqrt(gaslayer.getOuterR() * gaslayer.getOuterR() * dr2 - (d * d));


        double intersecOuterX1 = (d * dy + sgn * dx * rootOuter) / (dr2);
        double intersecOuterX2 = (d * dy - sgn * dx * rootOuter) / (dr2);
        double intersecOuterY1 = (-d * dx + Math.abs(dy) * rootOuter) / (dr2);
        double intersecOuterY2 = (-d * dx - Math.abs(dy) * rootOuter) / (dr2);

        //Check intersection with the inner layer
        //Here we can have the situation where we have no intersections, so we check this first.
        double discriminant = gaslayer.getInnerR() * gaslayer.getInnerR() * dr2 - d * d;
        double intersecInnerX1 = 0.0;
        double intersecInnerY1 = 0.0;
        double intersecInnerX2 = 0.0;
        double intersecInnerY2 = 0.0;
        if (discriminant > 0) {
            double rootInner = Math.sqrt(gaslayer.getInnerR() * gaslayer.getInnerR() * dr2 - (d * d));
            intersecInnerX1 = (d * dy + sgn * dx * rootInner) / (dr2);
            intersecInnerX2 = (d * dy - sgn * dx * rootInner) / (dr2);
            intersecInnerY1 = (-d * dx + Math.abs(dy) * rootInner) / (dr2);
            intersecInnerY2 = (-d * dx - Math.abs(dy) * rootInner) / (dr2);
        }
        //Since Z is irrelavent in this situation, we simply set z to 0.0
        ArrayList<Coordinate> res = new ArrayList<Coordinate>();
        res.add(new Coordinate(intersecOuterX1, intersecOuterY1, 0.0));
        res.add(new Coordinate(intersecOuterX2, intersecOuterY2, 0.0));
        res.add(new Coordinate(intersecInnerX1, intersecInnerY1, 0.0));
        res.add(new Coordinate(intersecInnerX2, intersecInnerY2, 0.0));

        return res;
    }


    /**
     * NOTE: THIS METHOD HAS NOT THOROUGHLY BEEN TESTED, BUGS MIGHT BE PRESENT
     * <p>
     * This method will return a PositionEnum.LEFT if the photon exited the gaslayer on the "left" side of the gaslayer
     * This method will return a PositionEnum.RIGHT if the photon exited the gaslayer on the "right" side of the gaslayer
     *
     * @return a corresponding PositionEnum
     */
    public PositionEnum leftOrRight(GasLayerBend2D gaslayer, double omega) {
        //TODO check if this is correct
        // The opposite angle here is the the angle 180 degrees from the angle in the middle of the two omega angles
        double oppositeAngle;
        double leftOmega = gaslayer.getLeftOmega();
        double rightOmega = gaslayer.getRightOmega();
        //Check if there is a wraparound
        if (leftOmega > rightOmega) {
            //If no wraparound, then the opposite angle is the half of the two angles, plus 180 degrees % 360
            oppositeAngle = ((leftOmega + rightOmega) / 2 + 180) % 360;
            //If the opposite angle is bigger than the the left omega, we use the left omega to check the location
            if (oppositeAngle > leftOmega) {
                //If the photon is in between the left omega and the opposite angle, it has exited the gaslayer on the left
                if (omega < oppositeAngle && omega > leftOmega) {
                    return PositionEnum.LEFT;
                }
                //If the photon is not in between those angles, it exited the gaslayer on the right
                else return PositionEnum.RIGHT;
            } else {
                if (omega > oppositeAngle && omega < rightOmega) {
                    return PositionEnum.LEFT;
                } else return PositionEnum.RIGHT;
            }
        } else {
            //If there is a wraparound the opposite angle is defined as leftomega + rightomega / 2
            oppositeAngle = ((leftOmega + rightOmega) / 2);
            //If the photon is in between the opposite angle and the left omega, then it has exited on the left
            if (omega < oppositeAngle && omega > leftOmega) {
                return PositionEnum.LEFT;
            }
            //If the photon is not in between those angles, it exited the gaslayer on the right
            else return PositionEnum.RIGHT;
        }
    }

    /**
     * This method makes sure the photon actually enters the gaslayer
     */
    public void stepInside(){
        double v_x = -this.currentCoordinate.getX();
        double v_y = -this.currentCoordinate.getY();

        this.currentCoordinate.setX(this.currentCoordinate.getX() + v_x * 0.000000001);
        this.currentCoordinate.setY(this.currentCoordinate.getY() + v_y * 0.000000001);
    }

    /**
     * This method makes sure the photon actually exits the gaslayer
     */
    public void stepOutside(){
        double v_x = this.currentCoordinate.getX();
        double v_y = this.currentCoordinate.getY();

        this.currentCoordinate.setX(this.currentCoordinate.getX() + v_x * 0.000000001);
        this.currentCoordinate.setY(this.currentCoordinate.getY() + v_y * 0.000000001);
    }

    public void calculateHorizontalIndex(ArrayList<GasLayerBend2D> gasLayers){
        double omega = this.calculateOmega(this.currentCoordinate);
        for (int i = 0; i < gasLayers.size(); i++) {
            if (checkInsideAngle(gasLayers.get(i).getLeftOmega(), gasLayers.get(i).getRightOmega(), omega)){
                this.horizontalIndex = i;
            }
        }
    }

}
