/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.pod;

import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author angel
 */
public class Entries {
    final static int NUM  = 7;
    public JTextField objectEntries[] = new JTextField[NUM];
    JTextArea comments = new JTextArea();

    public Entries (){
        int i;
        for( i = 0 ; i < NUM - 1 ; i++){
          objectEntries[i] = new JTextField( "" );
        }//endFor
        comments = new JTextArea("");
    }

    public void initialize( String initialized ){
        int i;
        for( i = 0 ; i < NUM - 1 ; i++){
          objectEntries[i] = new JTextField("");
        }//endFor
        objectEntries[0].setText( initialized );
        System.out.println( "Bestia" +  objectEntries[0].getText() );
        comments.setText("");

    }

    public void update_name( String initialized ){

        objectEntries[0].setText(initialized);
        System.out.println( "Updated with: " +  objectEntries[0].getText() );

    }

    
    public void write( JTextField[] userEntries, JTextArea area ){

        int i = 0;
        for( i = 0 ; i < NUM - 1 ; i++){
          System.out.println( userEntries[i].getText() );
          objectEntries[i] = new JTextField( userEntries[i].getText() );
        }//endFor        
        //objectEntries[i] = new JTextField( area.getText() );
        System.out.println( comments.getText() );
        comments = new JTextArea( area.getText() );
        //objectEntries[i].setText( userEntries[i].getText() );
    }//endentries

    public void write( String[] userEntries ){

        int i = 0;
        for( i = 0 ; i < NUM - 1 ; i++){
         // System.out.println( "Overwrite: " + userEntries[i] );
          objectEntries[i] = new JTextField( userEntries[i] );
        }//endFor
        //objectEntries[i] = new JTextField( area.getText() );
       // System.out.println( "Overwrite: " + userEntries[i] );
        comments.setText( userEntries[i] );
        //objectEntries[i].setText( userEntries[i].getText() );
    }//endentries




    public String readTextField( int j){
        return objectEntries[j].getText();
    }//endentries

    public String readComments( ){
        return comments.getText();
    }//endentries

}//endClass
