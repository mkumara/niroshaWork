/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.view;

import java.util.ArrayList;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;

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
    
   public void clearCoordinates(){
     this.points.clear();
   }
   
   public String getAsString()
   {
       StringBuilder poly=new StringBuilder();
       poly.append("POLYGON((");
       
       //iterate though list and form poly
       for(int i=0;i<this.points.size();i++){
         poly.append(this.points.get(i).getX1()).append(" ").append(this.points.get(i).getY1()).append(",");
        }
       poly.append(this.points.get(0).getX1()).append(" ").append(this.points.get(0).getY1());
       poly.append("))");
       return poly.toString();
   }
   
   public Envelope2D getEnvelope(){
        Envelope2D env = new Envelope2D();
        DirectPosition2D startPosWorld=new DirectPosition2D();
        DirectPosition2D endPosWorld=new DirectPosition2D();
        
        //setting initoal values
        startPosWorld.x=180.0;
        startPosWorld.y=90.0;
        endPosWorld.x=-180.0;
        endPosWorld.y=-90.0;
        
         //get minimum point and maximum point
        for(int i=0;i<this.points.size();i++){
          if(this.points.get(i).getX1() < startPosWorld.x)
               startPosWorld.x=this.points.get(i).getX1();
          if(this.points.get(i).getY1() < startPosWorld.y)
               startPosWorld.y=this.points.get(i).getY1();
          
          if(this.points.get(i).getX1() > endPosWorld.x)
               endPosWorld.x=this.points.get(i).getX1();
          if(this.points.get(i).getY1() > endPosWorld.y)
               endPosWorld.y=this.points.get(i).getY1();
        }
        
       env.setFrameFromDiagonal(startPosWorld, endPosWorld);
        
        return env;
   }
    
}
