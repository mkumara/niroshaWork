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
//public class Square {
    
//}

import java.awt.Graphics;
import java.awt.Point;
public class Square extends OneDimensionalShape
{
public Square(Point location, int size)
{
super(location, size);
}
@Override
public double getArea()
{
return Math.pow(getDimension(), 2);
}
@Override
public void draw(Graphics g)
{
g.fillRect(getLocation().x, getLocation().y,
getDimension(), getDimension());
}
}