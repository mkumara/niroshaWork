package tools;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
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
    private Map<String, String> tableParams;

    public DBInfo() {
        settings = new HashMap<String, String>();
        this.readParams();
        this.readTableParams();
    }

    public static void main(String args[]) {

        new DBInfo();
    }

    private void readParams() {
        try {
            File file = new File("DatabaseSettings.txt");
            Scanner s = new Scanner(file);
        //URL resUrl = this.getClass().getResource("/DatabaseSettings.txt");
        
        //Scanner s = new Scanner(is);
            //Scanner s = new Scanner(DBInfo.class.getResourceAsStream("/DatabaseSettings.txt"));
            //Scanner s = new Scanner("DatabaseSettings.txt");
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("%")) {
                    continue;
                }
                line = line.replaceAll("\\s", "");
                if (line.length() == 0) {
                    continue;
                }
                String[] tokens = line.split("=");
                settings.put(tokens[0], tokens[1]);
            }
        } catch (IOException ex) {
            Logger.getLogger(DBInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Map<String, String> getParams() {
        return this.settings;
    }
    
    public Map<String,String> getTableParams(){
        return this.tableParams;
    }

    private void readTableParams() {
        tableParams = new HashMap();
        try {

            File file = new File("Settings.txt");
                //URL resUrl = this.getClass().getResource("Settings.txt");
        //Scanner s = new Scanner(resUrl.openStream());
            //Scanner s = new Scanner(DBInfo.class.getResourceAsStream("/Settings.txt"));
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("%")) {
                    continue;
                }
                line = line.replaceAll("\\s", "");
                if (line.length() == 0) {
                    continue;
                }
                String[] tokens = line.split("=");
                tableParams.put(tokens[0], tokens[1]);
            }
        } catch (IOException ex) {
            Logger.getLogger(DBInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
