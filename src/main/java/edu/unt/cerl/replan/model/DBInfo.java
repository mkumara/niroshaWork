package edu.unt.cerl.replan.model;

import java.util.*;


/**
 * Class DBInfo
 */
public class DBInfo {

  //
  // Fields
  //

  private Map<String,String> getParams;
  
  //
  // Constructors
  //
  public DBInfo () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of getParams
   * @param newVar the new value of getParams
   */
  private void setGetParams ( Map<String,String> newVar ) {
    getParams = newVar;
  }

  /**
   * Get the value of getParams
   * @return the value of getParams
   */
  private Map<String,String> getGetParams ( ) {
    return getParams;
  }

  //
  // Other methods
  //

  /**
   * @param        params
   */
  public void setParams( Map<String,String> params )
  {
  }


  /**
   * @return       Map<String,String>
   */
  public Map<String,String> getParams(  )
  {
      return null;
  }


}
