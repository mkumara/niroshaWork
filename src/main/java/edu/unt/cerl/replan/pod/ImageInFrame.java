package edu.unt.cerl.replan.pod;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ImageInFrame {
  private String path = "http://cerl.unt.edu/sites/default/files/pictures/picture-3.jpg";
  private URL url;
  private BufferedImage image, read; // = ImageIO.read(url);
  private JButton mapButton = new JButton();
  private JLabel label =new JLabel();
  private JTextField field=new JTextField();
  private Box Vbox = Box.createVerticalBox();
  private JFrame f = new JFrame();
  private ButtonHandler handler = new ButtonHandler();
  private Process proc;

  public void popWindow () {
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    label.setIcon(new ImageIcon(image));

    try { // Get the image
      url = new URL(path);
      image = ImageIO.read(url);
      label.setIcon(new ImageIcon(image));
    } catch ( MalformedURLException e){
	System.out.println("Invalid URL " + path + ": "+e.getMessage());
    }catch( IOException e){
	System.out.println("Invalid URL " + path + ": "+e.getMessage());
    }


    Vbox.add(label);
    Vbox.add(field);
    mapButton.addActionListener(handler);
    mapButton.setText("Search for map");
    Vbox.add(mapButton);
    f.getContentPane().add(Vbox);
    f.pack();
    f.setLocation(200,200);
    f.setVisible(true);
  }//endPopImage



  public class ButtonHandler implements ActionListener {

    public void actionPerformed( ActionEvent event ) {
      if(event.getSource() == mapButton){
        System.out.println(path);
	path = field.getText();
        System.out.println(path);
	try{
	  proc=Runtime.getRuntime().exec("firefox "+ path);
          url = new URL(path);
          image = ImageIO.read(url);
          label.setIcon(new ImageIcon(image));
        } catch ( MalformedURLException e){
	  System.out.println("Invalid URL " + path + ": "+e.getMessage());
	} catch ( IOException e){
	    System.out.println("Invalid command: "+e.getMessage());
	}//endTRY/CATCH
        Vbox.add(label);
        f.getContentPane().add(Vbox);

      }//endIF
    }//end ActionPerformed
  }//end ButtonHandler
}//endImageInFrame