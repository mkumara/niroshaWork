/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DemoPackage;

import java.util.LinkedList;
import java.util.Observable;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class ExampleObservable  extends Observable{

    private String message;

    public void doSomething(String message){
        this.message = message;
        this.setChanged();
        this.notifyObservers();
        
    }

    public String getMessage(){
        return this.message;
    }
}
