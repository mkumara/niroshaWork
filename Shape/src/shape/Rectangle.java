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
//public class Rectangle {
    
//}
import java.awt.Graphics;
import java.awt.Point;
public class Rectangle extends TwoDimensionalShape
{
public Rectangle(Point location, int height, int width)
{
super(location, height, width);
}
@Override
public double getArea()
{
return (this.getHeight() * this.getWidth());
}
@Override
public void draw(Graphics g)
{
g.fillRect(getLocation().x, getLocation().y,
getHeight(), getWidth());
}
}