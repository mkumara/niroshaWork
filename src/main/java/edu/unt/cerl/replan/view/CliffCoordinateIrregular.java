/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.view;

import java.util.ArrayList;

/**
 *
 * @author jmuthukudage
 */
public class CliffCoordinateIrregular{
    
    ArrayList<Coordinate> points=new ArrayList<Coordinate>();
    
    public CliffCoordinateIrregular(){}
    
    public void addCoordinate(Coordinate coor){
      this.points.add(coor);
    }
    
    public Coordinate getCoordinate(int index){
     Coordinate coord;
      
       if(this.points.size() > (index-1))
           coord=this.points.get(index);
       else
           coord=null;
      return coord;
    }
    
}
