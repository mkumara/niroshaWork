package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.JTextField;

public class PodPanel extends JPanel {

    private String podName;
    private double numberOfBooths, cars, hours, rate_48, rate_servingTime;
    private int population, id;
    private double timePerBooth;
    private JTextField tf_pop, tf_booths;
    private PodFrame owner;
    private ResultSet rs;

    public PodPanel(int id, String name, PodFrame owner) {
        this.id = id;
        this.owner = owner;
        this.podName = name;
        Dimension d = new Dimension(300, 70);
        this.setMinimumSize(d);
        this.setPreferredSize(d);
        this.initLayout();
        this.setVisible(true);
    }

    private void initLayout() {

        this.setLayout(new GridLayout(0, 1));
        JLabel l_pop = new JLabel("Population in catchment area");
        tf_pop = new JTextField(5);
        JLabel l_booths = new JLabel("Number of booths");
        tf_booths = new JTextField(5);
        JPanel p_pop = new JPanel(new BorderLayout());
        JPanel p_booths = new JPanel(new BorderLayout());

        tf_pop.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c
                        == KeyEvent.VK_DELETE)))) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        tf_booths.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((Character.isDigit(c) || (c == '.')
                        || (c == KeyEvent.VK_BACK_SPACE) || (c
                        == KeyEvent.VK_DELETE)))) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        p_pop.add(l_pop, BorderLayout.CENTER);
        p_pop.add(tf_pop, BorderLayout.EAST);
        p_booths.add(l_booths, BorderLayout.CENTER);
        p_booths.add(tf_booths, BorderLayout.EAST);
        this.add(p_pop);
        this.add(p_booths);

    }

    private void setConstraints(GridBagLayout gbl, Component c, int x, int y,
            int w, int h, double wx, double wy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        constraints.weightx = wx;
        constraints.weighty = wy;
        this.add(c, constraints);
    }

    public String getName() {
        return this.podName;
    }

    public double getTime() {
        return this.timePerBooth;
    }

    public int getPopulation() {
        return this.population;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void fill(double booths, int population, String name) {
        this.podName = this.podName + ": " + name;
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.
                createEtchedBorder(), this.podName));
        this.population = population;
        this.numberOfBooths = booths;
        tf_booths.setText("" + booths);
        tf_pop.setText("" + population);
    }

    public void fillBooths(double booths) {
        tf_booths.setText(Double.toString(booths));
    }

    public String calculateEstimate() {
        this.readForm();
        double perCar = owner.getSettings().getDefaultPeoplePerCar();
        double estimate = ((double) population * (double) timePerBooth)
                / ((double) numberOfBooths * 3600 * perCar);
        cars = ((double) population) / perCar;
        rate_48 = (48 * 3600) / cars;
        rate_servingTime = (estimate * 3600) / cars;

        estimate = ((double) Math.round(10 * estimate)) / 10;
        rate_48 = ((double) Math.round(10 * rate_48)) / 10;
        rate_servingTime = ((double) Math.round(10 * rate_servingTime)) / 10;
        cars = ((double) Math.round(10 * cars)) / 10;
        // The following line is numPeopleServedPerDay = 24 hours / serving time * numPeoplePerCar * numBooths
double numPeopleServedPerDay = 24*60*60*owner.getSettings().getDefaultPeoplePerCar()*numberOfBooths/owner.getSettings().getDefaultTimePerBooth();
        String result = podName
                + "\n\n"
                + "has "
                + numberOfBooths
                + " booths and needs to serve "
                + population
                + " people. It is capable of serving " + numPeopleServedPerDay + " people per day.\n \n"
                + "It can serve the population in the catchment area ("
                + cars
                + " cars) in "
                + estimate
                + " hours. \n\n"
                + "To serve the catchment area within these "
                + estimate
                + " hours, \n"
                + "cars have to enter and leave the parking lot at a rate of "
                + rate_servingTime
                + ". seconds \n\n"
                + "If the cars were distributed equally over the 48 hour requirement, \n"
                + "they would have to enter and leave the POD at a rate of "
                + rate_48 + " seconds. \n \n \n \n";
        hours = estimate;
        return result;
    }

    public double getCars() {
        return this.cars;
    }

    public double getBooths() {
            return Double.parseDouble(this.tf_booths.getText());
        //return this.numberOfBooths;
    }

    public double getHours() {
        return this.hours;
    }

    public double getRate_48() {
        return this.rate_48;
    }

    public double getRate_servingTime() {
        return this.rate_servingTime;
    }

    private void readForm() {
        population = new Integer(tf_pop.getText());
        numberOfBooths = new Double(tf_booths.getText());
        timePerBooth = owner.getSettings().getDefaultTimePerBooth();
    }

    public String saveValues() {
        readForm();
        return (this.id + "\t" + this.population + "\t" + this.numberOfBooths
                + "\n");
    }
    //public void readInValues(int id, int population, double numberOfBooths) {
//		if (this.id != id) {
//			System.err.println("Id mismatch");
//			System.exit(0);
//		}
//		this.fill(numberOfBooths, population);
    //}
}
