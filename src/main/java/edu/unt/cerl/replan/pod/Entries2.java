/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.pod;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 *
 * @author angel
 */
public class Entries2 {
    private final static int NUM  = 8;
    private JTextField objectEntries[] = new JTextField[NUM];
    private JTextArea comments = new JTextArea("");
    private String typeofpod = "public";
    private String on = "true";
    private String fid = "";
    private String id = "";
    private Map<String, String> m = new HashMap<String,String>();

    public Entries2 (){
        int i;
        for( i = 0 ; i < NUM - 1 ; i++){
          objectEntries[i] = new JTextField( "" );
        }//endFor
        //comments = new JTextArea("");
//        typeofpod = "Public";

        m.put( "name",  objectEntries[0].getText() );
        m.put( "addy",  objectEntries[1].getText() );
        m.put( "city",  objectEntries[2].getText() );
        m.put( "zip",   objectEntries[3].getText().toString() );
        m.put( "lon",   objectEntries[4].getText().toString() );
        m.put( "lat",   objectEntries[5].getText().toString() );
        m.put( "booths",objectEntries[6].getText().toString() );
        m.put( "status", "true" );
        m.put( "comments", comments.getText() );        
        m.put( "on" , on );
        m.put( "fid", fid);
        m.put( "is_public", typeofpod );
        m.put( "id", id);
    }

    public Entries2( String [] loaded){
        int i;
        for( i = 0 ; i < NUM - 1 ; i++){
          objectEntries[i] = new JTextField( loaded[i] );
          System.out.println(objectEntries[i].getText());
        }//endFor
        //comments = new JTextArea("");
//        typeofpod = "Public";

        m.put( "name",  objectEntries[0].getText() );
        m.put( "addy",  objectEntries[1].getText() );
        m.put( "city",  objectEntries[2].getText() );
        m.put( "zip",   objectEntries[3].getText().toString() );
        m.put( "lon",   objectEntries[4].getText().toString() );
        m.put( "lat",   objectEntries[5].getText().toString() );
        m.put( "booths",objectEntries[6].getText().toString() );
        m.put( "status", "true" );
        m.put( "comments", comments.getText() );
        m.put( "on" , on );
        m.put( "fid", fid);
        m.put( "is_public", typeofpod );
        m.put( "id", id);
    }



    public void initialize( String initialized, String dbnum, String booth ){
        int i;
        for( i = 0 ; i < NUM - 1 ; i++){
          objectEntries[i] = new JTextField("");
        }//endFor
        objectEntries[0].setText( initialized );
        objectEntries[3].setText( "00000" );
        objectEntries[6].setText( booth );
        //System.out.println( "Bestia" +  objectEntries[0].getText() );
        comments.setText("");
        typeofpod = "public";
        on = "true";
        fid = dbnum;
        m.put("id", "");
        m.put( "name", objectEntries[0].getText() );
        m.put( "addy", objectEntries[1].getText() );
        m.put( "city", objectEntries[2].getText() );
        m.put( "zip",  objectEntries[3].getText().toString() );
        m.put( "lon",  objectEntries[4].getText().toString() );
        m.put( "lat",  objectEntries[5].getText().toString() );
        m.put( "status", "true");
        m.put( "comments", comments.getText() );
        m.put( "booths", objectEntries[6].getText() );
        m.put( "fid", fid );
        m.put( "is_public",typeofpod );

    }

    public void update_name( String initialized ){

        objectEntries[0].setText( initialized );
        System.out.println( "Updated with: " +  objectEntries[0].getText() );
        m.put("name", initialized);

    }


    public void write( JTextField[] userEntries, JTextArea area, String usertypepodentry, String active , String newId){

        int i = 0;
        for( i = 0 ; i < NUM - 1 ; i++){
          System.out.println( userEntries[i].getText() );
          objectEntries[i] = new JTextField( userEntries[i].getText() );
        }//endFor

        //objectEntries[i] = new JTextField( area.getText() );
        System.out.println( comments.getText() );
        comments.setText( area.getText() );
        typeofpod = usertypepodentry;
        on = active;
        id = newId;
        m.put( "name", objectEntries[0].getText() );
        m.put( "addy", objectEntries[1].getText() );
        m.put( "city", objectEntries[2].getText() );
        m.put( "zip",  objectEntries[3].getText().toString() );
        m.put( "lon",  objectEntries[4].getText().toString() );
        m.put( "lat",  objectEntries[5].getText().toString() );
        m.put( "booths", objectEntries[6].getText().toString() );
        m.put( "status", "true");
        m.put( "comments", comments.getText() );
        m.put( "on", active);
        m.put( "fid", "");
        m.put( "is_public",typeofpod );
        m.put("id", id);

    }//endentries

    public void writeMap( Map<String, String> hash){
        m = (Map<String, String>) hash;
        //System.out.println("Inside writeMap: " + m.get("fid"));
        objectEntries[0].setText( m.get( "name" ) );
        objectEntries[1].setText( m.get( "addy" ) );
        objectEntries[2].setText( m.get( "city" ) );
        objectEntries[3].setText( m.get( "zip" ) );
        objectEntries[4].setText( m.get( "lon" ) );
        objectEntries[5].setText( m.get( "lat" ) );
        objectEntries[6].setText( m.get( "booths" ) );
        comments.setText( m.get( "comments" ) );
        on = m.get("on");
        fid = m.get( "fid");
        typeofpod = m.get("is_public" );
    }


    public void writeFID( String out_fid ){
      fid = out_fid;
      m.put("fid", fid);
    }

    public String readFID( ){
        System.out.println("inside readFID: "+m.get("fid"));
      return m.get("fid");
    }

    public String readName( ){
        return m.get("name");
    }//endentries


    public String readAddress( ){
        return m.get("addy");
    }//endentries

    public String readTextField( int j){
        //System.out.println("My index: " + j);
        return objectEntries[j].getText();
    }//endentries

    public String readComments( ){
        return m.get("comments");
        //return comments.getText();
    }//endentries

    public String readTypeOfPod( ){
        //System.out.println();
        return m.get("is_public");
        //return typeofpod;
    }//endentries


    public String readActive( ){
        return m.get("on");
    }//endentries

    public Map<String, String> readPodInfo (){
        return m;
    }

    
    public void hashIntoTextField( Map<String, String> hash){
        m = (Map<String, String>) hash;
        objectEntries[0].setText( m.get( "name" ) );
        objectEntries[1].setText( m.get( "addy" ) );
        objectEntries[2].setText( m.get( "city" ) );
        objectEntries[3].setText( m.get( "zip" ) );
        objectEntries[4].setText( m.get( "lon" ) );
        objectEntries[5].setText( m.get( "lat" ) );
        objectEntries[6].setText( m.get( "booths" ) );
        comments.setText( m.get( "comments" ) );
        on = m.get("on");
        fid = m.get( "fid");
        typeofpod = m.get("is_public" );

    }


}//endClass
