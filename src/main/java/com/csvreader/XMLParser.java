/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csvreader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author sarat
 */
public class XMLParser {

    String fileName;
    Document doc;

    public XMLParser(String fileName) {
        this.fileName = fileName;
        init();

    }

    private void init() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(fileName);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getValue(String elementName, String attributeName) {
        //Example: elementName = Language-Spanish attributeName = Resource
        String value ="";
        return value;
    }

}
