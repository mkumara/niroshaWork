package edu.unt.cerl.replan.model;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.model.traffic.BaseTrafficEstimator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maintains record of the current state of a particular scenario including name
 * and description, as well as settings of parameters.
 *
 * Changed author data member to static since each session should be associated
 * with only one author
 *
 * @author Tamara Schneider
 */
public class ScenarioState extends Observable {

    // TODO Decide if necessary
    //  private final String NAME = "name";
    //  private final String AUTHOR = "author";
    //  private final String DESCRIPTION = "description";
    //  private final String PODS_SELECTED = "pods_selected";
    private String name = "";   // name of the scenario
    private static String author = ""; // name of user who created it
    private String description = ""; // optional description of scenario
    private boolean podsSelected = false; // any PODs selected?
    private boolean trafficAnalysisPerformed = false; // replan executed on current PODs?
    private boolean coverageAnalysisPerformed = false;
    private boolean isNew = false; // is it a new scenario (i.e a brand new senario that has never been saved before)
    private boolean isCurrentlySaved = true; // is the current scenario have any unsaved changes
    private boolean podsChanged = false; // changes made to PODs?
    private Map settings; // settings, e.g. ring distance and table names
    private int peoplePerCar = 4; // assume people traveling in a single car
    private int timeInterval = 49; // time interval to show
    private int timeframe = 48; // number of hours to include in analyis
    private int dayOfWeek = 0; // 0 = weekday, 1 = weekend
    private boolean baseTraffic = true;
    private boolean podTraffic = true;
    private boolean catchmentAreasGiven = false; // true for continuous opt 
    private String[] geographies;
    private double buffer;
    private int timePerIndividual = 0; //Time for which each individual is served
    private BaseTrafficEstimator baseTrafficEstimator;
    private HashMap trafficRoadsList;
    private boolean type2VulnAnalysisPerformed = false;
    private boolean resourceAllocationPerformed = false;

    /**
     * Initializes the scenario state
     *
     * @param name name of scenario
     * @param author current user
     * @param description optional description of scenario
     * @param isNew true for new scenario, false for loaded scenario
     */
    public ScenarioState(String name, String author, String description,
            boolean isNew, String[] geographies, int timePerInd) throws SQLException {
        this.name = name;
        this.author = author;
        this.description = description;
        this.isNew = isNew;
        this.geographies = geographies;
        settings = REPLAN.getTables();
        this.timePerIndividual = timePerInd;

        //baseTrafficEstimator = new BaseTrafficEstimator(this);
        trafficRoadsList = new HashMap();
    }

    public ScenarioState(List scenarioInfo, String[] geographies) throws SQLException {
        Iterator it = scenarioInfo.iterator();
        this.author = (String) it.next();
        this.author = UserState.userId;
        this.name = (String) it.next();
        this.description = (String) it.next();
        this.podsSelected = (Boolean) it.next();
        this.trafficAnalysisPerformed = (Boolean) it.next();
        this.podsChanged = (Boolean) it.next();
        this.peoplePerCar = (Integer) it.next();
        this.timeInterval = (Integer) it.next();
        this.timeframe = (Integer) it.next();
        this.dayOfWeek = (Integer) it.next();
        this.baseTraffic = (Boolean) it.next();
        this.podTraffic = (Boolean) it.next();
        this.timePerIndividual = (Integer) it.next();
        this.catchmentAreasGiven = (Boolean) it.next();
        this.isNew = true;

        this.geographies = geographies;
        settings = REPLAN.getTables();

//        baseTrafficEstimator = new BaseTrafficEstimator(this);
        trafficRoadsList = new HashMap();
    }

    public void createBaseTrafficEstimator() throws SQLException {
        baseTrafficEstimator = new BaseTrafficEstimator(this);
    }

    /**
     *
     * @return
     */
    public double getBuffer() {
        return this.buffer;
    }

    /**
     * Scenario settings, such as ring distance and table names
     *
     * @return map of settings
     */
    public Map getSettings() {
        return this.settings;
    }

