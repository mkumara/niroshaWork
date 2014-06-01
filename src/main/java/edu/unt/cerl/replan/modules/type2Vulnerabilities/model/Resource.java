/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.model;

/**
 *
 * @author sarat
 */
public class Resource {

    private double latitude;
    private double longitude;
    private int availability; //0-all the time; 1- 1st half; 2-2nd half; ... so on
    private int id;
    private double quantity;
    

    public Resource(double latitude, double longitude, int availability, int id) {
        this.availability = availability;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
        public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
