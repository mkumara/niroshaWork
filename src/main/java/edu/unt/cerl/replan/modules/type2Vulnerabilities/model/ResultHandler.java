/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.modules.type2Vulnerabilities.model;

import java.util.Observable;

/**
 *
 * @author sarat
 */

/*
Not used for now
*/

public class ResultHandler extends Observable{
     
    public static ResultHandler instance = new ResultHandler();
    
    
    public ResultHandler() {
        
    }
    
    public static ResultHandler getInstance() {
        return instance;
    }
}
