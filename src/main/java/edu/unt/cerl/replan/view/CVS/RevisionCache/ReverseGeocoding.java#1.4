package edu.unt.cerl.replan.view;

/**
 *
 * @author josh
 */

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

 
public class ReverseGeocoding {	
	
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read); 

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }

    }
    
    public String[] getData_TAMU_RGeocoder(String latLon){
        String TAMU_url = "http://geoservices.tamu.edu/Services/ReverseGeocoding/WebService/v04_01/HTTP/default.aspx?"
                +         latLon
                +         "&state=tx&apikey=903089c411a84a9198467beb0268fcc4&format=json&notStore=false&version=4.01";
        
        String[] finalAddress = new String[3];
        

            JSONObject json;
            JSONArray jsonA;
            try {
                // test lon&lat url:
                //json = new JSONObject(readUrl("http://maps.googleapis.com/maps/api/geocode/json?latlng=32.417037,-97.759602&sensor=true"));
                json = new JSONObject(readUrl(TAMU_url));
                
                System.out.println("======TAMU======");
                jsonA = json.getJSONArray("StreetAddresses");
                json = jsonA.getJSONObject(0);
                
                finalAddress[0] = json.getString("StreetAddress");
                finalAddress[1] = json.getString("City");
                finalAddress[2] = json.getString("Zip");
                
                System.out.println("street="+finalAddress[0]);
                System.out.println("city="+finalAddress[1]);
                System.out.println("zip="+finalAddress[2]);
                
                return finalAddress;
                
            } catch (Exception ex) {
                System.out.println("Could not read URL");
                int option = JOptionPane.showConfirmDialog(null,"This feature will be disabled for the rest of this session,\nPlease try again later.","Geocoding Service Currently Unavailabe.", JOptionPane.OK_OPTION);
                return null;
            }  

        
    }
    
    public String[] getData_Google_RGeocoder(String latLon){
        String[] finalAddress = new String[3];
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLon + "&sensor=true";
        
        try{
            JSONObject json = null;
            JSONArray jsonA = null;
            try {
                // test lon&lat url:
                //json = new JSONObject(readUrl("http://maps.googleapis.com/maps/api/geocode/json?latlng=32.417037,-97.759602&sensor=true"));
                json = new JSONObject(readUrl(url));
                jsonA = json.getJSONArray("results");
                //System.out.println(jsonA);
                json = jsonA.getJSONObject(0);
                //System.out.println(json);
                //System.out.println(json.getString("formatted_address"));
                String fullAddress = json.getString("formatted_address");
                String[] addressParts = fullAddress.split(",");
                
                finalAddress[0] = addressParts[0].trim(); // street
                finalAddress[1] = addressParts[1].trim(); // city                
                String separateZip[] = addressParts[2].trim().split(" ");
                finalAddress[2] = separateZip[1]; // zip
                
                return finalAddress;
                
                
                
            } catch (Exception ex) {
                System.out.println("Could not read URL");
                return null;
            }  
        } catch(JSONException e) {
            System.out.println("JSON expection!");
            return null;
        }
    }
   
}