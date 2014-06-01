/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csvreader;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.geotools.geometry.DirectPosition2D;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author sarat
 */
public class GEarthExporter {

    String templateFilePath = "RE-PLAN_Location.kml";
    String workingFilepath = "ExampleWorking.kml";
    File src = new File(templateFilePath);
    File dest = new File(workingFilepath);
    DirectPosition2D pos;
    String absoluteDestPath;

    //Create a working copy file, replace any existing files with same name,  by copying contents of original template
    //Make modifications to the working copy
    //Ask G Earth to open the working copy
    public void init() {
        try {
            prepareKMLFile();
            writeToKML(Double.toString(pos.x), Double.toString(pos.y));
            openGEarth();
            //cleanupFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLocation(DirectPosition2D pos) {
        this.pos = pos;
    }

    private void writeToKML(String lon, String lat) {

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(workingFilepath);
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("LookAt");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("Longitude : " + getTagValue("longitude", eElement));
                    setTagValue("longitude", eElement, lon);
                    System.out.println("Latitude : " + getTagValue("latitude", eElement));
                    setTagValue("latitude", eElement, lat);
//                    System.out.println("Nick Name : " + getTagValue("nickname", eElement));
//                    System.out.println("Salary : " + getTagValue("salary", eElement));

                }
            }

            NodeList nList2 = doc.getElementsByTagName("Point");
            String coordinates = lon.concat("," + lat + ",0");
            for (int temp = 0; temp < nList2.getLength(); temp++) {

                Node nNode = nList2.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("coordinates : " + getTagValue("coordinates", eElement));
                    setTagValue("coordinates", eElement, coordinates);


                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(workingFilepath));
            //StreamResult result = new StreamResult(new File("WorkingExample2.kml"));
            transformer.transform(source, result);
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    private static void setTagValue(String sTag, Element eElement, String value) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);
        //nValue.setNodeValue(value);
        nValue.setTextContent(value);
    }

    private void openGEarth() {
        System.out.println("positions: lon " + Double.toString(pos.x) + " lat: " + Double.toString(pos.y) + "\n");

        try {

            if (dest.exists()) {

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(dest);
                } else {
                    System.out.println("Awt Desktop is not supported!");
                }

            } else {
                System.out.println("File doesnot exist!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private void openGEarth() {
//        if (isOSWindows()) {
//
//            System.out.println("Windows: positions: lon " + Double.toString(pos.x) + " lat: " + Double.toString(pos.y) + "\n");
//
//            try {
//
//		if (dest.exists()) {
//
//			if (Desktop.isDesktopSupported()) {
//				Desktop.getDesktop().open(dest);
//			} else {
//				System.out.println("Awt Desktop is not supported!");
//			}
//
//		} else {
//			System.out.println("File doesnot exist!");
//		}
//
//	  } catch (Exception ex) {
//		ex.printStackTrace();
//	  }
//
//        } else if (isOSUnix()) {
//            try {
//                Process p = Runtime.getRuntime().exec("google-earth " + absoluteDestPath);
//                System.out.println("ExampleWorking path " + absoluteDestPath + "\n");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println("Unix: positions: lon " + Double.toString(pos.x) + " lat: " + Double.toString(pos.y) + "\n");
//        } else {
//            System.out.println("OS Not supported\n");
//            System.out.println("OS Not supported: positions: lon " + Double.toString(pos.x) + " lat: " + Double.toString(pos.y) + "\n");
//        }
//
//
//    }
    private void prepareKMLFile() throws IOException {
        copyFileToFile(src, dest);
        absoluteDestPath = dest.getAbsolutePath();

    }

    private boolean isOSWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.indexOf("win") >= 0);
    }

    private boolean isOSUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        // linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
    }

    private void cleanupFiles() {
        try {
            dest.delete();
            File newDest = new File(workingFilepath);
            newDest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void copyFileToFile(final File src, final File dest) throws IOException {
        copyInputStreamToFile(new FileInputStream(src), dest);
        dest.setLastModified(src.lastModified());
    }

    public static void copyInputStreamToFile(final InputStream in, final File dest)
            throws IOException {
        copyInputStreamToOutputStream(in, new FileOutputStream(dest));
    }

    public static void copyInputStreamToOutputStream(final InputStream in,
            final OutputStream out) throws IOException {
        try {
            try {
                final byte[] buffer = new byte[1024];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
