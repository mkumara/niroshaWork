package edu.unt.cerl.replan.controller.action;

import java.util.EventObject;


/**
 * This is the event object associated with opening an existing scenario
 */
public class LoadScenarioEvent extends EventObject {

    private String name; // name of scenario to be loaded
    private String author; // name of person who originally saved scenario
    /*
     * creates a new action event
     */
    public LoadScenarioEvent(Object source, String author, String name) {
        super(source);
        this.name = name;
        this.author = author;
    }

    /**
     * returns the name of the new scenario
     * @return name of new scenario
     */
    public String getName() {
        return this.name;
    }

    /**
     * returns the name of the original author of the scenario
     * @return name of author
     */
    public String getAuthor() {
        return this.author;
    }
}
