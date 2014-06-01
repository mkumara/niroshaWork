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
//public class DrawFrame {
    
//}
import javax.swing.*;
import java.awt.*;
public class DrawFrame extends JFrame
{
@Override
public void paint(Graphics g)
{
super.paint(g);
Shape[] shapes = {
new Square(new Point(70,70), 70),
new Rectangle(new Point(50,150), 25, 50),
new Oval(new Point(100,220), 50,30),
new Circle(new Point(175,100), 100),
new RoundedRectangle(new Point(110,160), 40, 60, 20),
new RightAngledTriangle(new Point(180, 210), 100, 50)
};
for (int i = 0; i < shapes.length; i++)
{
shapes[i].draw(g);
}
}
public static void main(String[] args)
{
DrawFrame frame = new DrawFrame();
frame.setTitle("Lots of shapes...");
frame.setSize(400,400);
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setVisible(true);
}
}