    //
    // SETTER METHODS
    // will flag the state of the scenario as changed
    //
    /**
     * Decide if PODs have been selected
     *
     * @param selected true if selected
     */
    public void setPodsSelected(boolean selected) {
        this.podsSelected = selected;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Decides if RE-PLAN execution is current
     *
     * @param executed true if current
     */
    public void setTrafficAnalysisPerformed(boolean executed) {
        this.trafficAnalysisPerformed = executed;
        this.setChanged();
        this.notifyObservers();
    }

    public void setCoverageAnalysisPerformed(boolean executed) {
        this.coverageAnalysisPerformed = executed;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Set true if scenario is new
     *
     * @param isNew
     */
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
        this.setChanged();
    }

    //
    // GETTER METHODS
    // TODO Decide if all getter methods necessary or hand map of changed values
    //
    public boolean arePodsSelected() {
        return this.podsSelected;
    }

    public boolean isTrafficAnalysisPerformed() {
        return this.trafficAnalysisPerformed;
    }

    public boolean isCoverageAnalysisPerformed() {
        return this.coverageAnalysisPerformed;
    }

    public boolean isType2VulnAnalysisPerformed() {
        return this.type2VulnAnalysisPerformed;
    }

    public boolean isNewScenario() {
        return this.isNew;
    }

    public void setScenarioSavedState(boolean isSaved) {
        this.isCurrentlySaved = isSaved;
    }

    public boolean isScenarioSaved() {
        return this.isCurrentlySaved;
    }

    public String getName() {
        return this.name;
    }

    public int getTimePerIndividual() {
        return this.timePerIndividual;
    }

    /**
     * Returns the author's user name which is also used as the schema name in
     * the database
     *
     * @return author user name
     */
    public static String getAuthor() {
        return author;
    }

    /**
     * Returns the working copy name
     *
     * @return name with workingcpy_ prefix
     */
    public String getWorkingCopyName() {
        return "workingcpy_" + this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setName(String name) {
        this.name = name;
    }

    //  public int getNumPods() throws SQLException {
    //      GISConversionTools gisConvTools = new GISConversionTools();
    //      Connection c = DriverManager.getConnection(gisConvTools.
    //              getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
    //      return DBTools.tableSize(UserState.getSchemaPrefix() + this.
    //              getWorkingCopyName() + "_pods", c);
    //  }
    public void setPodsChanged() {
        this.podsChanged = true;
    }

    public void unsetPodsChanged() {
        this.podsChanged = false;
        this.setChanged();
        this.notifyObservers();
    }

    public boolean didPodsChange() {
        //this.setChanged();
        //this.notifyObservers();
        return this.podsChanged;
    }

    public void setPeoplePerCar(int peoplePerCar) {
        this.peoplePerCar = peoplePerCar;
    }

    public int getPeoplePerCar() {
        return this.peoplePerCar;
    }

    public void setTimeInterval(int time) {
        this.timeInterval = time;
    }

    public int getTimeInterval() {
        return this.timeInterval;
    }

    public void setTimeFrame(int interval) {
        this.timeframe = interval;
    }

    public int getTimeFrame() {
        return this.timeframe;
    }

    public void setDayOfWeek(int day) {
        this.dayOfWeek = day;
    }

    public int getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setBaseTraffic(boolean b) {
        this.baseTraffic = b;
    }

    public boolean getBaseTraffic() {
        return this.baseTraffic;
    }

    public void setPodTraffic(boolean b) {
        this.podTraffic = b;
    }

    public boolean getPodTraffic() {
        return this.podTraffic;
    }

    public void setCatchmentAreasGiven(boolean b) {
        this.catchmentAreasGiven = b;
    }

    public void setType2VulnAnalysisPeformed(boolean b) {
        this.type2VulnAnalysisPerformed = b;
    }

    public boolean getCatchmentAreasGiven() {
        return this.catchmentAreasGiven;
    }

    public boolean isResourceAllocationPerformed() {
        return resourceAllocationPerformed;
    }

    public void setResourceAllocationPerformed(boolean resourceAllocationPerformed) {
        this.resourceAllocationPerformed = resourceAllocationPerformed;
        this.setChanged();
        this.notifyObservers();
    }

    public void setChangedAndNotify() {
        System.out.println("ScenarioState: setchangedandnotify");
        this.setChanged();
        this.notifyObservers();
    }

    public String[] getGeographies() {
        return this.geographies;
    }

    /**
     * Reads in settings, such as ring distance and table names. Potentially
     * RE-PLAN can support different settings for different Tabs, but currently
     * not implemented. *
     *
     * @return settings stored in a hash map
     */
    private Map setSettings() {
        Map settings = new HashMap();
        try {

            File file = new File("Settings.txt");
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("%")) {
                    continue;
                }
                line = line.replaceAll("\\s", "");
                if (line.length() == 0) {
                    continue;
                }
                String[] tokens = line.split("=");
                settings.put(tokens[0], tokens[1]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return settings;
    }

    public List<String> getValues() {
        List<String> l = new LinkedList();
        l.add(UserState.userId);
        l.add("'" + this.getName() + "'");
        l.add("'" + this.getDescription() + "'");
        l.add(Boolean.toString(this.arePodsSelected()));
        l.add(Boolean.toString(this.isTrafficAnalysisPerformed()));
        l.add(Boolean.toString(this.didPodsChange()));
        l.add(Integer.toString(this.getPeoplePerCar()));
        l.add(Integer.toString(this.getTimeInterval()));
        l.add(Integer.toString(this.getTimeFrame()));
        l.add(Integer.toString(this.getDayOfWeek()));
        l.add(Boolean.toString(this.getBaseTraffic()));
        l.add(Boolean.toString(this.getPodTraffic()));
        return l;
    }

    public BaseTrafficEstimator getBaseTrafficEstimator() {
        return baseTrafficEstimator;
    }

    public HashMap getTrafficRoadsList() {
        return trafficRoadsList;
    }
}
