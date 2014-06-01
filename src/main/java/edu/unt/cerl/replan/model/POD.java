package edu.unt.cerl.replan.model;

//import java.util.*;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Each object of this class maintains data for a POD. The class provides
 * methods for accessing and updating this data.
 *
 * The vivid and postgis point x and y values are not identical to the double values for longitude and latitude.
 * This may be due to geographic projections.
 * Need to investigate this more later.
 */
public class POD {

    //
    // Fields
    //
    private int id;
    private int fid;
    private String name;
    private String address;
    private String city;
    private String zip;
    private double longitude;
    private double latitude;
    private Boolean type; // true if public, false if private
    private Boolean status;
    private String comments;
    private int numBooths;
    private org.postgis.Point postgisPoint;
    private com.vividsolutions.jts.geom.Point vividPoint;
    private GeometryFactory geometryFactory;

    //
    // Constructors
    //
    public POD() {
    }

    ;

    public POD(int set_id, int set_fid, String set_name, String set_address, String set_city, String set_zip, double set_longitude, double set_latitude, Boolean set_type, Boolean set_status, String set_comments, int set_numBooths) {
        id = set_id;
        fid = set_fid;
        name = set_name;
        address = set_address;
        city = set_city;
        zip = set_zip;
        longitude = set_longitude;
        latitude = set_latitude;
        type = set_type;
        status = set_status;
        comments = set_comments;
        numBooths = set_numBooths;
        postgisPoint = new org.postgis.Point(longitude, latitude);
        //Coordinate testing = new Coordinate(32.0, 32.0);
        //vividPoint = geometryFactory.createPoint(testing);

        //longitude = 1.0;
        //latitude = 1.0;
        vividPoint = (new GeometryFactory()).createPoint(new Coordinate(longitude, latitude));

        //System.out.println("PostGIS:" + postgisPoint.getX() + "," + postgisPoint.getY());
        //System.out.println("Vivid:" + vividPoint.getX() + "," + vividPoint.getY());
    }

    public int get_id() {
        return id;
    }

    public int get_fid() {
        return fid;
    }

    public String get_name() {
        return name;
    }

    public String get_address() {
        return address;
    }

    public String get_city() {
        return city;
    }

    public String get_zip() {
        return zip;
    }

    public double get_longitude() {
        return longitude;
    }

    public double get_latitude() {
        return latitude;
    }

    public Boolean get_type() {
        return type;
    }

    public Boolean get_status() {
        return status;
    }

    public String get_comments() {
        return comments;
    }

    public int get_numBooths() {
        return numBooths;
    }

    public void set_id(int new_id) {
        id = new_id;
    }

    public int decrement_id() {
        id--;
        return id;
    }

    public void set_name(String new_name) {
        name = new_name;
    }

    public void set_address(String new_address) {
        address = new_address;
    }

    public void set_city(String new_city) {
        city = new_city;
    }

    public void set_zip(String new_zip) {
        zip = new_zip;
    }

    public void set_longitude(double new_longitude) {
        longitude = new_longitude;

        postgisPoint.setX(longitude);
        //System.out.println("PostGIS:" + postgisPoint.getX() + "," + postgisPoint.getY());
        vividPoint = (new GeometryFactory()).createPoint(new Coordinate(longitude, latitude));
        //System.out.println("Vivid:  " + vividPoint.getX() + "," + vividPoint.getY());
    }

    public void set_latitude(double new_latitude) {
        latitude = new_latitude;

        postgisPoint.setY(latitude);
        //System.out.println("PostGIS:" + postgisPoint.getX() + "," + postgisPoint.getY());
        vividPoint = (new GeometryFactory()).createPoint(new Coordinate(longitude, latitude));
        //System.out.println("Vivid:  " + vividPoint.getX() + "," + vividPoint.getY());
    }

    public void set_type(Boolean new_type) {
        type = new_type;
    }

    public void set_status(Boolean new_status) {
        status = new_status;
    }

    public void set_comments(String new_comments) {
        comments = new_comments;
    }

    public void set_numBooths(int new_numBooths) {
        numBooths = new_numBooths;
    }
    
    public void update_POD(String set_name, String set_address, String set_city, String set_zip, double set_longitude, double set_latitude, Boolean set_type, Boolean set_status, String set_comments, int set_numBooths) {
        name = set_name;
        address = set_address;
        city = set_city;
        zip = set_zip;
        longitude = set_longitude;
        latitude = set_latitude;
        type = set_type;
        status = set_status;
        comments = set_comments;
        numBooths = set_numBooths;

        postgisPoint.setX(longitude);
        postgisPoint.setY(latitude);
        //System.out.println("PostGIS:" + postgisPoint.getX() + "," + postgisPoint.getY());
        vividPoint = (new GeometryFactory()).createPoint(new Coordinate(longitude, latitude));
        //System.out.println("Vivid:  " + vividPoint.getX() + "," + vividPoint.getY());
    }

    public void createPostGisPoint(double longitude, double latitude) {
        this.postgisPoint = new org.postgis.Point(longitude, latitude);
    }

    public void createVividPoint(double longitude, double latitude) {
        this.vividPoint = (new GeometryFactory()).createPoint(new Coordinate(longitude, latitude));
    }
}
