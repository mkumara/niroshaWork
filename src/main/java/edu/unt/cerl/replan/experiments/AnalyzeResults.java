/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.experiments;

import edu.unt.cerl.replan.REPLAN;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author martyo
 */
public class AnalyzeResults {

    public static void main(String args[]) throws SQLException {
        REPLAN replan = new REPLAN();
        Connection c = REPLAN.getController().getConnection();

        String query = "SELECT * FROM martyo.denton_block_rates;";
        System.out.println(query);
        ResultSet results = c.createStatement().executeQuery(query);
        /*
        query = "CREATE TABLE martyo.denton_block_rates_analysis(geoid10 text, pop integer, cases integer, proportion double precision);";
        System.out.println(query);
        c.createStatement().executeUpdate(query);
         */
        query = "SELECT geoid10, pop, cases INTO martyo.denton_block_rates_analysis FROM martyo.denton_block_rates;";
        System.out.println(query);
        c.createStatement().executeUpdate(query);

        query = "ALTER TABLE martyo.denton_block_rates_analysis ";
        for (int i = 3; i < 150; i += 2) {
            if (i > 3) {
                query = query + ", ";
            }
            query = query + "ADD COLUMN up_to_" + i + " double precision";
        }
        query = query + ";";
        System.out.println(query);
        c.createStatement().executeUpdate(query);

        while (results.next()) { // go through each record, one at a time
            double proportion = 0;
            if (results.getInt("pop") > 0) {
                proportion = ((double) results.getInt("cases") / (double) results.getInt("pop"));
            }
            //query = "UPDATE martyo.denton_block_rates_analysis SET geoid10='" + results.getString("geoid10") + "', pop=" + results.getInt("pop") + ", cases=" + results.getInt("cases") + ", proportion=" + proportion + ";";
            //System.out.println(query);
            //c.createStatement().executeUpdate(query);
            double sum = 0;
            int numPartitionings = 0;
            query = "UPDATE martyo.denton_block_rates_analysis SET ";

            for (int i = 3; i < 150; i += 2) {
                // go through each partitioning of each record, one at a time
                numPartitionings++;
                sum += results.getDouble("p" + i + "_prop");
                if (i > 3) {
                    query = query + ", ";
                }

                query = query + "up_to_" + i + "=" + sum / numPartitionings;
            }
            query = query + " WHERE geoid10='" + results.getString("geoid10") + "';";
            System.out.println(query);
            c.createStatement().executeUpdate(query);
        }
    }
}
