
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ExtensionFileFilter;
import org.jfree.ui.Layer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.CategoryItemRenderer;

import data.Settings;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import view.PodFrame.PopVsDistResults;

public class TabbedResult extends JTabbedPane {

    private JScrollPane scroller_general, scroller_popDistr, scroller_time, scroller_entryRate, scroller_popvsdist;
    private JPanel p_general, p_popDistr, p_time, p_entryRate, p_popVsDist, popVsDistComboPanel, popvsdistcontainerpanel;
    private JTextArea result;
    private int[] population, pods;
    private double[] hours, booths, cars, rate_48, rate_servingTime, distRange;
    private int pop_size;
    private PopVsDistResults popVsDist;
    private JFreeChart popChart_pie, popChart_bar, hourChart;
    private Settings set;
    private JList podList;
    private JComboBox podCombo;
//    private String prefix;
    private PodFrame podFrame;
    JLabel mean, stdDev;

    public TabbedResult(Settings set) {
        this.set = set;
        result = new JTextArea();
    }

    public void init() {

        this.scroller_general = new JScrollPane();
        this.scroller_popDistr = new JScrollPane();
        this.scroller_time = new JScrollPane();
        this.scroller_entryRate = new JScrollPane();
        this.scroller_popvsdist = new JScrollPane();
        this.initGeneral();
        this.initPopDistr();
        this.initTime();
        this.initEntryRate();
        this.initPopVsDist();
        this.scroller_general.setVisible(true);
        this.scroller_popDistr.setVisible(true);
        this.scroller_time.setVisible(true);
        this.scroller_entryRate.setVisible(true);
        this.scroller_popvsdist.setVisible(true);
        this.p_general.setVisible(true);
        this.p_popDistr.setVisible(true);
        this.p_time.setVisible(true);
        this.p_entryRate.setVisible(true);
        this.p_popVsDist.setVisible(true);
        this.addTab("General", scroller_general);
        this.addTab("Population Distribution", scroller_popDistr);
        this.addTab("Time", scroller_time);
        this.addTab("Entry and exit rate", scroller_entryRate);
        this.addTab("Population Vs Distance", scroller_popvsdist);
        this.setVisible(true);
    }

