/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.applicationframework.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

/**
 *
 * @author Tamara Schneider <tschneider@unt.edu>
 */
public class ExportTools {

    public static void saveMapAsImage(MapContext map, String file, String type, int scalingFactor) {

        GTRenderer renderer = new StreamingRenderer();
        renderer.setContext(map);

        Rectangle imageBounds = null;
        try {
            ReferencedEnvelope mapBounds = map.getLayerBounds();

            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            int imageWidth = 600;

            imageBounds = new Rectangle(
                    0, 0, scalingFactor*imageWidth, scalingFactor* (int) Math.round(imageWidth
                    * heightToWidth));
        } catch (Exception e) {
        }

        //Rectangle imageSize = new Rectangle(600,600);

        BufferedImage image = new BufferedImage(imageBounds.width,
                imageBounds.height, BufferedImage.TYPE_INT_RGB); //darker red fill

        Graphics2D gr = image.createGraphics();
        gr.setPaint(Color.WHITE);
        gr.fill(imageBounds);

        try {
            renderer.paint(gr, imageBounds, map.getAreaOfInterest());


            File fileToSave = new File(file);
            ImageIO.write(image, type, fileToSave);
        } catch (IOException e) {
        }
    }

}
