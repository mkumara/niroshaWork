package edu.unt.cerl.replan.controller;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import org.opengis.feature.type.FeatureType;


/**
 * Interface FeatureConverter
 */
public interface FeatureConverter {

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
   * @param        ft
   */
  public void createFeatureTypeFromDB( FeatureType ft );


  /**
   * @param        ft
   */
  public void createFeatureTypeFromShp( FeatureType ft );


  /**
   * @param        name
   */
  public void createPODFeatureType( String name );


  /**
   * @param        source
   * @param        target
   * @param        postParams
   * @param        pods
   */
  public void createPostgisFromPODs( String source, String target, Map postParams, LinkedList<Map<String,String>> pods );


  /**
   * @param        tableName
   * @param        file
   * @param        postParams
   */
  public void createShpFromPostgis( String tableName, File file, Map postParams );


}