    private void initGeneral() {

        scroller_general.setAutoscrolls(true);
        p_general = new JPanel(new BorderLayout());
        scroller_general.getViewport().add(p_general);
        scroller_general.setVisible(true);
        p_general.setVisible(true);
        result.setEditable(false);
        result.setVisible(true);
        p_general.add(result, BorderLayout.CENTER);
        JButton export = new JButton("Export report to file");
        p_general.add(export, BorderLayout.NORTH);
        export.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser c = new JFileChooser();
                FileFilter filter = new ExtensionFileFilter("Documents", "doc");
                c.setFileFilter(filter);
                int returnVal = c.showSaveDialog(scroller_general);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = c.getSelectedFile();
                    if (!(f.getName().toLowerCase()).endsWith(".doc")) {
                        f = new File(f.getPath() + ".doc");
                        if (!f.exists()) {
                            try {
                                f.createNewFile();
                            } catch (IOException e1) {
                                JOptionPane.showMessageDialog(p_general,
                                        "Cannot create file", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    try {
                        BufferedWriter output = new BufferedWriter(
                                new FileWriter(f, false));
                        output.write(result.getText());
                        output.close();
                    } catch (IOException e2) {
                        JOptionPane.showMessageDialog(p_general,
                                "Cannot save data", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

    }

    private void initPopDistr() {

        scroller_popDistr.setAutoscrolls(true);
        p_popDistr = new JPanel(new BorderLayout());
        scroller_popDistr.getViewport().add(p_popDistr);
        scroller_popDistr.setVisible(true);
        pop_size = 0;
        for (int i = 0; i < population.length; i++) {
            pop_size += population[i];
        }
        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        DefaultCategoryDataset data2 = new DefaultCategoryDataset();
        for (int i = 0; i < population.length; i++) {
            double value = 100 * ((double) population[i] / (double) pop_size);
            data.setValue("POD " + (i + 1), value);
            data2.setValue(population[i], "Population", "" + pods[i]);
        }
        // create a chart...
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Population distribution across the PODs",
                // JFreeChart chart = ChartFactory.createPieChart("Population
                // distribution across the PODs",
                data, false, // legend?
                true, // tooltips?
                false // URLs?
                );

        JFreeChart chart2 = ChartFactory.createBarChart3D(
                "Population distribution across the PODs", "PODs",
                "Population", data2, PlotOrientation.VERTICAL, false, true,
                false);

        ChartPanel cp = new ChartPanel(chart);
        ChartPanel cp2 = new ChartPanel(chart2);
        JPanel charts = new JPanel(new GridLayout(0, 1));
        charts.add(cp);
        charts.add(cp2);
        // p_popDistr.add(cp, BorderLayout.CENTER);
        // p_popDistr.add(cp2, BorderLayout.CENTER);
        popChart_bar = chart2;
        popChart_pie = chart;
        p_popDistr.add(charts);
        charts.setVisible(true);
        cp.setVisible(true);
        cp2.setVisible(true);
        p_popDistr.setVisible(true);
    }

    private void initEntryRate() {
        scroller_entryRate.setAutoscrolls(true);
        p_entryRate = new JPanel(new GridLayout(0, 1));
        scroller_entryRate.getViewport().add(p_entryRate);
        scroller_entryRate.setVisible(true);

        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (int i = 0; i < rate_48.length; i++) {
            double value1 = rate_servingTime[i];
            double value2 = rate_48[i];
            data.setValue(value1, "Minimum serving time based on given assumptions", "" + pods[i]);
            data.setValue(value2, "Equally distributed across 48 hours", "" + pods[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart3D(
                "Entry and exit rate of cars at the POD locations", "PODs",
                "Time in seconds", data, PlotOrientation.VERTICAL, true, true,
                false);

        ChartPanel cp = new ChartPanel(chart);

        p_entryRate.add(cp);
        p_entryRate.setVisible(true);
        cp.setVisible(true);
    }

    public void initTime() {
        scroller_time.setAutoscrolls(true);
        //p_time = new JPanel(new GridLayout(0, 1));
        p_time = new JPanel();
        p_time.setLayout(new BoxLayout(p_time, BoxLayout.PAGE_AXIS));
        scroller_time.getViewport().add(p_time);
        scroller_time.setVisible(true);

        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (int i = 0; i < hours.length; i++) {
            double time = hours[i];
            data.setValue(time,
                    "Time needed to serve population at the individual PODs with the given assumptions",
                    "" + (i + 1));
        }
        final CategoryItemRenderer renderer = new SufficientHoursBarRenderer();

        CategoryPlot plot = new CategoryPlot();
        plot.setDataset(data);
        plot.setRenderer(renderer);
        plot.setDomainAxis(new CategoryAxis("PODs"));
        plot.setRangeAxis(new NumberAxis("Time in hours"));
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        ValueMarker baseline = new ValueMarker(48.0);
        baseline.setPaint(Color.blue);
        baseline.setStroke(new BasicStroke(1.1f));
        plot.addRangeMarker(baseline, Layer.FOREGROUND);

        double sum = 0;
        for (int i = 0; i < hours.length; i++) {
            sum += (48.0 - hours[i]);
        }
        DecimalFormat df = new DecimalFormat(".#");
        JLabel underUtilLabel = new JLabel("Net Under Utilization: " + df.format(sum) + " hours");

        JFreeChart chart = new JFreeChart("Time needed by PODs to serve their populations",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        ChartPanel cp = new ChartPanel(chart);
        hourChart = chart;
        p_time.add(cp);
        p_time.add(underUtilLabel);
        p_time.setVisible(true);
        cp.setVisible(true);

        //JOptionPane.showMessageDialog(this, "Deviation from 48hrs: "+sum);

    }

    public void initPopVsDist() {

        podCombo = new JComboBox();
        podCombo.addItem(0);

        for (int i = 0; i < pods.length; i++) {
            podCombo.addItem(pods[i]);
        }

        podCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent ie) {
                int index = (Integer) podCombo.getSelectedItem();

                double[] distanceRange = podFrame.getDistanceRange(index);
                popVsDistComboPanel.removeAll();
                popVsDistComboPanel.add(new JLabel("POD"));
                popVsDistComboPanel.add(podCombo);
                if (index == 0) {
                    //popVsDistComboPanel.add(new JLabel("Variance: NA"));
                    popVsDistComboPanel.add(new JLabel("Mean: " + roundTo3Decimals(mean(podFrame.getPopVsDistResultSet()))));
                    popVsDistComboPanel.add(new JLabel("Standard Deviation: " + roundTo3Decimals(Math.sqrt(variance(podFrame.getPopVsDistResultSet())))));

                } else {
                    //popVsDistComboPanel.add(new JLabel("Variance: " + Double.toString(callVariance(index))));
                    popVsDistComboPanel.add(new JLabel("Mean: " + roundTo3Decimals(callMean(index))));
                    popVsDistComboPanel.add(new JLabel("Standard Deviation: " + roundTo3Decimals(Math.sqrt(callVariance(index)))));

                }

                //variance.setText( "Variance: " + Double.toString(variance(results)));
                popVsDistComboPanel.setVisible(false);
                popVsDistComboPanel.setVisible(true);
                //drawPopVsDistPlot(results, distanceRange);
                if (index == 0) {
                    drawPopVsDistPlot(popVsDist, distRange);
                } else {
                    callDrawPopVsDistPlot(index);
                }
                Insets insets = popvsdistcontainerpanel.getInsets();

                Dimension size = popVsDistComboPanel.getPreferredSize();
                popVsDistComboPanel.setBounds(insets.left, 5 + insets.top,
                        size.width, size.height);

                Dimension size1 = p_popVsDist.getPreferredSize();
                p_popVsDist.setBounds(insets.left, 10 + insets.top + size.height,
                        size1.width, size1.height);
                popvsdistcontainerpanel.removeAll();

                popvsdistcontainerpanel.add(popVsDistComboPanel);
                popvsdistcontainerpanel.add(p_popVsDist);


                popvsdistcontainerpanel.setVisible(false);
                popvsdistcontainerpanel.setVisible(true);
                scroller_popvsdist.setVisible(false);
                scroller_popvsdist.setVisible(true);

                popvsdistcontainerpanel.setVisible(false);
                popvsdistcontainerpanel.setVisible(true);
            }
        });


        scroller_popvsdist.setAutoscrolls(true);
        popVsDistComboPanel = new JPanel(new FlowLayout());
        p_popVsDist = new JPanel(new GridLayout(0, 1));

        popvsdistcontainerpanel = new JPanel(null);


        //variance = new JLabel("Varaince:");
        if (!podFrame.getSettings().getDefaultDistinKM()) {
            stdDev = new JLabel("Standard Deviation: " + roundTo3Decimals(0.621*Math.sqrt(variance(podFrame.getPopVsDistResultSet()))));
            mean = new JLabel("Mean: " + roundTo3Decimals(0.621*mean(podFrame.getPopVsDistResultSet())));
        } else {
            stdDev = new JLabel("Standard Deviation: " + roundTo3Decimals(Math.sqrt(variance(podFrame.getPopVsDistResultSet()))));
            mean = new JLabel("Mean: " + roundTo3Decimals(mean(podFrame.getPopVsDistResultSet())));
        }
        popVsDistComboPanel.add(new JLabel("POD"));
        popVsDistComboPanel.add(podCombo);
        //popVsDistComboPanel.add(variance);
        popVsDistComboPanel.add(mean);
        popVsDistComboPanel.add(stdDev);

        popVsDistComboPanel.setVisible(true);

        scroller_popvsdist.getViewport().add(popvsdistcontainerpanel);
        scroller_popvsdist.setVisible(true);

        //int selectListenerFlag = 0;
        drawPopVsDistPlot(popVsDist, distRange);
        Insets insets = popvsdistcontainerpanel.getInsets();

        Dimension size = popVsDistComboPanel.getPreferredSize();
        popVsDistComboPanel.setBounds(insets.left, 5 + insets.top,
                size.width, size.height);

        Dimension size1 = p_popVsDist.getPreferredSize();
        p_popVsDist.setBounds(insets.left, 10 + insets.top + size.height,
                size1.width, size1.height);

        popvsdistcontainerpanel.add(popVsDistComboPanel);
        popvsdistcontainerpanel.add(p_popVsDist);


        popvsdistcontainerpanel.setVisible(false);
        popvsdistcontainerpanel.setVisible(true);
        scroller_popvsdist.setVisible(false);
        scroller_popvsdist.setVisible(true);

    }

    public void setText(String text) {
        text = "REPORT \n\n\n" + "Assumptions: \n" + "People per car: "
                + set.getDefaultPeoplePerCar() + "\n"
                + "Estimated time per car at a booth: "
                + set.getDefaultTimePerBooth() + " seconds \n\n\n\n" + text;
        result.setText(text);
    }

    public void setPopulation(int[] population) {
        this.population = population;
    }

    public void setHours(double[] hours) {
        this.hours = hours;
    }

    public void setBooths(double[] booths) {
        this.booths = booths;
    }

    public void setRate_48(double[] rate) {
        this.rate_48 = rate;
    }

    public void setRate_servingTime(double[] rate) {
        this.rate_servingTime = rate;
    }

    public void setCars(double[] cars) {
        this.cars = cars;

    }

    public void setPODs(int[] pods) {
        this.pods = pods;
    }

    public void setPopVsDist(PopVsDistResults popVsDist) {
        this.popVsDist = popVsDist;
    }

    public void setDistRange(double[] distRange) {
        this.distRange = distRange;
    }

    private double roundToDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Double.valueOf(twoDForm.format(d));
    }

    public void setPodFrame(PodFrame podFrame) {
        this.podFrame = podFrame;
    }

    public void callDrawPopVsDistPlot(int podNum) {
        //drawPopVsDistPlot(podFrame.getPopVsDistForPod(podNum), podFrame.getDistanceRange(podNum));
        drawPopVsDistPlot(podFrame.getPopVsDistForPod(podNum), podFrame.getDistanceRange(podNum));
    }

    public double callVariance(int podNum) {
        return variance(podFrame.getPopVsDistForPod(podNum));
    }

    public double callMean(int podNum) {
        return mean(podFrame.getPopVsDistForPod(podNum));
    }

    public void drawPopVsDistPlot(PopVsDistResults popvsdistResults, double[] distanceRange) {
        int numBuckets = 10;

        DefaultCategoryDataset data = new DefaultCategoryDataset();

        double[] buckets = new double[numBuckets];
        double minDist = distanceRange[0];
        double maxDist = distanceRange[1];
        for (int i = 0; i < numBuckets; i++) {
            if (!podFrame.getSettings().getDefaultDistinKM()) {
                buckets[i] = roundToDecimals(0.621 * (minDist + i * (maxDist - minDist) / (numBuckets - 1)));
            } else {
                buckets[i] = roundToDecimals(minDist + i * (maxDist - minDist) / (numBuckets - 1));
            }
        }

        int[] pop = new int[numBuckets];
        //int[] pop = new int[popvsdistResults.dist.length];

        for (int i = 0; i < numBuckets; i++) {
            pop[i] = 0;
        }

//        try {
//            while (popvsdistResults.next()) {
//                //System.out.println("Row Num: " + popvsdistResults.getRow() + "\n");
//                double dist = popvsdistResults.getDouble("distance");
//                int numPop = popvsdistResults.getInt("population");
//                int index = (int) ((dist - minDist) * (numBuckets - 1) / (maxDist - minDist));
//                pop[index] += numPop;
//            }
//        } catch (java.sql.SQLException e) {
//            e.printStackTrace();
//        }

        for (int i = 0; i < popvsdistResults.dist.length; i++) {
            //System.out.println("Row Num: " + popvsdistResults.getRow() + "\n");
            double dist = popvsdistResults.dist[i];
            int numPop = popvsdistResults.numPop[i];
            int index = (int) ((dist - minDist) * (numBuckets - 1) / (maxDist - minDist));
            pop[index] += numPop;
        }

        int[] cumPop = new int[numBuckets];
        for (int i = 0; i < numBuckets; i++) {
            if (i == 0) {
                cumPop[i] = pop[i];
            } else {
                cumPop[i] = pop[i] + cumPop[i - 1];
            }
        }


//        for (int i = 0; i < popvsdistResults.dist.length; i++) {
//            //System.out.println("Row Num: " + popvsdistResults.getRow() + "\n");
////            double dist = popvsdistResults.dist[i];
////            int numPop = popvsdistResults.numPop[i];
////            int index = (int) ((dist - minDist) * (numBuckets - 1) / (maxDist - minDist));
////            pop[index] += numPop;
//            pop[i] = popvsdistResults.numPop[i];
//        }
//
//        //int[] cumPop = new int[numBuckets];
//        int[] cumPop = new int[popvsdistResults.dist.length];
//        for (int i = 0; i < popvsdistResults.dist.length; i++) {
//            if (i == 0) {
//                cumPop[i] = pop[i];
//            } else {
//                cumPop[i] = pop[i] + cumPop[i - 1];
//            }
//        }


//        for (int i = 0; i < numBuckets; i++) {
//            data.setValue(cumPop[i], "Distance in km", "" + buckets[i]);
//            data.setValue(pop[i], "Distance in km", "" + buckets[i]);
//        }

        final String series1 = "Cumulative";
        final String series2 = "Non-Cumulative";

        for (int i = 0; i < numBuckets; i++) {
            data.setValue(cumPop[i], series1, "" + buckets[i]);
            data.setValue(pop[i], series2, "" + buckets[i]);
        }

//        for (int i = 0; i < cumPop.length; i++) {
//            data.setValue(cumPop[i], series1, ""+popvsdistResults.dist[i]);
//            //data.setValue(pop[i], series2, "" + buckets[i]);
//        }
        //final CategoryItemRenderer renderer = new SufficientHoursBarRenderer();
        final JFreeChart chart;
        if (!podFrame.getSettings().getDefaultDistinKM()) {
            chart = ChartFactory.createLineChart(
                    "Population vs Distance", // chart title
                    "Distance in Miles", // domain axis label
                    "Population", // range axis label
                    data, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
                    );
        } else {
            chart = ChartFactory.createLineChart(
                    "Population vs Distance", // chart title
                    "Distance in KM", // domain axis label
                    "Population", // range axis label
                    data, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
                    );
        }

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);

        plot.setDataset(data);
        //plot.setRenderer(renderer);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(
                0, new BasicStroke(
                2.0f, BasicStroke.JOIN_MITER, BasicStroke.JOIN_MITER,
                1.0f, new float[]{10.0f, 6.0f}, 0.0f));

        renderer.setSeriesStroke(
                1, new BasicStroke(
                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[]{6.0f, 6.0f}, 0.0f));

//        plot.setRenderer(renderer);
//        CategoryItemRenderer renderer0 = new SufficientHoursBarRenderer();
//        CategoryItemRenderer renderer1 = new SufficientHoursBarRenderer();
//
//        plot.setRenderer(0, renderer0);
//        plot.setRenderer(1, renderer1);

//        renderer0.setSeriesPaint(0, Color.red);
//        renderer0.setSeriesPaint(1, Color.blue);

        //plot.setRenderer(renderer);
        if (!podFrame.getSettings().getDefaultDistinKM()) {
            plot.setDomainAxis(new CategoryAxis("Distance in miles"));
        } else {
            plot.setDomainAxis(new CategoryAxis("Distance in km"));
        }
        plot.setRangeAxis(new NumberAxis("Population"));
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        //ValueMarker baseline = new ValueMarker(48.0);
        //baseline.setPaint(Color.blue);
        //baseline.setStroke(new BasicStroke(1.1f));
        //plot.addRangeMarker(baseline, Layer.FOREGROUND);



        ChartPanel cp = new ChartPanel(chart);
        //hourChart = chart;
        p_popVsDist.removeAll();
        p_popVsDist.add(cp);
        cp.setVisible(true);
        p_popVsDist.setVisible(false);
        p_popVsDist.setVisible(true);

        popvsdistcontainerpanel.setVisible(false);
        popvsdistcontainerpanel.setVisible(true);

    }

    public void drawPopVsDistPlot(ResultSet popvsdistResults, double[] distanceRange) {
        int numBuckets = 10;

        DefaultCategoryDataset data = new DefaultCategoryDataset();
        double[] buckets = new double[numBuckets];
        double minDist = distanceRange[0];
        double maxDist = distanceRange[1];
        for (int i = 0; i < numBuckets; i++) {

            if (!podFrame.getSettings().getDefaultDistinKM()) {
                buckets[i] = roundToDecimals(0.621 * (minDist + i * (maxDist - minDist) / (numBuckets - 1)));
            } else {
                buckets[i] = roundToDecimals(minDist + i * (maxDist - minDist) / (numBuckets - 1));
            }
        }

        int[] pop = new int[numBuckets];
        for (int i = 0; i < numBuckets; i++) {
            pop[i] = 0;
        }

        try {
            while (popvsdistResults.next()) {
                //System.out.println("Row Num: " + popvsdistResults.getRow() + "\n");
                double dist = popvsdistResults.getDouble("distance");
                int numPop = popvsdistResults.getInt("population");
                int index = (int) ((dist - minDist) * (numBuckets - 1) / (maxDist - minDist));
                pop[index] += numPop;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

//        for(int i =0; i<popvsdistResults.dist.length; i++) {
//            //System.out.println("Row Num: " + popvsdistResults.getRow() + "\n");
//            double dist = popvsdistResults.dist[i];
//            int numPop = popvsdistResults.numPop[i];
//            int index = (int) ((dist - minDist) * (numBuckets - 1) / (maxDist - minDist));
//            pop[index] += numPop;
//        }

        int[] cumPop = new int[numBuckets];
        for (int i = 0; i < numBuckets; i++) {
            if (i == 0) {
                cumPop[i] = pop[i];
            } else {
                cumPop[i] = pop[i] + cumPop[i - 1];
            }
        }

        final String series1 = "Cumulative";
        final String series2 = "Non-Cumulative";

        for (int i = 0; i < numBuckets; i++) {
            //data.setValue(cumPop[i], "Distance in km", "" + buckets[i]);
            data.setValue(cumPop[i], series1, "" + buckets[i]);
            System.out.println(cumPop[i] + "\t" + buckets[i]);
            data.setValue(pop[i], series2, "" + buckets[i]);
        }

        final JFreeChart chart;
        if (!podFrame.getSettings().getDefaultDistinKM()) {
            chart = ChartFactory.createLineChart(
                    "Population vs Distance", // chart title
                    "Distance in Miles", // domain axis label
                    "Population", // range axis label
                    data, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
                    );
        } else {
            chart = ChartFactory.createLineChart(
                    "Population vs Distance", // chart title
                    "Distance in KM", // domain axis label
                    "Population", // range axis label
                    data, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips
                    false // urls
                    );
        }

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);

        plot.setDataset(data);
        //plot.setRenderer(renderer);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(
                0, new BasicStroke(
                2.0f, BasicStroke.JOIN_MITER, BasicStroke.JOIN_MITER,
                1.0f, new float[]{10.0f, 6.0f}, 0.0f));

        renderer.setSeriesStroke(
                1, new BasicStroke(
                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[]{6.0f, 6.0f}, 0.0f));

//        plot.setRenderer(renderer);
//        CategoryItemRenderer renderer0 = new SufficientHoursBarRenderer();
//        CategoryItemRenderer renderer1 = new SufficientHoursBarRenderer();
//
//        plot.setRenderer(0, renderer0);
//        plot.setRenderer(1, renderer1);

//        renderer0.setSeriesPaint(0, Color.red);
//        renderer0.setSeriesPaint(1, Color.blue);

        //plot.setRenderer(renderer);

        if (!podFrame.getSettings().getDefaultDistinKM()) {
            plot.setDomainAxis(new CategoryAxis("Distance in miles"));
        } else {
            plot.setDomainAxis(new CategoryAxis("Distance in km"));
        }
        plot.setRangeAxis(new NumberAxis("Population"));
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        //ValueMarker baseline = new ValueMarker(48.0);
        //baseline.setPaint(Color.blue);
        //baseline.setStroke(new BasicStroke(1.1f));
        //plot.addRangeMarker(baseline, Layer.FOREGROUND);



        ChartPanel cp = new ChartPanel(chart);
        //hourChart = chart;
        p_popVsDist.removeAll();
        p_popVsDist.add(cp);
        cp.setVisible(true);
        p_popVsDist.setVisible(false);
        p_popVsDist.setVisible(true);

        popvsdistcontainerpanel.setVisible(false);
        popvsdistcontainerpanel.setVisible(true);

    }

    /*
     * Older implementation of pop s dist chart
    public void drawPopVsDistPlot(ResultSet popvsdistResults, double[] distanceRange) {
    int numBuckets = 10;

    DefaultCategoryDataset data = new DefaultCategoryDataset();
    double[] buckets = new double[numBuckets];
    double minDist = distanceRange[0];
    double maxDist = distanceRange[1];
    for (int i = 0; i < numBuckets; i++) {
    buckets[i] = roundToDecimals(minDist + i * (maxDist - minDist) / (numBuckets - 1));
    }

    int[] pop = new int[numBuckets];
    for (int i = 0; i < numBuckets; i++) {
    pop[i] = 0;
    }

    try {
    while (popvsdistResults.next()) {
    //System.out.println("Row Num: " + popvsdistResults.getRow() + "\n");
    double dist = popvsdistResults.getDouble("distance");
    int numPop = popvsdistResults.getInt("population");
    int index = (int) ((dist - minDist) * (numBuckets - 1) / (maxDist - minDist));
    pop[index] += numPop;
    }
    } catch (java.sql.SQLException e) {
    e.printStackTrace();
    }

    //        for(int i =0; i<popvsdistResults.dist.length; i++) {
    //            //System.out.println("Row Num: " + popvsdistResults.getRow() + "\n");
    //            double dist = popvsdistResults.dist[i];
    //            int numPop = popvsdistResults.numPop[i];
    //            int index = (int) ((dist - minDist) * (numBuckets - 1) / (maxDist - minDist));
    //            pop[index] += numPop;
    //        }

    int[] cumPop = new int[numBuckets];
    for (int i = 0; i < numBuckets; i++) {
    if (i == 0) {
    cumPop[i] = pop[i];
    } else {
    cumPop[i] = pop[i] + cumPop[i - 1];
    }
    }

    for (int i = 0; i < numBuckets; i++) {
    data.setValue(cumPop[i], "Distance in km", "" + buckets[i]);
    }
    final CategoryItemRenderer renderer = new SufficientHoursBarRenderer();

    ValueMarker marker = new ValueMarker(Math.sqrt(variance(popvsdistResults)));
    marker.setLabel("Std. Dev.");
    marker.setPaint(Color.black);

    CategoryPlot plot = new CategoryPlot();
    plot.setDataset(data);
    plot.setRenderer(renderer);
    plot.setDomainAxis(new CategoryAxis("Distance in km"));
    plot.setRangeAxis(new NumberAxis("Population"));
    plot.setOrientation(PlotOrientation.VERTICAL);
    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    ValueMarker baseline = new ValueMarker(48.0);
    baseline.setPaint(Color.blue);
    baseline.setStroke(new BasicStroke(1.1f));
    plot.addRangeMarker(baseline, Layer.FOREGROUND);
    plot.addRangeMarker(marker);

    final JFreeChart chart = ChartFactory.createAreaChart(
    "Population vs Distance", // chart title
    "Distance in KM", // domain axis label
    "Population", // range axis label
    data, // data
    PlotOrientation.VERTICAL, // orientation
    true, // include legend
    true, // tooltips
    false // urls
    );

    ChartPanel cp = new ChartPanel(chart);
    //hourChart = chart;
    p_popVsDist.removeAll();
    p_popVsDist.add(cp);
    cp.setVisible(true);
    p_popVsDist.setVisible(false);
    p_popVsDist.setVisible(true);

    popvsdistcontainerpanel.setVisible(false);
    popvsdistcontainerpanel.setVisible(true);

    }
     */
    public double variance2(ResultSet popVsDistResults) {
        double var = 0;
        double expected = 0;
        double expectedSquared = 0;
        int count = 0;
        try {
            while (popVsDistResults.next()) {
                double dist = popVsDistResults.getDouble("distance");
                //System.out.println("Distance: " + dist + "\n");
                expected += dist;
                expectedSquared += (dist * dist);
                count++;
            }
        } catch (java.sql.SQLException e) {
        }
        expected = expected / count;
//        System.out.println("Count: " + count + "\n");
//        System.out.println("Expected: " + expected + "\n");
//        System.out.println("ExpectedSqaured: " + expectedSquared + "\n");
        return (expectedSquared - (expected * expected));
    }

    public double mean(ResultSet popVsDistResults) {

        double expected = 0;
        //double expectedSquared = 0;
        int count = 0;
        try {
            while (popVsDistResults.next()) {
                double dist = popVsDistResults.getDouble("distance");
                //System.out.println("Distance: " + dist + "\n");
                expected += dist;

                count++;
            }
        } catch (java.sql.SQLException e) {
        }
        expected = expected / count;
//        System.out.println("Count: " + count + "\n");
//        System.out.println("Expected: " + expected + "\n");
//        System.out.println("ExpectedSqaured: " + expectedSquared + "\n");
        return (expected);
    }

    public double variance(ResultSet popVsDistResults) {


        double expected = 0;
        double value = 0;
        int count = 0;
        ArrayList<Double> distArray = new ArrayList<Double>();
        try {
            while (popVsDistResults.next()) {
                double dist = popVsDistResults.getDouble("distance");
                //System.out.println("Distance: " + dist + "\n");
                distArray.add(dist);
                expected += dist;
                //expectedSquared += (dist * dist);
                count++;
            }
        } catch (java.sql.SQLException e) {
        }
        expected = expected / count;

        for (int i = 0; i < count; i++) {
            double dist = distArray.get(i);
            //System.out.println("Distance: " + dist + "\n");
            value += (dist - expected) * (dist - expected);
        }

        value = value / count;

        return value;
    }

    private String roundTo3Decimals(double d) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(d);
    }
}
