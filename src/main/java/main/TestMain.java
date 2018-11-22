package main;

import main.objects.Photon;

public class TestMain {

    public static void main(String[] args){
        Photon photon = new Photon(0, 1, 1, 10);
        System.out.println(photon.calculateOmega());
    }


}
