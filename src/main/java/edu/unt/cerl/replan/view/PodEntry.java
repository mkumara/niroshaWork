package edu.unt.cerl.replan.view;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sarat
 */

public class PodEntry {

    private int index;
    private String name;
    private String address;
    private String city;
    private float lat;  //Wt shd be the variable type?
    private float lon;  //Wt shd be the variable type??
    private String zip;
    private String typePod;
    private int booths;
    private boolean status;
    private String comments;

    public PodEntry(String name, String address, String city, float lon, float lat, String zip,
            String typePod, int booths, boolean status, String comments ) {

        this.name = name;
        this.address = address;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.zip = zip;
        this.booths = booths;
        this.typePod = typePod;
        this.status = status;
        this.comments = comments;

    }

     public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    /**
     * @param city the city to set
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * @return the lat
     */
    public float getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(float lat) {
        this.lat = lat;
    }

    /**
     * @return the lon
     */
    public float getLon() {
        return lon;
    }

    /**
     * @param lon the lon to set
     */
    public void setLon(float lon) {
        this.lon = lon;
    }

    /**
     * @return the typePod
     */
    public String getTypePod() {
        return typePod;
    }

    /**
     * @param typePod the typePod to set
     */
    public void setTypePod(String typePod) {
        this.typePod = typePod;
    }

    /**
     * @return the status
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getBooths() {
        return booths;
    }

    /**
     * @param status the status to set
     */
    public void setBooths(int booths) {
        this.booths = booths;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

}
