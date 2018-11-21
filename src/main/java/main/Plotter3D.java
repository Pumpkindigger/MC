package main;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class Plotter3D extends AbstractAnalysis {

    private Coord3d[] coordinates;
    private Color[] colors;

    public Plotter3D(Coord3d[] coordinates, Color[] colors){
        this.coordinates = coordinates;
        this.colors = colors;
        try {
            AnalysisLauncher.open(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(){
        int size = coordinates.length;

//        Color[] colors = new Color[size];
//
//
//        for (int i = 0; i < size; i++) {
//            //colors[i] = new Color(coordinates[i].x, coordinates[i].y, coordinates[i].z, 0.25f);
//            colors[i] = Color.BLUE;
//        }

        Scatter scatter = new Scatter(coordinates, colors);
        scatter.setWidth(2);
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        chart.getScene().add(scatter);
    }

}
