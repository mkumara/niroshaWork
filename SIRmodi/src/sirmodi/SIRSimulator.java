/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sirmodi;

import java.util.ArrayList;

/**
 *
 * @author Onara
 */
   

/**
 *
 * @author jmuthukudage
 */
public class SIRSimulator {
    
    private int NUM_IND=1000000;
    private int CONTACT_RATE=3;
    private int INFECTIOUS_PERIOD=4;
    private float INFECTIVITY=0.000001f;
    private int DURATION = 50;
    private Individual population[];
    private int sus=0;
    private int rec=0;
    private int inf=0;
    
    public SIRSimulator(){
        this.population=new Individual[this.NUM_IND];
        
        //create objects in the array
        for(int i=0;i<this.NUM_IND;i++)
        { 
            int age=(int)Math.round(Math.random()*120);
            this.population[i]=new Individual(0,0, age);
            this.population[i].setAgeGroup(age);
           // System.out.println("Age is "+age);
        }
        }
    
    public SIRSimulator(int ind, int contact_rate, int infectious_period, float infectivity, int duration){
        this.CONTACT_RATE=contact_rate;
        this.DURATION=duration;
        this.INFECTIOUS_PERIOD=infectious_period;
        this.NUM_IND=ind;
        this.INFECTIVITY=infectivity;
        this.population=new Individual[this.NUM_IND];
        
        //create objects in the array
        for(int i=0;i<this.NUM_IND;i++)
        {   int age=(int)Math.round(Math.random()*120);
            System.out.println("Age is "+age);
            this.population[i]=new Individual(0,0, age);
             this.population[i].setAgeGroup(age);
        }
        }
    
    
    public void run(){
        Individual population_copy[]=new Individual[this.NUM_IND];
        
        for(int i=0;i<this.DURATION;i++){
            System.arraycopy(this.population, 0, population_copy, 0, this.NUM_IND);
            for(int j=0;j<this.NUM_IND;j++){
                //go through each individual and infect others based on infectious rate
                if(this.population[j].getInfState()==1){
                
                     int k=0;
                     while(k<this.population[j].getContactRate()){
                         int randLoc=(int)Math.round(Math.random()*(this.NUM_IND-1));
                         if(this.population[j].getInfectivity() < Math.random())
                             if(population_copy[randLoc].getInfState()==0)
                                   population_copy[randLoc].setInfState(1);
                             k++;
                     }
                     
                     this.population[j].incDays();
                     if(population_copy[j].getDays()>=population_copy[j].getInfectiousPeriod()){
                        population_copy[j].setInfState(2);
                     }
                
                }   
              
            }  
              System.arraycopy(population_copy, 0, this.population, 0, this.NUM_IND);
              this.printStats();
        }// end of DURATION loop
    }
    
    public void printStats(){
         this.sus=0;
         this.inf=0;
         this.rec=0;
        
        for(int i=0;i<this.NUM_IND;i++){
            if(this.population[i].getInfState()==0){
            this.sus++;
            }
            
            if(this.population[i].getInfState()==1){
            this.inf++;
            }
            if(this.population[i].getInfState()==2){
            this.rec++;
            }
        }
        
        System.out.println("Suspisious="+this.sus+" Infected="+this.inf+" Recovered="+this.rec+"\n");
    }
    
    public void setInfectious(int num){
        
        ArrayList markedInfectious=new ArrayList(num);
        int location;
        int i=0;   
        
        while(i<num){
        
             location=(int)Math.round(Math.random()*(this.NUM_IND-1));
             
             if(!markedInfectious.contains(location))
             { 
                 markedInfectious.add(location);
                 this.population[location].setInfState(1);
                 i++;
             }   
        
             }
        
        this.inf=num;
        this.sus=this.NUM_IND-this.inf;
        this.rec=0;
        printStats();
    }

    
}


