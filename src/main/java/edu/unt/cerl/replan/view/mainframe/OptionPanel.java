package edu.unt.cerl.replan.view.mainframe;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.view.windows.ProgressWindow;
import edu.unt.cerl.replanexecution.Replan;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class OptionPanel extends JPanel {

    private JComboBox carBox;
    private JComboBox timeBox;
    private JComboBox timeframeBox;
    private JComboBox weekdayWeekendBox;
    private JCheckBox baseTrafficBox;
    private JCheckBox podTrafficBox;
    private ScenarioState state;
    private boolean initializing = false;
    private int peoplePerCar;
    private int timeInterval;
    private int timeframe;
    private int dayOfWeek; // 0 = weekday, 1 = weekend
    private boolean podTraffic;
    private boolean baseTraffic;

    public OptionPanel(ScenarioState state) {
        this.state = state;

        this.peoplePerCar = state.getPeoplePerCar();
        this.timeInterval = state.getTimeInterval();
        this.timeframe = state.getTimeFrame();
        this.dayOfWeek = state.getDayOfWeek();
        this.baseTraffic = state.getBaseTraffic();
        this.podTraffic = state.getPodTraffic();

        initComponents();
        this.initializing = true;
        carBox.setSelectedIndex(this.peoplePerCar - 1);
        timeframeBox.setSelectedIndex(this.timeframe - 1);
        timeBox.setSelectedIndex(this.timeInterval);
        weekdayWeekendBox.setSelectedIndex(this.dayOfWeek);
        baseTrafficBox.setSelected(this.baseTraffic);
        podTrafficBox.setSelected(this.podTraffic);

        this.initializing = false;
    }

    private void initComponents() {

        setMaximumSize(new java.awt.Dimension(280, 220));
        setMinimumSize(new java.awt.Dimension(280, 220));
        setPreferredSize(new java.awt.Dimension(280, 220));
        setVerifyInputWhenFocusTarget(false);
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new GridLayout(0, 1));
        JLabel carLabel = new JLabel("People per car: ");
        JLabel timeLabel = new JLabel("Time of the day: ");
        JLabel timeframeLabel = new JLabel("Distribute traffic over ");
        JLabel hrsLabel = new JLabel("hours.");
        JLabel podTrafficLabel = new JLabel("Show POD traffic");
        JLabel baseTrafficLabel = new JLabel("Show base traffic");
        JLabel dayOfWeekLabel = new JLabel("Weekday / Weekend: ");
        JButton refreshButton = new JButton("Refresh");
        JButton revertButton = new JButton("Revert");

        JPanel carPanel = new JPanel(new FlowLayout());
        JPanel timePanel = new JPanel(new FlowLayout());
        JPanel timeframePanel = new JPanel(new FlowLayout());
        JPanel podTrafficPanel = new JPanel(new FlowLayout());
        JPanel baseTrafficPanel = new JPanel(new FlowLayout());
        JPanel dayOfWeekPanel = new JPanel(new FlowLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout());


        timeframeBox = new JComboBox();
        for (int i=1; i<=240; i++){
        timeframeBox.addItem(Integer.toString(i));
        }
            //timeframeBox.addItem("24");
        //timeframeBox.addItem("48");


        timeBox = new JComboBox();
        for (int i = 0; i <= 95; i++) {
            timeBox.addItem(this.mapIntervalToTimeOfDay(i));
        }

        carBox = new JComboBox();
        carBox.addItem("1");
        carBox.addItem("2");
        carBox.addItem("3");
        carBox.addItem("4");
        carBox.addItem("5");

        weekdayWeekendBox = new JComboBox();
        weekdayWeekendBox.addItem("weekday");
        weekdayWeekendBox.addItem("weekend");

        baseTrafficBox = new JCheckBox();

        podTrafficBox = new JCheckBox();

        carBox.setActionCommand("cars");
        timeBox.setActionCommand("interval");
        timeframeBox.setActionCommand("timeframe");
        weekdayWeekendBox.setActionCommand("dayofweek");
        baseTrafficBox.setActionCommand("basetraffic");
        podTrafficBox.setActionCommand("podtraffic");

        BoxListener listener = new BoxListener();
//        carBox.addActionListener(listener);
//        timeBox.addActionListener(listener);
//        timeframeBox.addActionListener(listener);
//        weekdayWeekendBox.addActionListener(listener);
//        baseTrafficBox.addActionListener(listener);
//        podTrafficBox.addActionListener(listener);s

        refreshButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        revertButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertButtonActionPerformed(evt);
            }
        });

        carPanel.add(carLabel);
        carPanel.add(carBox);

        timePanel.add(timeLabel);
        timePanel.add(timeBox);

        timeframePanel.add(timeframeLabel);
        timeframePanel.add(timeframeBox);
        timeframePanel.add(hrsLabel);


        dayOfWeekPanel.add(dayOfWeekLabel);
        dayOfWeekPanel.add(weekdayWeekendBox);

        baseTrafficPanel.add(baseTrafficLabel);
        baseTrafficPanel.add(baseTrafficBox);

        podTrafficPanel.add(podTrafficLabel);
        podTrafficPanel.add(podTrafficBox);

        buttonsPanel.add(refreshButton);
        buttonsPanel.add(revertButton);

        this.add(carPanel);
        this.add(dayOfWeekPanel);
        this.add(timePanel);
        this.add(timeframePanel);
        this.add(baseTrafficPanel);
        this.add(podTrafficPanel);
        this.add(buttonsPanel);

        setVisible(true);
    }

    private int mapHoursToInterval(String hours) {
        Scanner s = new Scanner(hours);
        s.useDelimiter(":");
        int hrs = s.nextInt();
        int mins = s.nextInt();
        int interval = hrs * 4 + mins / 15;
        return interval;
    }

    private String mapIntervalToTimeOfDay(int num) {
        int hrs = num / 4;
        int mins = (num % 4) * 15;
        String hours = hrs + ":";
        if (mins == 0) {
            hours += "00";
        } else {
            hours += mins;
        }
        return hours;
    }

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {

        peoplePerCar = new Integer((String) carBox.getSelectedItem());
        timeInterval = mapHoursToInterval((String) timeBox.getSelectedItem());
        timeframe = new Integer((String) timeframeBox.getSelectedItem());
        int day = 0;
        if (weekdayWeekendBox.getSelectedItem().equals("weekend")) {
            day = 1;
        }
        boolean podT = podTrafficBox.isSelected();
        boolean baseT = baseTrafficBox.isSelected();
        state.setPeoplePerCar(peoplePerCar);
        state.setTimeInterval(timeInterval);
        state.setTimeFrame(timeframe);
        state.setDayOfWeek(day);
        state.setPodTraffic(podT);
        state.setBaseTraffic(baseT);
        state.setChangedAndNotify();
        //leave the timeframe in hours
        //timeframe = timeframe / 24;

        /*
        Boolean dayBool;
        if (day == 0) {
            dayBool = false;
        } else {
            dayBool = true;
        }
        */

        //if pods are changed change tables
        if (state.didPodsChange()) {
            // fire warning message 
            //execute replan from catchment area creation 
            //traffic analysis
            //unsetpodschanged..
            state.setTrafficAnalysisPerformed(false);
            state.setCatchmentAreasGiven(false);
            state.setChangedAndNotify();
            System.out.println("PODS Changed... recreating all relevant tables\n");

            ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
            Replan task = new Replan(s);         
            ProgressWindow progressWin = new ProgressWindow(task);
            task.refreshAnalysis();

        } else {
            System.out.println("OptionPanel: BoxListener actioPerformed");

//ProgressWindow progressWin = new ProgressWindow(REPLAN.getMainFrame().getREPLANMenuBar().getReplanTask());
            if (!initializing) {
                System.out.println("not initializing");



//            int trafficType;
//            if (podT == true && baseT == true) {
//                trafficType = 0;
//            } else if (podT == true && baseT == false) {
//                trafficType = 2;
//            } else if (podT == false && baseT == true) {
//                trafficType = 1;
//            } else {
//                trafficType = 3;
//            }

//                TrafficAtCrossingPoints tacpts = new TrafficAtCrossingPoints();
//                tacpts.assignTrafficClasses(state, peoplePerCar, trafficType, timeInterval, dayBool, timeframe );
//                REPLAN.getMainFrame().getTabs().getSelectedScenario().refreshLayer(REPLAN.getMainFrame().getTabs().getSelectedScenario().getMapContent().layers().get(REPLAN.getMainFrame().getTabs().getSelectedScenario().getMapContent().layers().size() - 1));
//              
                Replan task = new Replan(REPLAN.getMainFrame().getTabs().getSelectedScenario());
                task.reAssignClasses(state, peoplePerCar, timeInterval, day, timeframe, podT, baseT);
            }
        }
    }

    private void revertButtonActionPerformed(java.awt.event.ActionEvent evt) {
        carBox.setSelectedIndex(this.peoplePerCar - 1);
        timeframeBox.setSelectedIndex(this.timeframe - 1);
        timeBox.setSelectedIndex(this.timeInterval);
        weekdayWeekendBox.setSelectedIndex(this.dayOfWeek);
        baseTrafficBox.setSelected(this.baseTraffic);
        podTrafficBox.setSelected(this.podTraffic);
    }

    private class BoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //String command = e.getActionCommand();

            System.out.println("OptionPanel: BoxListener actioPerformed");


            if (!initializing) {
                System.out.println("not initializing");
                peoplePerCar = new Integer((String) carBox.getSelectedItem());
                timeInterval = mapHoursToInterval((String) timeBox.getSelectedItem());
                timeframe = new Integer((String) timeframeBox.getSelectedItem());
                int day = 0;
                if (weekdayWeekendBox.getSelectedItem().equals("weekend")) {
                    day = 1;
                }
                boolean podT = podTrafficBox.isSelected();
                boolean baseT = baseTrafficBox.isSelected();
                state.setPeoplePerCar(peoplePerCar);
                state.setTimeInterval(timeInterval);
                state.setTimeFrame(timeframe);
                state.setDayOfWeek(day);
                state.setPodTraffic(podT);
                state.setBaseTraffic(baseT);

                //convert hours to days
                timeframe = timeframe ;
                state.setChangedAndNotify();

            }


        }
    }
}
