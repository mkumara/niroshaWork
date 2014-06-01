/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DemoPackage;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.db.DBQueries;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class DatabaseExample {

    DatabaseExample(){
        DBQueries queries = REPLAN.getQueries();
        queries.addNewPOD(5);

        // 1. Goto interface DBQueries and modify parameters in interface
        // 2. Modify DBQueriesJava and DBQueriesPostgreSQL to match interface
        // 3. Call approriate query object, e.g. PODQueries with identical method
        //    name (create appropriate method)
    }


}
