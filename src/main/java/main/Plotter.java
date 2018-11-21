package main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class Plotter extends ApplicationFrame{

    private XYSeries[] series;

    public Plotter(final String title, String[] dataTitles){
        super(title);
        this.series = new XYSeries[dataTitles.length];
        for (int i = 0; i < dataTitles.length; i++){
            series[i] = new XYSeries(dataTitles[i]);
        }
    }

    //Add data to the series
    public void addData(double x, double y, int dataset){
        series[dataset-1].add(x, y);
    }

    //Create the plot
    public void plot(){
        final XYSeriesCollection data = new XYSeriesCollection();
        for (int i = 0; i < series.length; i++){
            data.addSeries(series[i]);
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "XY Series Demo",
                "X",
                "Y",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    //For testing purposes
    public static void main(String[] args){
        final Plotter demo = new Plotter("XY Series Demo", new String[]{"Expected", "Actual"});
        demo.addData(1.0, 500.2, 1);
        demo.addData(5.0, 694.1, 1);
        demo.addData(4.0, 100.0, 1);
        demo.addData(12.5, 734.4, 1);
        demo.addData(1.0, 600, 2);
        demo.addData(5.0, 800, 2);
        demo.addData(4.0, 200, 2);
        demo.addData(12.5, 800, 2);
        demo.plot();
    }
}


