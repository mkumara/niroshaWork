/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DemoPackage;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class ExampleMain {

    public static void main(String args[]){
        ExampleObservable obj = new ExampleObservable();
        ExampleObserver watcher = new ExampleObserver(obj);
       

        //obj.addObserver(watcher);
        
        obj.doSomething("This is a message");
        obj.doSomething(("Another one"));


    }
}
