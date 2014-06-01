/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.pubtrans.model;

import com.csvreader.CsvReader;
import edu.unt.cerl.replan.REPLAN;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 *
 * @author martyo
 */
public class GtfsConversionTools {

    private class GtfsShapeRecord {

        long shape_id;
        double shape_pt_lon;
        double shape_pt_lat;
        long shape_pt_sequence;
        double shape_dist_traveled;

        private GtfsShapeRecord(long set_shape_id,
                double set_shape_pt_lon, double set_shape_pt_lat,
                long set_shape_pt_sequence, double set_shape_dist_traveled) {
            shape_id = set_shape_id;

            shape_pt_lon = set_shape_pt_lon;
            shape_pt_lat = set_shape_pt_lat;
            shape_pt_sequence = set_shape_pt_sequence;
            shape_dist_traveled = set_shape_dist_traveled;
        }

        public long get_shape_id() {
            return shape_id;
        }
    }

    private class GtfsStopsRecord {

        long stop_id;
        String stop_name;
        String stop_desc;
        double stop_lat;
        double stop_lon;
        String zone_id;
        String stop_url;
        //String location_type;
        //int wheelchair_boarding;

        private GtfsStopsRecord(long set_stop_id, String set_stop_name, String set_stop_desc, double set_stop_lat, double set_stop_lon, String set_zone_id, String set_stop_url) {
            stop_id = set_stop_id;
            stop_name = set_stop_name;
            stop_desc = set_stop_desc;
            stop_lat = set_stop_lat;
            stop_lon = set_stop_lon;
            zone_id = set_zone_id;
            stop_url = set_stop_url;
        }
    }
    LinkedList gtfsShapeRecords;
    LinkedList gtfsStopsRecords;
    // LinkedList newSequenceBegins;
    REPLAN REPLAN;

    public static void main(String args[]) throws FileNotFoundException, IOException, SQLException {
        GtfsConversionTools myFile = new GtfsConversionTools();
        myFile.readGtfsStopsCSV("/home/martyo/Dropbox/dissertation/data/fwta20120203gj12b/stops.txt");
        myFile.createStopsPostGISLayer("tarrant_gtfs_stops");


//        myFile.readGtfsShapeCSV("/home/martyo/Dropbox/dissertation/data/fwta20120203gj12b/shapes.txt");
//        myFile.createShapePostGISLayer("tarrant_gtfs_shape");
    }

    public GtfsConversionTools() {
        gtfsShapeRecords = new LinkedList();
        gtfsStopsRecords = new LinkedList();
        //  newSequenceBegins = new LinkedList();
        REPLAN = new REPLAN();

    }

    public void readGtfsStopsCSV(String filename) throws FileNotFoundException, IOException {
        CsvReader file = new CsvReader(filename);
        file.readHeaders();

        while (file.readRecord()) {
            gtfsStopsRecords.add(new GtfsStopsRecord(
                    Long.parseLong(file.get("stop_id")),
                    file.get("stop_name"),
                    file.get("stop_desc"),
                    Double.parseDouble(file.get("stop_lat")),
                    Double.parseDouble(file.get("stop_lon")),
                    file.get("zone_id"),
                    file.get("stop_url")));
        }
    }

    public void readGtfsShapeCSV(String filename) throws FileNotFoundException, IOException {
        CsvReader file = new CsvReader(filename);

        file.readHeaders();

        while (file.readRecord()) {
            gtfsShapeRecords.add(new GtfsShapeRecord(
                    Long.parseLong(file.get("shape_id")),
                    Double.parseDouble(file.get("shape_pt_lon")),
                    Double.parseDouble(file.get("shape_pt_lat")),
                    Long.parseLong(file.get("shape_pt_sequence")),
                    Double.parseDouble(file.get("shape_dist_traveled"))));
        }
    }

    public void createStopsPostGISLayer(String tableName) throws SQLException {
        String query = "CREATE TABLE " + tableName + " (the_geom geometry, stop_id integer, stop_name character varying(255), stop_desc character varying(255), stop_lat double precision, stop_lon double precision, zone_id character varying(255), stop_url character varying(255));";
        System.out.println(query);
        REPLAN.getController().getConnection().createStatement().executeUpdate(query);

        query = "";
        for (int i = 0; i < gtfsStopsRecords.size(); i++) {

            query = "INSERT INTO " + tableName + " (the_geom, stop_id, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url) VALUES ( ST_GeomFromText('POINT(" + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).stop_lon + " " + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).stop_lat + ")')" + ", " + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).stop_id + ", '" + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).stop_name + "', '" + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).stop_desc + "', " + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).stop_lat + ", " + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).stop_lon + ", '" + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).zone_id + "', '" + ((GtfsStopsRecord) gtfsStopsRecords.get(i)).stop_url + "' );";
            System.out.println(query);
            REPLAN.getController().getConnection().createStatement().executeUpdate(query);
        }

    }

    public void createShapePostGISLayer(String tableName) throws SQLException {

        String query = "CREATE TABLE " + tableName + " (shape_id integer, the_geom geometry);";
        REPLAN.getController().getConnection().createStatement().executeUpdate(query);
        query = "";
        long last = 0;
        for (int i = 0; i < gtfsShapeRecords.size(); i++) {
            if (((GtfsShapeRecord) gtfsShapeRecords.get(i)).shape_id != last) {
                query = "INSERT INTO " + tableName + " (shape_id, the_geom) VALUES ( " + ((GtfsShapeRecord) gtfsShapeRecords.get(i)).shape_id + ", ST_GeomFromText('LINESTRING(";

                //System.out.println(i + " " + ((GtfsShapeRecord) gtfsRecords.get(i)).shape_id);


            }
            query = query + ((GtfsShapeRecord) gtfsShapeRecords.get(i)).shape_pt_lon + " " + ((GtfsShapeRecord) gtfsShapeRecords.get(i)).shape_pt_lat + " ";
            //    System.out.println(((GtfsShapeRecord) gtfsRecords.get(i)).shape_id);

            if ((i != gtfsShapeRecords.size() - 1) && ((GtfsShapeRecord) gtfsShapeRecords.get(i)).shape_id != ((GtfsShapeRecord) gtfsShapeRecords.get(i + 1)).shape_id) {
                query = query + ")'));";
                System.out.println(query);
                REPLAN.getController().getConnection().createStatement().executeUpdate(query);
            } else if (i == gtfsShapeRecords.size() - 1) {
                query = query + ")'));";
                System.out.println(query);
                REPLAN.getController().getConnection().createStatement().executeUpdate(query);
                //System.out.println("Processed " + i + 1 + " point records");
            } else {
                query = query + ",";
            }
            last = ((GtfsShapeRecord) gtfsShapeRecords.get(i)).shape_id;
            //  System.out.println(query);


        }
        System.out.println("Task Complete!");
    }
}
