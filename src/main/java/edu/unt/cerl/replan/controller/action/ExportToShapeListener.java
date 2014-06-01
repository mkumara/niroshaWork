/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller.action;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.view.mainframe.MainFrame;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.CreateShape;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.geotools.feature.SchemaException;

/**
 *
 * @author sarat
 */
public class ExportToShapeListener implements ActionListener {

    //private GISConversionTools gisConvTools;
    private MainFrame owner;

    public ExportToShapeListener(MainFrame owner) {
        //this.gisConvTools = new GISConversionTools();
        this.owner = owner;
    }

    private void exportTable(Statement stmt, String prefix, String tableName, String suffix, String geomType, String geomName) throws MalformedURLException, IOException, SchemaException, SQLException {
        String path = prefix + suffix + ".shp";
        String query = "INSERT INTO geometry_columns VALUES ('','" + UserState.userId + "','" + tableName + suffix + "','" + geomName + "',2,4326,'" + geomType + "');";
        System.out.println(query);
        stmt.executeUpdate(query);
        System.out.println(tableName + suffix);
        System.out.println(path);
        //  System.out.println(gisConvTools.getPOSTGIS_USERSCHEMA().size());
        CreateShape.createShpFromPostgis(tableName + suffix, new File(path));
        query = "DELETE FROM geometry_columns WHERE f_table_schema = '" + UserState.userId + "' AND f_table_name = '" + tableName + suffix + "';";
        System.out.println(query);
        stmt.executeUpdate(query);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            //this.gisConvTools = new GISConversionTools();
            //Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());

            Statement stmt = REPLAN.getController().getConnection().createStatement();
            String query;
            JFileChooser fc = new JFileChooser();
            FileFilter filter = new ExtensionFileFilter("Shapefiles", new String[]{"shp"});
            fc.setFileFilter(filter);
            int returnVal = fc.showSaveDialog(owner);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (path.endsWith(".shp")) {
                    path = path.substring(0, path.length() - 5);
                }
                ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
                String tableName = state.getWorkingCopyName();
//                if (gisConvTools.getPOSTGIS_USERSCHEMA().isEmpty()) {
//                    gisConvTools.initUserSchemaProperties();
//                }
                if (state.arePodsSelected()) {
                    this.exportTable(stmt, path, tableName, DefaultConstants.POD_SUFFIX, "POINT", "location");
                }
                if (state.getCatchmentAreasGiven()) {
                    this.exportTable(stmt, path, tableName, DefaultConstants.CATCHMENT_SUFFIX, "MULTIPOLYGON", DefaultConstants.GEOM_FIELD);
                }
                if (state.isTrafficAnalysisPerformed()) {
                    this.exportTable(stmt, path, tableName, DefaultConstants.RINGS_SUFFIX, "MULTIPOLYGON", DefaultConstants.GEOM_FIELD);
                    this.exportTable(stmt, path, tableName, DefaultConstants.CROSSINGPT_SUFFIX, "POINT", "crossing_point");
                }
                if (state.isCoverageAnalysisPerformed()) {
                    this.exportTable(stmt, path, tableName, DefaultConstants.COVERAGE_SUFFIX, "MULTIPOLYGON", DefaultConstants.GEOM_FIELD);
                }

            }

            //c.close();
            //} catch (MalformedURLException ex) {
            //   Logger.getLogger(ExportToShpListener.class.getName()).log(Level.SEVERE, null, ex);
            //} catch (IOException ex) {
            //   Logger.getLogger(ExportToShpListener.class.getName()).log(Level.SEVERE, null, ex);
            //} catch (SchemaException ex) {
            //    Logger.getLogger(ExportToShpListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ExportToShapeListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportToShapeListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SchemaException ex) {
            Logger.getLogger(ExportToShapeListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ExportToShapeListener.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("RE-PLAN: Error exporting to shapefiles");
        }

    }

    class ExtensionFileFilter extends FileFilter {

        String description;
        String extensions[];

        public ExtensionFileFilter(String description, String extension) {
            this(description, new String[]{extension});
        }

        public ExtensionFileFilter(String description, String extensions[]) {
            if (description == null) {
                this.description = extensions[0];
            } else {
                this.description = description;
            }
            this.extensions = (String[]) extensions.clone();
            toLower(this.extensions);
        }

        private void toLower(String array[]) {
            for (int i = 0, n = array.length; i < n; i++) {
                array[i] = array[i].toLowerCase();
            }
        }

        public String getDescription() {
            return description;
        }

        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            } else {
                String path = file.getAbsolutePath().toLowerCase();
                for (int i = 0, n = extensions.length; i < n; i++) {
                    String extension = extensions[i];
                    if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
