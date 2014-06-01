package edu.unt.cerl.replan.controller.db;

import java.nio.charset.Charset;


/**
 * Interface ImportCSV2DB
 */
public interface ImportCSV2DB {

  //
  // Fields
  //

  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  //
  // Other methods
  //

  /**
   * @param        newTableName
   * @param        filename
   * @param        delimeter
   * @param        charset
   */
  public void readCsvToTable( String newTableName, String filename, char delimeter, Charset charset );


}
