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
//public class RoundedRectangle {
    
//}
import java.awt.Graphics;
import java.awt.Point;
public class RoundedRectangle extends Rectangle
{
private int cornerRadius;
private int getCornerRadius()
{
return cornerRadius;
}
private void setCornerRadius(int cornerRadius)
{
this.cornerRadius = cornerRadius;
}
public RoundedRectangle(Point location, int width,
int height, int cornerRadius)
{
super(location, height, width);
setCornerRadius(cornerRadius);
}
@Override
public double getArea()
{
double innerWidth = getHeight()-(getCornerRadius()*2);
double innerHeight = getWidth()-(getCornerRadius()*2);
return innerWidth * innerHeight + 2 * getCornerRadius()
* (innerWidth + innerHeight) +
(Math.PI * Math.pow(this.getCornerRadius(), 2.0));
}
@Override
public void draw(Graphics g)
{
g.fillRoundRect
(getLocation().x, getLocation().y, getHeight(),
getWidth(), getCornerRadius(), getCornerRadius());
}
}