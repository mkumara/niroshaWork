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
//public class RightAngledTriangle {
    
//}
import java.awt.Graphics;
import java.awt.Point;
public class RightAngledTriangle extends TwoDimensionalShape
{
public RightAngledTriangle(Point location, int dimension1,
int dimension2)
{
super(location, dimension1, dimension2);
}
@Override
public double getArea()
{
return (this.getDimensions()[0]*this.getDimensions()[1]/2);
}
@Override
public void draw(Graphics g)
{
int xLocation = this.getLocation().x;
int yLocation = this.getLocation().y;
int[] xValues =
{xLocation, xLocation, xLocation + getWidth() };
int[] yValues = {yLocation, yLocation + getHeight(),
yLocation + getHeight()};
g.fillPolygon(xValues, yValues, 3);
}
}