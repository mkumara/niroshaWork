/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.controller.action;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.ReadPODsCSV;
import edu.unt.cerl.replan.model.PODList;
import edu.unt.cerl.replan.view.PODEditor_View;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

import java.util.Observable;
import javax.swing.JFileChooser;

/**
 *
 * @author sarat
 */
public class LoadPodsFromCSV{
    //private Map<Integer, Entries2> m = new HashMap<Integer, Entries2>();

    private PODList podList;
    private Integer count = 0;

    public LoadPodsFromCSV(PODEditor_View podEditor) {

//this.addObserver(podEditor);
        this.podList = podEditor.getPodList();

        
        // open file selection dialog box
        JFileChooser fc = new JFileChooser();

        int returnVal = fc.showOpenDialog(podEditor);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {

                // Here is how to get started using the newer methods of importing PODs from CSV
                File file = fc.getSelectedFile();
                Charset myCharset = Charset.forName("UTF-8");
                ReadPODsCSV myFile;

                //GISConversionTools gisConvTools = new GISConversionTools();
                //LinkedList<String[]> pods = new LinkedList();
                //gisConvTools.createPostgisFromPODs(file.getPath(),scenario.getWorkingName() + "_pods", gisConvTools.getPOSTGIS(), pods);


                try {
                    
                    
                    myFile = new ReadPODsCSV(file.getPath(), ',', myCharset); // note: you will want to change the '|' to a ',' for comma-delimited files
                    int numRecords = myFile.returnNumRecords();
                    for (int i = 0; i < numRecords; i++) {
                        podList.add_pod_dontNotify(podEditor.getScenarioState(), myFile.returnName(i), myFile.returnAddress(i), myFile.returnCity(i), myFile.returnZip(i), myFile.returnLongitude(i), myFile.returnLatitude(i), myFile.returnType(i), myFile.returnStatus(i), myFile.returnComments(i), myFile.returnNumBooths(i));
                        count++;
                    }//endFOR
                    //updatePodsinDB(podList);
                    podList.setListChanged();
                    podEditor.setPodList(podList);
                    //podEditor.getPodList().setListChanged();
                    
                    
                    //REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().setPodsChanged();
                    //REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().setChangedAndNotify();
                    
                    
                } catch (SQLException ex) {
                }
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
                //Logger.getLogger(PODToolBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//                            Entries2 e;
//                            Map<String, String> entry;
//
//                    String name = null;
//                    String concat = null;
//                    String typeOfPod = null;
//                    //int typeOfPodInteger;
//                    Color textcolor = null;
//                    //Iterator myVeryOwnIterator = m.keySet().iterator();
//                    String str = null;
//                    Integer id = new Integer(0);

        //Integer iterator = new Integer(0);


        //POD pod;
        //System.out.println(myFile.returnId(i) + " " + myFile.returnName(i) + " " + myFile.returnAddress(i) + " " + myFile.returnCity(i) + " " + myFile.returnZip(i) + " " + myFile.returnLongitude(i) + " " + myFile.returnLatitude(i) + " " + myFile.returnAdditional(i) + " " + myFile.returnComments(i) + " " + myFile.returnNumBooths(i) + " " + myFile.returnType(i));

        // build map to use for adding pod
//                        id = count + 1;
//                                entry = new HashMap();
//                                entry.put("name", myFile.returnName(i));
//                                entry.put("addy", myFile.returnAddress(i));
//                                entry.put("city", myFile.returnCity(i));
//                                entry.put("zip", myFile.returnZip(i));
//                                entry.put("lon", String.valueOf(myFile.returnLongitude(i)));
//                                entry.put("lat", String.valueOf(myFile.returnLatitude(i)));
//                                entry.put("booths", String.valueOf(myFile.returnNumBooths(i)));
//                                entry.put("additional", myFile.returnAdditional(i));
//                                entry.put("comments", myFile.returnComments(i));
//                                entry.put("on", "true");
//                                entry.put("fid", fid.toString());
//                                entry.put("is_public", myFile.returnType(i));
//                                entry.put("id", id.toString());

        //podTools.addNewPOD(entry);


        // for display in pod editor:
//                        str = Integer.toString(count + 1);
//
//                        //fidId.put(new Integer(count + 1), Integer.parseInt(entry.get("fid")));
//                        name = entry.get("name"); //myFile.returnName(i); //e.readAddress();//  objectEntries[ 0 ].getText();
//
//                        System.out.println("Name:" + name);
//                        typeOfPod = entry.get("is_public"); // e.readTypeOfPod();
//                        System.out.println("TypeOfPod: " + typeOfPod);
//                        if (typeOfPod.equals("public")) {
//                            //typeOfPodInteger = 0;
//                            textcolor = Color.black;
//                        }
//                        if (typeOfPod.equals("corporate")) {
//                            //typeOfPodInteger = 1;
//                            textcolor = Color.blue;
//                        }
//                        if (entry.get("on").equals("true")) {
//                            diamondcolor = Color.green;
//                        } else {
//                            diamondcolor = Color.red;
//                        }
//
//
//                        concat = name + ":" + str;
//                        //System.out.println( concat );
//                        Object[] obj = {
//                            new Font("Helvetica", Font.PLAIN, 15), textcolor, //Color.black,
//                            new DiamondIcon(diamondcolor), concat
//                        };

//                        listModel.add(count, obj);
//
//                        e = new Entries2();
//                        e.writeMap(entry);
//                        e.hashIntoTextField(entry);
//                        m.put(new Integer(fid), e);
//                        // for (int testI = 1; testI < fid; testI++) {
//
//                        //     System.out.println(testI + " Big Loop: " + m.get(new Integer(testI)).readFID());
//                        // }
//                        //System.out.println("outside writeMap: " + e.readFID());
//                        count++;
//                        fid++;


//                    scenario.reRender(); // for displaying the updates to the map
//
//                    if (count > 0) {
//
//                        list.setSelectedIndex(0);
//
//                        //jcombobox.setSelectedIndex(0);
//
//                        for (int i = 0; i < SIZE - 1; i++) {
//                            jTextField[i].setEnabled(true);
//                            //jTextField[i].enable(true);
//                        }//endFOR
//
//                        jTextArea1.setEnabled(true);
//                        jcombobox.setEnabled(true);
//                        jcheckbox.setEnabled(true);
//                        System.out.println("My count: " + count);
//                        readPOD(0);
//                        globalListIndex = 0;
//                        prev = false;
//
//                    }//endIF



        //String toUpdate = "";

        //%%%%%%%%%%%%%%%%%%%%%%%%%%%

        //String myFilePath = file.getPath();
                           /*
        GISConversionTools gisConvTools = new GISConversionTools();
        LinkedList<String[]> pods = new LinkedList();
        try {
        gisConvTools.createPostgisFromPODs(file, scenario.getWorkingName() + "_pods", gisConvTools.getPOSTGIS(), pods);
        } catch (ClassNotFoundException ex) {
        Logger.getLogger(Jpod2.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection c = DriverManager.getConnection(gisConvTools.getJDBC_CONNECTION_STRING(), gisConvTools.getJDBC());
        if (!DBTools.entryExists("workingcpy_timestamps", "author", UserState.userId, "name", scenario.getWorkingName(), c)) {
        ScenarioOperations.insertTimestamp(UserState.userId, scenario.getWorkingName(), c);
        }
         */
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%


//
//                } catch (SQLException ex) {
//                    Logger.getLogger(Jpod2.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (FileNotFoundException ex) {
//                    Logger.getLogger(Jpod2.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//
//
//                // *******************************************************
//            } catch (IOException ex) {
//                Logger.getLogger(PODToolBox.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//
//        try {
//            //barra.kill();
//        } catch (Throwable ex) {
//            Logger.getLogger(Jpod2.class.getName()).log(Level.SEVERE, null, ex);
//        }


    }

    private void updatePodsinDB(PODList list) {
        System.out.println("Number of pods in updatePodsinDB: "+list.get_number_of_pods()+"\n");
        for (int id = 1; id <= (list.get_number_of_pods()); id++) {
            list.access_POD_by_id(id);
            String name = list.access_POD_by_id(id).get_name();
            String address = list.access_POD_by_id(id).get_address();
            String city = list.access_POD_by_id(id).get_city();
            String zip = list.access_POD_by_id(id).get_zip();
            double longitude = list.access_POD_by_id(id).get_longitude();
            double latitude = list.access_POD_by_id(id).get_latitude();
            Boolean type = list.access_POD_by_id(id).get_type();
            Boolean status = list.access_POD_by_id(id).get_status();
            String comments = list.access_POD_by_id(id).get_comments();
            int numBooths = list.access_POD_by_id(id).get_numBooths();
            try{
            list.update_pod(id, name, address, city, zip, longitude, latitude, type, status, comments, numBooths);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
