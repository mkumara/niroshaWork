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
import org.json.JSONObject;
import org.json.XML;

public class TAMU_Geocoding {
    
    // Get contents from HTTP request given a URL
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
    
    public String[] getData_TAMU_Geocoder(String address){
        String TAMU_url = "http://geoservices.tamu.edu/Services/Geocode/WebService/GeocoderWebServiceHttpNonParsed_V04_01.aspx?"
                +         address
                +         "&apikey=903089c411a84a9198467beb0268fcc4&format=XML&census=false&notStore=false&version=4.01";
        
        //System.out.println(TAMU_url);
        
        String[] finalLatLon = new String[2];
        
        JSONObject json = null;
        JSONArray aJson = null;
        
       

            try {
                          
                json = XML.toJSONObject(readUrl(TAMU_url)); 
                
                finalLatLon[0] = json.getJSONObject("WebServiceGeocodeResult").getJSONObject("OutputGeocodes").getJSONObject("OutputGeocode").get("Latitude").toString();
                finalLatLon[1] = json.getJSONObject("WebServiceGeocodeResult").getJSONObject("OutputGeocodes").getJSONObject("OutputGeocode").get("Longitude").toString();
                //System.out.println("Lat = " + finalLatLon[0]);
                //System.out.println("Lon = " + finalLatLon[1]);
                
                return finalLatLon;
                
                
            } catch (Exception ex) {
                System.out.println("failed to get data from TAMU's geocoding");
                int option = JOptionPane.showConfirmDialog(null,"This feature will be disabled for the rest of this session,\nPlease try again later.","Geocoding Service Currently Unavailabe.", JOptionPane.OK_OPTION);
                return null;
        }
    }
    
    
}
