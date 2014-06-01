/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DemoPackage;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class ExampleObserver implements Observer {

    public ExampleObserver(Observable obs){
        obs.addObserver(this);
    }

    @Override
    public void update(Observable o, Object o1) {
        System.out.println("Observer" + ((ExampleObservable) o).getMessage());
    }

}
