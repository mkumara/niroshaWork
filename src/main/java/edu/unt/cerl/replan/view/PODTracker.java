/*
 * POD Tracker to save pods automatically and track thier information for changes
 * 
 */
package edu.unt.cerl.replan.view;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author josh
 */

public class PODTracker {
    
    String name,address,city,zip,comments, next;
    Float lon, lat;
    int booths,id,fid, index;
    boolean status, type, hasOne, hasChanges, isNew, inputInvalid, isDefault;
    
    // constructor
    public PODTracker(){
        hasOne = false;
        hasChanges = false;
        isDefault = false;
    }
    
    // set thet tracker to follow a new pod
    public void setNew(int _index, String _name, String _address, String _city, String _zip, String _comments,
                      Double _lon, Double _lat, int _booths, int _id, int _fid, boolean _status, boolean _type){

        // validate name of pod
        _name = makeValidPODName(_name);
        
        
        // create the PODTracker to keep track of the POD to update
        index = _index;
        name = _name;
        address = _address;
        city = _city;
        zip = _zip;
        comments = _comments;
        
        // convert to float, which is needed for saving, but the local list will give as a double
        lon = Float.parseFloat(Double.toString(_lon));
        lat = Float.parseFloat(Double.toString(_lat));
        
        booths = _booths;
        id = _id;
        fid = _fid;
        status = _status;
        type = _type;
        
        
    }
    
    // check for changes from the pods last save to the new input and return if any changes have occured
    public void checkForChanges(int _index, String _name, String _address, String _city, String _zip, String _comments,
                                    String _lon, String _lat, int _booths, int _id, int _fid, boolean _status, boolean _type){
        Double dLon, dLat;
        hasChanges = false;
        inputInvalid = false;
        isDefault = false;
        
        if (_lon.equals("0.0") && _lat.equals("0.0") && _name.equals("<New POD Name>")){
            isDefault = true;
        }

        if (_lon.isEmpty() || !PODTracker.isNumeric(_lon) || _lon.equals(" ")){
            _lon = "0.0";
            hasChanges = true;
            inputInvalid = true;
            dLon = Double.valueOf(_lon);
        }
        if (_lat.isEmpty() || !PODTracker.isNumeric(_lat) || _lat.equals(" ")){
            _lat = "0.0";
            hasChanges = true;
            inputInvalid = true;
            dLat = Double.valueOf(_lat);
        }

        dLon = Double.valueOf(_lon);
        dLat = Double.valueOf(_lat);

        // test for any changes from the pod was loaded, to when it was exited off
        if (index != _index || !name.equals(_name) || !address.equals(_address) || !city.equals(_city) || !zip.equals(_zip) ||
            !comments.equals(_comments)  || lon != dLon.floatValue() || lat.doubleValue() != dLat.floatValue() ||
            booths != _booths || status != _status || type != _type){
            // set status to has changes
            hasChanges = true; 
        }
        if (hasChanges == true){
            if (!isDefault) {
                // assign all the new values
                this.setNew(_index, _name, _address, _city, _zip, _comments, dLon, dLat, _booths, _id, _fid, _status, _type);
            }
            else{
                hasChanges = false;
            }
        }
    }
    
    // check if the pod needs to save or if it was just called again
    // mainly needed for the way that the pod list building the pods and helps
    // keeping from saving them as they are loading them also
    public boolean needsToSave(int currentIndex){
        if (this.isNew) {
            return true;
        }
        else {
            //if (next == null || name.equals(next)){
            if (index == currentIndex){  
                return false;
            }
            else{       
                return true;
            }
        }
    }
    
    // validation check to see if input is all numbers of some sort
    public static boolean isNumeric(String str){
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
    
    // validation check to see if pod name is compatible for db queries
    public static String makeValidPODName(String str){
        if (str.contains("'")){
            str = str.replaceAll("'", "");
        }
        return str;
    }
    
    // check if teh input given is correct and can be saved to the database
    // if the input is invalid, show a form and get corrections
    // place the defualt value 0.0 in all fields not corrected or still invalid after being changed
    public boolean hasValidInput(){
        boolean isValid = true;
        
        if (zip.isEmpty() || Integer.valueOf(zip) < 0 || inputInvalid){
            isValid = false;
            // create a form to get the new input
            JTextField lonF = new JTextField(20);
            JTextField latF = new JTextField(20);
            JTextField zipF = new JTextField(20);
               
            
            Object[] form = {
                "Zip Code:", zipF,
                "\n",
                "Longitude:",lonF,
                "\n",
                "Latitude:",latF,                
            };
            
            lonF.setText(lon.toString());
            latF.setText(lat.toString());
            zipF.setText(zip);
            
            int option = JOptionPane.showConfirmDialog(null, form, "Please Correct Loction Values", JOptionPane.OK_CANCEL_OPTION);

            if (!lonF.getText().isEmpty() && !PODTracker.isNumeric(lonF.getText())){
                lon = Float.valueOf(lonF.getText());
            }
            else {
                lon = Float.valueOf("0.0");                    
            }
            if (latF.getText().isEmpty() && !PODTracker.isNumeric(latF.getText())){
                lat = Float.valueOf(latF.getText());
            }
            else{
                lat = Float.valueOf("0.0");                    
            }
            if (zipF.getText().isEmpty()){
                zip = "00000";
                if (Integer.parseInt(zip) <= 0){
                    zip = "00000";
                }
            }              

            isValid = true;
           
        }    
        return isValid;
    }
}
