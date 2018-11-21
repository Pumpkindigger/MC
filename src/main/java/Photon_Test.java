import java.util.ArrayList;

public class Photon_Test {

    public static void main(String[] args) {
        int nrPhotons = 1000000;
        int layers = 1;

        GasLayer gasLayer = new GasLayer(10, 5, 0);

        System.out.println("Optical depth: " + gasLayer.getOpticalDepth());

        // Line 52 calcloc.f:
        // l= (1.0/e(lold))*LOG(1.0/temp)

        ArrayList<Photon> photons = new ArrayList<Photon>(nrPhotons);

        for (int i = 0; i < nrPhotons; i++) {
            photons.add(new Photon(layers));
        }

        int photonsPassed = 0;

        for (Photon photon : photons){
            //l= (1.0/e(lold))*LOG(1.0/temp)
            double travelled = 1.0/(gasLayer.getOpticalDepth()) * Math.log(1.0/MyRandom.random());
            photon.updatePosition(travelled);
            //System.out.println(travelled);
            if (photon.getZ() > gasLayer.getGeometricalDepth()){
                photon.setCurrentLayer(photon.getCurrentLayer()-1);
                photonsPassed++;
            }
        }

        System.out.println("Nr of photons passed by simulation: " + photonsPassed);

        double expected = nrPhotons * Math.exp(-gasLayer.getOpticalDepth());

        System.out.println("Nr of photons passed by formula: " + expected);

        double error = expected / photonsPassed;
        System.out.println("Error factor: " + error);
    }
}
