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
public class CalculateRates {

    public static void main(String args[]) throws SQLException {
        // To set up this table initially:
        // SELECT population AS pop, sum_disease AS cases, substring(dec_10_sf1 from 10 for 15) AS geoid10 INTO martyo.denton_block_rates FROM martyo.denton_synthdata_blocks;

        REPLAN replan = new REPLAN();
        Connection c = REPLAN.getController().getConnection();
        for (int i = 3; i < 150; i += 2) {
            // create new db columns - need to create another column for which partition each block is in

            String query_create_columns = "ALTER TABLE martyo.denton_block_rates ADD COLUMN p" + i + "_partition integer, ADD COLUMN p" + i + "_pop integer, ADD COLUMN p" + i + "_cases integer, ADD COLUMN p" + i + "_prop double precision;";
            System.out.println(query_create_columns);
            c.createStatement().executeUpdate(query_create_columns);

            for (int j = 1; j <= i; j++) {
                String query = "SELECT  "
                        + "sum(synth.population) AS pop, "
                        + "sum(sum_disease) AS cases "
                        + "FROM  martyo.denton_synthdata_blocks AS synth "
                        + "JOIN martyo.denton_county_113_partitions_p2b AS part "
                        + "ON  part.geoid10 = substring(dec_10_sf1 from 10 for 15) "
                        + "WHERE  part.partition_id=" + j + ";";
                System.out.println(query);

                ResultSet results = c.createStatement().executeQuery(query);
                results.next();
                double rate;
                if (results.getInt("pop") > 0) {
                    rate = (double) results.getInt("cases") / (double) results.getInt("pop");
                } else {
                    rate = 0;
                }
                // problem: need to get partition for each block
                query = "UPDATE "
                        + "martyo.denton_block_rates AS rates "
                        + "SET p" + i + "_partition=part.partition_id "
                        + "FROM martyo.denton_county_" + i + "_partitions_p2b AS part "
                        + "WHERE  "
                        + "part.partition_id=" + j + " "
                        + "AND part.geoid10 = rates.geoid10;";
                System.out.println(query);
                c.createStatement().executeUpdate(query);

                query = "UPDATE martyo.denton_block_rates SET p" + i + "_pop = " + results.getInt("pop") + ", p" + i + "_cases = " + results.getInt("cases") + ", p" + i + "_prop =" + rate + " WHERE p" + i + "_partition=" + j + ";";
                System.out.println(query);
                // populate new db columns
                c.createStatement().executeUpdate(query);
            }
        }
        System.out.println("********************************* Done! *********************************");
    }
}
