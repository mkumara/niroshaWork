/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shape;

/**
 *
 * @author Onara
 */
//public class Shape {
    
//}
import java.awt.Graphics;
import java.awt.Point;
public abstract class Shape
{
protected Point location;
protected int[] dimensions;
public Shape(Point location)
{
setLocation(location);
}
public int[] getDimensions() {
return dimensions;
}
public void setDimensions(int[] dim) {
this.dimensions = dim;
}
public Point getLocation() {
return location;
}
public void setLocation(Point location) {
this.location = location;
}
public abstract double getArea();
public abstract void draw(Graphics g);
}
