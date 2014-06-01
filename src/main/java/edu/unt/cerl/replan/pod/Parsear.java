package edu.unt.cerl.replan.pod;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Parsear{


	public String read_info_file(String file_name) throws FileNotFoundException, IOException{
//        int index = 0;
//        String Field[];
        File file = new File(file_name);
        String line = null;
        String field = "";
        BufferedReader bufRdr  = new BufferedReader(new FileReader(file));
        //read each line of text file
        while((line = bufRdr.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line,":");
            while ( st.hasMoreTokens() ){
                //get next token and store it in the array
                field = st.nextToken() ;
                System.out.println(field);
            }//endWhile
        }//endWhile
        bufRdr.close();
        return field;
    }//endread_info


	public String parse_string(String str){

        String field = null;
        //read each line of text file
            System.out.println( str );
        StringTokenizer st = new StringTokenizer(str,":");
        while ( st.hasMoreTokens() ){
            //get next token and store it in the array
            field = st.nextToken() ;
            System.out.println( "Object: " + field );
        }//endWhile
        return field;
    }//endread_info






}