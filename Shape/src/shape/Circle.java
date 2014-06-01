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
//public class Circle {
    
//}
import java.awt.Graphics;
import java.awt.Point;
public class Circle extends OneDimensionalShape
{
public Circle(Point location, int height)
{
super(location, height);
}
@Override
public double getArea()
{
return (Math.PI * (Math.pow(this.getDimension(), 2.0)));
}
@Override
public void draw(Graphics g)
{
g.fillOval(getLocation().x, getLocation().y,
getDimension(), getDimension());
}
}
