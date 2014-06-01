/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.experiments;

import edu.unt.cerl.replan.REPLAN;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author martyo
 */
public class Partitions2Blocks {

    public static void main(String args[]) throws SQLException{
        REPLAN replan = new REPLAN();
         Connection c = REPLAN.getController().getConnection();
         for (int i=3; i<150;i+=2){
         String query = "SELECT blocks.geoid10, c2p.partition_id, blocks.logrecno INTO martyo.denton_county_"+i+"_partitions_p2b FROM denton_census_blocks  AS blocks, ( SELECT blocks.*, partitions.id AS partition_id FROM denton_census_blocks_centroids AS blocks, martyo.denton_county_"+i+"_partitions AS partitions WHERE ST_WITHIN(blocks.centroid, partitions.the_geom) ) AS c2p WHERE blocks.logrecno = c2p.logrecno ;";
        System.out.println(i);

        c.createStatement().executeUpdate(query);

        }
    }

}
