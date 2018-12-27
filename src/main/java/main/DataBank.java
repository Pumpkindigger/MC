package main;

import org.jzy3d.maths.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DataBank {

    public ArrayList<org.jzy3d.maths.Pair<Double, Double>> data = new ArrayList<>();

    public DataBank(String filename) {
        readData(filename);
    }

    public double maxValue(){
        return data.get(data.size()-1).a;
    }

    /**
     * Reads date from file and puts in in data
     * @param filename the filename of the file from which to read
     */
    private void readData(String filename) {
        try {
            FileReader file = new FileReader(new File(filename));
            BufferedReader br = new BufferedReader(file);
            String temp;
            while (true) {
                temp = br.readLine();
                if (temp == null) {
                    break;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(temp);
                Double angle = Double.parseDouble(stringTokenizer.nextToken());
                Double value = Double.parseDouble(stringTokenizer.nextToken());
                data.add(new Pair<>(value, angle));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the angle corresponding to the parameter value
     * This method will use a binary search and thus run in O(log n)
     * @param value the value which is to be checked to the cdf values
     * @return the angle in which the photon will scatter
     */
    public double getAngle(double value){
        //If the value which is being querried, is higher than the max value, throw exception
        if (value > maxValue()){
            throw new IllegalArgumentException("Couldnt find value");
        }
        //Start the search halfway in the list
        int index = data.size()/2;
        //Count will start on 2 since in the first iteration we want to either add or substract
        // 0.25 of the size of data from the index
        double count = 2;
        //We loop until we found the value
        while (true){
            //If we are at index 0, we return the value at index 0
            if (index == 0){
                return data.get(0).b;
            }
            //If the value is lower than the value at index, but higher than at index-1, we found our value
            if (value <= data.get(index).a && value > data.get(index-1).a){
                return data.get(index).b;
            }
            //calculate the index change
            int difference = data.size() / (int) Math.pow(2, count);
            //If the difference gets to 0, set it to 1
            if (difference == 0){
                difference++;
            }
            //If the value is lower, than we must search the lower half of the array
            if (value < data.get(index).a){
                index = index - difference;
            }
            //Else the value is higher, so we must search the higher half of the array
            else{
                index = index + difference;
            }
            count++;
        }
    }




}
