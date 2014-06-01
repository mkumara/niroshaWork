package edu.unt.cerl.replan.maintainance;


import edu.unt.cerl.applicationframework.controller.GISConverter;
import edu.unt.cerl.replan.controller.db.DBController;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * This class takes in a shapefile and stores it in the database
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class ReadShpIntoDB {

    //
    // DEFINE FILE AND TABLE NAMES HERE!!!
    //
    private static final File f =
            new File(
            "census2010/tl_2010_48121_tabblock10_Denton/tl_2010_48121_tabblock10.shp");
    private static final String table = "denton_census_blocks";

    public static void main(String args[]) throws IOException,
        ClassNotFoundException, SQLException {
        DBController db = new DBController();
        Map params = db.getPostGIS();
        params.remove("role");
        params.remove("schema");
        params.put("schema", "public");
        if (params == null){
            System.out.println("NULL!!!!");
        }
        GISConverter.createPostgisFromShp(f, table, params, false);
    }
}
