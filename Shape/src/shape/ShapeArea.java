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

    /**
     * @param args the command line arguments
     */
    //public static void main(String[] args) {
        // TODO code application logic here
    //}
    
//}
public class ShapeAreas
{
public static void main(String[] args)
{
Shape[] shapes = {
new Square(null, 250),
new Rectangle(null, 25, 50),
new Oval(null, 20,10),
new Circle(null, 10),
new RoundedRectangle(null, 25, 50, 10),
new RightAngledTriangle(null, 100, 50),
};
for (int i = 0; i < shapes.length; i++)
{
Shape shape = shapes[i];
System.out.println("The area of " +
shape.getClass().getSimpleName() +
" is " + shape.getArea());
}
}
}

