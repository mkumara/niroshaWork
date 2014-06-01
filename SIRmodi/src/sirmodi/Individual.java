/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sirmodi;

/**
 *
 * @author Onara
 */
public class Individual {
    

/**
 *
 * @author jmuthukudage
 */
    
    int age;
    int daysOfInfection;
    int infState;
    Range ageGroup;
    int contactRate;
    int infectious_period;
    
    public Individual(){}
    
    public Individual(int inf, int daysOfInf){
      this.daysOfInfection=daysOfInf;
      this.infState=inf;
    }
    
     public Individual(int inf, int daysOfInf, int age){
      this.daysOfInfection=daysOfInf;
      this.infState=inf;
      this.age=age;
    }
    
    public void incDays(){
     this.daysOfInfection++;
    }
    
    public int getDays(){
    return this.daysOfInfection;
    }
    
    public void setInfState(int state){
        this.infState=state;
    }
    
    public int getInfState(){
    return this.infState;
    }
    
    public Range getAgeGroup()
    {
       return this.ageGroup;
    }
    
    public void setAgeGroup(int ageG)
    {
       
       this.ageGroup=new AgeGroup().getAgeGroup(ageG); 
    }
    
    public int getAge()
    {return this.age;}
    
    public double getInfectivity(){
     double infectivity;
     switch(this.ageGroup)
      {
          case ONE_2_4: infectivity=0.00004;
                        break;
          case FIVE_2_24: infectivity=0.00002;
                        break;
          case TWENTYFIVE_2_49: infectivity=0.00003;
                        break;
           case FIFTY_2_64: infectivity=0.00004;
                        break;
           case SIXTYFIVE_PLUS: infectivity=0.00004;
                        break;
           default: infectivity=0.00004;
               break;     
      }
      
      return infectivity;
    }
     
    
    
    
  public int getInfectiousPeriod()
    {  
        int returnVal;
      switch(this.ageGroup)
      {
          case ONE_2_4: returnVal=3;
                        break;
          case FIVE_2_24: returnVal=5;
                        break;
          case TWENTYFIVE_2_49: returnVal=3;
                        break;
           case FIFTY_2_64: returnVal=2;
                        break;
           case SIXTYFIVE_PLUS: returnVal=1;
                        break;
           default: returnVal=4;
               break;     
      }
      
      return returnVal;
    }
  
  public int getContactRate()
    {  
        int returnVal;
      switch(this.ageGroup)
      {
          case ONE_2_4: returnVal=3;
                        break;
          case FIVE_2_24: returnVal=5;
                        break;
          case TWENTYFIVE_2_49: returnVal=3;
                        break;
           case FIFTY_2_64: returnVal=3;
                        break;
           case SIXTYFIVE_PLUS: returnVal=2;
                        break;
           default: returnVal=3;
               break;     
      }
      
      return returnVal;
    }
    
   
    
}

