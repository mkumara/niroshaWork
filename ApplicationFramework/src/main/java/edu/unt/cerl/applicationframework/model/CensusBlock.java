package edu.unt.cerl.applicationframework.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import org.hibernatespatial.postgis.PGGeometryUserType;

/**
 *
 * @author tamara
 */
public class CensusBlock {

    private int id;
    private org.postgis.Point pg_centroid;
    private Point centroid;
    private MultiPolygon p;
    private int population;
    private int pod;
    private org.postgis.MultiPolygon pg_p;


    public CensusBlock(int id, org.postgis.Point pg_centroid, int population) {
      this.initAndConvertToJTS(id, pg_centroid, population, null);
    }

    public CensusBlock(int id, org.postgis.Point pg_centroid, int population, org.postgis.MultiPolygon pg_p) {
        this.initAndConvertToJTS(id, pg_centroid, population, pg_p);
    }

    private void initAndConvertToJTS(int id, org.postgis.Point pg_centroid, int population, org.postgis.MultiPolygon pg_p){
                this.id = id;
        this.pg_centroid = pg_centroid;
        this.population = population;
             this.pg_p = pg_p;
             if(this.pg_p != null){
                 PGGeometryUserType converter = new PGGeometryUserType();
                 this.p = (MultiPolygon) converter.convert2JTS(pg_p);
                 this.centroid = (Point) converter.convert2JTS(pg_centroid);
             }

    }

    public int getId() {
        return this.id;
    }

    public org.postgis.Point getPGCentroid() {
        return this.pg_centroid;
    }

    public Point getCentroid(){
        return this.centroid;
    }

    public int getPopulation() {
        return this.population;
    }

    public void setPod(int pod) {
        this.pod = pod;
    }

    public int getPod() {
        return this.pod;
    }

    public org.postgis.MultiPolygon getPGPolygon() {
        return this.pg_p;
    }

    public MultiPolygon getPolygon(){
        return this.p;
    }

    public String toString() {
        return this.id + " - " + this.pg_centroid + " - " + this.pod + " - " + this.population ;
    }
}
