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
public class AgeGroup {
    
       
    public Range getAgeGroup(int age)
    {
          
     if(age>0 && age < 6)
        return Range.ONE_2_4;
     else if(age > 5 && age < 25)
         return Range.FIVE_2_24;
     else if(age > 25 && age < 50)
         return Range.TWENTYFIVE_2_49;
     else if(age > 49 && age < 65)
         return Range.FIFTY_2_64;
     else
         return Range.SIXTYFIVE_PLUS;
    }
    
}


