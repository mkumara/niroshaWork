/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.coverage;

import edu.unt.cerl.replan.model.POD;

/**
 *
 * @author rag0122
 */
public class PodPercentFull {
    int    pod         = -1;
    double percentFull = 0.0;

    public PodPercentFull() {}

    public PodPercentFull( int pod )
    {
        this.pod = pod;
    }

    public PodPercentFull( int pod, double percentFull )
    {
        this.pod = pod;
        this.percentFull = percentFull;
    }
}
