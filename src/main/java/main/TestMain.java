package main;

public class TestMain {

    /** src/main/resources/<FILENAME>
     *
     */
    public static void main(String[] args){
        DataBank dataBank = new DataBank("src/main/resources/400nm_cdf.txt");
        System.out.println(dataBank.getAngle(238742));
    }
}
