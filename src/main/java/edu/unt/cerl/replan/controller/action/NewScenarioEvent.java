package edu.unt.cerl.replan.controller.action;

import java.util.EventObject;

/**
 * This class holds an instance of a NewScenarioEvent, which contains name and
 * description information of the new scenario to be created
 */
public class NewScenarioEvent extends EventObject {

    private String name; // name of new scenario
    private String description; // optional description of new scenario
    private String[] geographies; // list of geographies to be included
    private int timePerInd; //Time per individual in seconds

    /*
     * creates a new action event
     */
    public NewScenarioEvent(Object source, String name, String description, String[] geographies, String timePerInd) {
        super(source);
        this.name = name;
        this.description = description;
        this.geographies = geographies;
        this.timePerInd = Integer.parseInt(timePerInd);
    }

    /**
     * returns the name of the new scenario
     * @return name of new scenario
     */
    public String getName() {
        return this.name;
    }

    public int gettimePerIndividual() {
        return this.timePerInd;
    }

    /**
     * returns the optional description of the new scenario
     * @return description of new scenario
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * return the geographies of the scenario
     * @return list of geographies to be used (at least of size 1)
     */
    public String[] getGeographies(){
        return this.geographies;
    }
}
