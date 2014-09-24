
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataOperations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class SummarizePodStatsFromDB {

    private int numPods;
    private Statement stmt;

//	public static void main(String args[]) {
//		SummarizePodStats sps = new SummarizePodStats();
//		sps.summarizePopulation();
//		sps.getBooths();
//	}
    public SummarizePodStatsFromDB(int numPods) {
        this.numPods = numPods;
        
    }

    /**
     * adds up the population for the individual catchment areas
     * @return
     * 		array filled with the population count for each of the catchment areas
     */
    public int[] summarizePopulation(ResultSet population) {
        int[] populationCount = new int[numPods];
        for (int i = 0; i < populationCount.length; i++) {
            populationCount[i] = 0;
        }
        try {
            population.beforeFirst();
            int id = 0;
            while (population.next()) {
                populationCount[id] = population.getInt("population");
                id++;
            }

        } catch (SQLException ex) {
            Logger.getLogger(SummarizePodStatsFromDB.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("PODAnalyzer: Error retrieving population sizes from DB");
        }

        return populationCount;
    }


        public String[] getNames(ResultSet pods) {
        String[] names = new String[numPods];
        for (int i = 0; i < names.length; i++) {
             names[i] = "";
        }
        try {
            pods.beforeFirst();
            int id = 0;
            while (pods.next()) {
                names[id] = pods.getString("name");
                id++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SummarizePodStatsFromDB.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("PODAnalyzer: Error retrieving names from DB");
        }
        return names;
    }

    /**
     * read in the number of booths per POD
     * @return
     * 		array filled with the number of booths for each of the PODs
     */
    public double[] getBooths(ResultSet pods) {
        double[] boothCount = new double[numPods];
        for (int i = 0; i < boothCount.length; i++) {
            boothCount[i] = 0;
        }
        try {
            pods.beforeFirst();
            int id = 0;
            while (pods.next()) {
                boothCount[id] = pods.getInt("numbooths");
                id++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SummarizePodStatsFromDB.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("PODAnalyzer: Error retrieving booth count from DB");
        }
        return boothCount;
    }

     public int[] getIds(ResultSet pods) {
        int[] ids = new int[numPods];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = 0;
        }
        try {
            pods.beforeFirst();
            int id = 0;
            while (pods.next()) {
                ids[id] = pods.getInt("id");
                id++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SummarizePodStatsFromDB.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("PODAnalyzer: Error retrieving ids from DB");
        }
        return ids;
    }

}
