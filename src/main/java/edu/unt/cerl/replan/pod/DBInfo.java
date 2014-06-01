/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.pod;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tamara
 */
public class DBInfo {

    private Map<String, String> settings;

    public DBInfo() {
        settings = new HashMap<String,String>();
        this.readParams();
    }

    public static void main(String args[]) {

        new DBInfo();
    }

    private void readParams() {
        try {
            File file = new File("DatabaseSettings.txt");
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if(line.startsWith("%")){
                    continue;
                }
                line = line.replaceAll("\\s", "");
                if (line.length() == 0){
                    continue;
                }
                String[] tokens = line.split("=");
                settings.put(tokens[0], tokens[1]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Map<String,String> getParams(){
        return this.settings;
    }
}
