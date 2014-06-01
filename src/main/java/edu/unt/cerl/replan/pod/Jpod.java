/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.unt.cerl.replan.pod;

import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import edu.unt.cerl.replan.model.ScenarioState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

/**
 *
 * @author angel
 */
public class Jpod extends JPanel {
    //Container content = this.getContentPane();
    final static int SIZE  = 7;
    final static int LABEL_SIZE = 9;
    private boolean prev = false;
//    private boolean curr = false;
//    private String bestia;
    private Box Hbox0 = Box.createHorizontalBox();
    private Box Hbox1 = Box.createHorizontalBox();
    private Box Hbox2 = Box.createHorizontalBox();
    private Box Hbox3 = Box.createHorizontalBox();
    private Box Hbox4 = Box.createHorizontalBox();
    private Box Vbox0 = Box.createVerticalBox();
    private Box Vbox1 = Box.createVerticalBox();
    private Box Hbox[] = new Box[SIZE];
    private JButton addButton = new JButton( "Add POD" );
    private JButton deleteButton = new JButton( "Delete POD" );
    private JButton saveButton = new JButton( "Save" );
    private JButton infoButton;
    private JButton okButton = new JButton("O.K.");
    private ScenarioState scenario;
    private JTextArea jTextArea1 = new JTextArea("",5,15);
    private JScrollPane jScrollPane0 = new JScrollPane();
    private JScrollPane jScrollPane1 = new JScrollPane( jTextArea1 );
    private JLabel num_pods_label = new JLabel("# of additional PODS:");
    private JLabel jLabel[] = new JLabel[LABEL_SIZE];
    private JLabel jLabelArea = new JLabel();
    private JTextField jTextField[] = new JTextField[SIZE];
    private JTextField num_pods_entry = new JTextField(3);    
    private JCheckBox jcheckbox = new JCheckBox();    
    private Border border;
    private JSplitPane splitPane = new JSplitPane();
    private Font font = new Font("Serif", Font.ITALIC, 20);
    private Color[] listColorValues = { Color.BLACK, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE
        ,Color.MAGENTA, Color.DARK_GRAY, Color.RED, Color.PINK};
    private ButtonHandler handler = new ButtonHandler();
    private ButtonHandler mouse = new ButtonHandler();
    private ButtonHandler focus = new ButtonHandler();
    private ButtonHandler keystroke = new ButtonHandler();
    private Map<Integer, Entries> m = new HashMap<Integer,Entries>();
    private JList list = new JList();
    private DefaultListModel listModel;
    private Integer count = 0;
    private int globalListIndex = -1;
    private boolean flag = false;
    private Parsear cadena = new Parsear();
    private JPanel panel1, panel2;
    private JComboBox jcombobox;
    private String [] podtype = { "Public", "Corporate"};
    private Object [] elemento1 = {
        new Font("Helvetica", Font.PLAIN, 20), Color.black,
        new DiamondIcon(Color.green), "Hello"};
    private Object [] elemento2 = {
        new Font("Helvetica", Font.PLAIN, 20), Color.black,
        new DiamondIcon(Color.red), "Adios"};
    //private ListCellRenderer renderer= null;
    //private Component pesu = new Component() {};

    public Jpod() { //throws IOException{

        super( new FlowLayout() );  
        panel1 = new JPanel( new BorderLayout() );
        panel1.setSize(200, 100);
        panel2 = new JPanel( new BorderLayout() );
        panel2.setSize(200, 100);
        listModel = new DefaultListModel();
        list = new JList(listModel);
//        listModel.add( 0, elemento1 );
//        ListCellRenderer ren = new ComplexCellRenderer();
//        list.setCellRenderer(ren); //add(ren);

        list.addMouseListener( mouse );
        list.addKeyListener( keystroke );
        final boolean MULTICOLORED = false;
        if (MULTICOLORED) {
            this.setOpaque(true);
            this.setBackground(new Color(255, 0, 0));
        }

        border = BorderFactory.createLineBorder(Color.BLACK);
//      Dimension sd1 = new Dimension(400, 400);
//      Dimension sd2 = new Dimension(50,50);
//      SizeDisplayer hola = new SizeDisplayer();
        Hbox0.add( num_pods_label  );
        Hbox0.add( num_pods_entry  );

        okButton.addActionListener( handler );
        Hbox0.add( okButton );

        addButton.addActionListener( handler );
        deleteButton.addActionListener( handler );
        Hbox1.add( addButton );
        Hbox1.add(Box.createHorizontalGlue());
        Hbox1.add( deleteButton );

        jScrollPane0 = new JScrollPane( list );
        Vbox0.add( jScrollPane0 );
        panel2.add( Hbox0, BorderLayout.NORTH );
        panel2.add( Hbox1, BorderLayout.SOUTH );
        panel2.add( Vbox0, BorderLayout.CENTER );

        jLabel[0] = new JLabel( "Name:\t" );
        jLabel[1] = new JLabel( "Address:\t" );
        jLabel[2] = new JLabel( "City:\t" );
        jLabel[3] = new JLabel( "Zip:\t" );
        jLabel[4] = new JLabel( "*Lon:\t" );
        jLabel[5] = new JLabel( "*Lat:\t" );
        jLabel[6] = new JLabel( "TypePOD:\t" );
        jLabel[7] = new JLabel( "Status:\t" );
        jcombobox = new JComboBox( podtype );
        jcheckbox = new JCheckBox("OFF");
        splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT);
        int i ;
        for ( i = 0 ; i < SIZE - 1 ; i++){
            Hbox[ i ] = Box.createHorizontalBox();
            jLabel[ i ].setBorder( border );
            jLabel[ i ].setFont( font );
            Hbox[ i ].add( jLabel[i] );

            //jTextField[i] = new JTextField( String.valueOf(i), 30);
            jTextField[ i ] = new JTextField( "", 30);
            if( i == 0){ jTextField[0].setText( "<unnamed>" ); }
            jTextField[ i ].addMouseListener( mouse );
            jTextField[ i ].addFocusListener( focus );
            jTextField[ i ].setEnabled( false );
            Hbox[ i ].add( jTextField[ i ] );
            Vbox1.add( Hbox[ i ] );
        }
        
        //Hbox3.createHorizontalBox();
        jLabel[ 6 ].setBorder( border );
        jLabel[ 6 ].setFont( font );
        Hbox3.add( jLabel[ 6 ] );
        //Hbox3.add( Box.createHorizontalGlue() );
        jcombobox.setEnabled( false );
        jcombobox.addItemListener( handler );
        Hbox3.add( jcombobox );
        Vbox1.add( Hbox3 );
        
        jLabel[ 7 ].setBorder( border );
        jLabel[ 7 ].setFont( font );
        

        //Hbox4.createHorizontalBox();
        Hbox4.add( jLabel[ 7 ] );
        jcheckbox.setEnabled( false );
        jcheckbox.addItemListener( handler );
        Hbox4.add( Box.createHorizontalGlue() );
        Hbox4.add( jcheckbox );
        Vbox1.add( Hbox4 );
         

        //jcheckbox.addActionListener(mouse);
        jTextArea1.setText( "" );
        jTextArea1.setEnabled( false );
        jTextArea1.addMouseListener( mouse );
        jTextArea1.addFocusListener( focus );
        
        jLabelArea.setText( "Comments" );
        jLabelArea.setBorder( border );
        //Vbox1.add( jcheckbox );
        Vbox1.add( jLabelArea );
        Vbox1.add( jScrollPane1 );

        saveButton = new JButton( "Save" );
        saveButton.addActionListener( handler );
        Hbox2.add( saveButton );
        Hbox2.add(Box.createHorizontalGlue());
        //infoButton = new JButton();//( new PodInfoAction( scenario.getMapPane() ,this) );
//        infoButton.addActionListener(handler);
//        Hbox2.add( infoButton);
        Vbox1.add( Hbox2 );


        Dimension minimumSize = new Dimension(10, 10);
        jScrollPane1.setMinimumSize(minimumSize);

        jScrollPane1.setSize(new Dimension(200,100));
        panel1.add( Vbox1 );
        //panel1.setVisible( false );
        panel2.add( Vbox0 );
        splitPane.setRightComponent( panel1 );

        //jScrollPane1.getViewport().add(jTextArea1);
        ///jTextArea1.setText("something");

        splitPane.setLeftComponent( panel2 );
        splitPane.setSize( 495, 395 );
        add(splitPane);
        setVisible( true );
    }


    public void start( Map<Integer, Entries> mapa ){
        
        String str = null;
        String name = null;
        String concat = null;
        Entries e;
        m = new HashMap<Integer, Entries>(mapa);
        Iterator myVeryOwnIterator = m.keySet().iterator();

        while( myVeryOwnIterator.hasNext() ) {
            //System.out.println( myVeryOwnIterator.next() );
            str = Integer.toString( count + 1 );
            e = new Entries();
            e = m.get( (Integer) myVeryOwnIterator.next() );    //Check 4 problems here
            name = e.objectEntries[ 0 ].getText();
            concat = name + ":" + str;
            System.out.println( concat );
            listModel.add( count, concat );
            count++;
        }//endWHILE

        if(count > 0){

            list.setSelectedIndex(0);

            for (int i = 0; i < SIZE - 1 ; i++){
                jTextField[ i ].setEnabled( true );
                //jTextField[i].enable(true);
            }//endFOR

            jTextArea1.setEnabled( true );
            jcheckbox.setEnabled( true );
            readPOD( 0 );
            globalListIndex = 0;
            prev = false;
            
        }//endIF
        
    }//endSTART

/*
    public void setScenario(ScenarioState s){
        scenario = s;
        JButton tmp = new JButton( new PodInfoAction( scenario.getMapPane() ,this) );
        infoButton = tmp;
        infoButton.addActionListener( handler );
        Hbox2.add( infoButton);
        //Hbox2.remove(2);
        //Hbox2.setComponentZOrder((JButton)tmp, 2);
        //infoButton = tmp;
        //infoButton = new JButton(new PodInfoAction( s.getMapPane() ,this));
        //Hbox2.add( infoButton );
    }
*/


    public void readPOD(int index){
        if( index != -1){                           //Valid click

                Entries pod = new Entries();
                pod = m.get(new Integer( index ));

                for(int i = 0; i < SIZE - 1 ; i++){

                    jTextField[i].setText( pod.readTextField(i) );
                }//endFOR

                jTextArea1.setText( pod.readComments() );
                System.out.println("Reading too too . . ." + index);

        }//endIF
        
    }//endREAD_POD

/*
    public void setNewLocation( double x, double y){

        if( jTextField[ 4 ].isEnabled() ){
            int num = list.getSelectedIndex();
            String str;
            str = Double.toString( x );
            jTextField[ 4 ].setText( str );
            str = Double.toString( y );
            jTextField[ 5 ].setText( str );
            prev = true;
            scenario.getMapPane().setRepaint(true);
        }//endIF enabled ( show coordinates on the textfields)

    }
*/
    
     public class ButtonHandler implements ActionListener, MouseListener, FocusListener, KeyListener, ItemListener {


        @SuppressWarnings("empty-statement")

        @Override
        public void mouseClicked(MouseEvent event) {
            //throw new UnsupportedOperationException("Not supported yet.");

            if ( event.getSource() == list  ){          //You clicked on the list

                int index = list.getSelectedIndex();
                System.out.println("Index: "+ index);
                if ( index >= 0 ) {  //You clicked on a valid index AND You haven't clicked same POD to display

                    for (int i = 0; i < SIZE - 1 ; i++){
                        //jTextField[i].enable(true);
                        jTextField[ i ].setEnabled( true );
                    }

                    jTextArea1.setEnabled( true );
                    jcombobox.setEnabled( true );
                    jcheckbox.setEnabled( true );
                    
                    if( !( jTextField[0].getText().equals("<unnamed>")  && jTextField[1].getText().equals("")  && jTextField[2].getText().equals("") && jTextField[3].getText().equals("") && jTextField[4].getText().equals("") && jTextField[5].getText().equals("") && jTextArea1.getText().equals("") ) && prev == true ){  //Either TextField are default and prev(modified) is true


                            int num1 = globalListIndex + 1;
                            int r = JOptionPane.showConfirmDialog( null , "Do you want to save changes info on POD: " + num1 + "?", "Confirm Save?", JOptionPane.YES_NO_OPTION);

                            if (r == JOptionPane.YES_OPTION) {

                                if ( savePOD( globalListIndex ) == 1 ){
                                    System.out.println( "Saving since whatif = 1" );
                                    prev = false;
                                    readPOD( index );
                                    System.out.println( "Reading: " + num1 );
                                    list.setSelectedIndex( index );

                                }else{
                                    System.out.println( "whatif = 0" );
                                    prev = true;
                                    index = globalListIndex;
                                    System.out.println( "Ignoring any operation, stay in: " + globalListIndex );
                                    list.setSelectedIndex( globalListIndex );

                                    
                                }//endIF/ELSE
                            }//endIF

                            if ( r == JOptionPane.NO_OPTION  ){
                                
                                int num2 = index + 1;
                                int s = JOptionPane.showConfirmDialog( null , "Do you want to save changes on selected POD: " + num2 + "?", "Confirm Save?", JOptionPane.YES_NO_OPTION);
                                if ( s == JOptionPane.YES_OPTION ){

                                    if ( savePOD( index ) == 1 ) {                                        
                                        System.out.println( "Saving" );
                                        prev = false;
                                        readPOD( index );
                                        System.out.println( "Reading: " + num2 );
                                        globalListIndex = index;

                                    }else{  //You want to save in a new selected POD but you don't have
                                        System.out.println( "whatif = 0" );
                                        prev = true;
                                        //readPOD( index );
                                        list.setSelectedIndex( index );
                                        index = globalListIndex;
                                        System.out.println( "Stay same POD with displaying possible changes: " + index );
                                        
                                    }//endIF/ELSE
                                }
                            }//endNO_OPTION_01

                    }//endIF
                    System.out.println( "Ignoring any operation" );
                    readPOD( index );
                    System.out.println( "Reading: " + index );
                    prev = false;
                    globalListIndex = index;
                }//endIF_ValidPOD
            }//endIF LIST

            for (int i =0 ; i < SIZE -1 ; i++){
                if( event.getSource() == jTextField[i] ){
                    System.out.println("Modifying...");
                    prev = true;
                }//endIF Jtextfield
            }

            if( event.getSource() == jTextArea1  ){
                System.out.println("Modifying...");
                prev = true;
            }
        }//endmouseClicked




        @Override
        public void actionPerformed( ActionEvent event ) {

            if ( event.getSource() == addButton ){
                addPOD();
//                entry.write(jTextField, jTextArea1);    //write info within Objetc
//                m.put( count, entry); //write info in HashMap
            }

            if ( event.getSource() == saveButton ){
                int index = list.getSelectedIndex();
                if( index == -1 ){
                    System.out.println("No Pod selected for saving information");
                    JOptionPane.showMessageDialog(null,
                    "No Pod selected for saving information",
                    "No Pod selected",
                    JOptionPane.ERROR_MESSAGE);
                }else{
//                      index++;
                      savePOD(index);
                      prev = false;
                }
            }


            if ( event.getSource() == deleteButton ){

                if( list.getSelectedIndex() == -1 ){

                    System.out.println("You have not selected a pod for deletion");
                    JOptionPane.showMessageDialog(null,
                    "You have not selected a pod for deletion",
                    "Error Pod Deletion",
                    JOptionPane.ERROR_MESSAGE);

                }else{
                        int index = list.getSelectedIndex();

                        if( listModel.getSize() > 1){

//                          System.out.println("Smth is going on else 1");
                            listModel.removeElementAt(index);
                            count--;
                            //System.out.println("Smth is going on else 2");
                            int listSize = listModel.getSize();

                            for( int i = index + 1 ; i <=  listSize ;i++){

                                m.put( (i - 1) , m.remove(i) );
                                // Substraction  of (i -1) - 1 is necessary  since object value in i -1 had moved to i - 1.
//                                listModel.add( i - 1 , (Integer) listModel.remove( i - 1 ) - 1 );
                System.out.println( "Jpod.actionPerformed calling getElementAt with index = " + (i - 1) );
                                String str = (String) listModel.getElementAt( i - 1 );
                                System.out.println( "Removing: " + str );                                
                                str = cadena.parse_string( str );
                                System.out.println( "Removing: " + str );
                                int j = Integer.parseInt( str );
                                j--;
                                System.out.println( "Updating: " + j );
                                //readPOD(index);
                                String id = Integer.toString(j);
                                readPOD(i-1);
                                id = jTextField[0].getText() + ":" + id;
                                listModel.remove(i - 1);
                                listModel.add(i - 1, id);
                                //listModel.add( i - 1 , (Integer)listModel.remove( i - 1 ) - 1 );
                                System.out.println("Index: " + j + " and listSize "+ listSize );

                            }//endFOR

                            System.out.println("Smth is going on else 5");
                            //m.remove( index );
                            index++;
                            System.out.println("Delete Pod number: " + index);

                        }else{
//                                index++;
                                m.remove(index) ;
                                listModel.remove( index );
                                count--;

                                for (int i = 0; i < SIZE - 1 ; i++){
                                    jTextField[i].setEnabled(false);
                                }//endFOR
                                jTextArea1.setEnabled(false);
                                jcheckbox.setEnabled(false);

                                System.out.println("Falsear POD: " + index);
                        }//endIF/ELSE
                }//endIF/ELSE
            }//endEVENT

            if ( event.getSource() == okButton ){

                int num;
                String str = num_pods_entry.getText();
                boolean b = Pattern.matches("[0-9]+", str);
                System.out.print(str);

                if( !str.isEmpty() ){   //Validate num_pods_entry different from empty.

                    if (b){ //Validate numbers

                        num = Integer.parseInt(str);

                        while ( num > 0  ){

                            addPOD();
                            num--;

                        }//endWHILE

                        globalListIndex = 0;

                    }else{

                            System.out.println("You have enter a non valid number");
                            JOptionPane.showMessageDialog(null,
                            "You have enter a non valid number",
                            "Error entering valid number",
                            JOptionPane.ERROR_MESSAGE);
                    }//endIF/ELSE REGEX
                }else{

                        System.out.println("You have not enter the number of PODs");
                        JOptionPane.showMessageDialog(null,
                        "You have not enter number of new PODs",
                        "Error entering number of PODs",
                        JOptionPane.ERROR_MESSAGE);
                        
                }//endIF/ELSE EMPTY
            }//endIF

/*            if( event.getSource() == infoButton ){
            }
 */


        }//end ActionPerformed

        public void addPOD(){

            String str = Integer.toString(count + 1);
            String concat = "<unnamed>:" + str;

//            Object [] obj = {
//                new Font("Helvetica", Font.PLAIN, 14), Color.black,
//                new DiamondIcon(Color.red), concat};
//            listModel.add(count, obj );

            listModel.add(count, concat );
            Entries entry = new Entries();
            entry.initialize("<unnamed>");
            m.put(new Integer( count ) , entry);
            count++;
        }


        public int savePOD(int index){
            
            int whatif;
            String str1, str, concat;
            boolean b;

            if( jTextField[0].getText().compareTo("") == 0 ){

                System.out.println("Empty entry");
                JOptionPane.showMessageDialog(null,
                "Check for empty entry at: " + jLabel[0].getText() ,
                "Error reading empty entry",
                JOptionPane.ERROR_MESSAGE);
                whatif = 0;
                return whatif;
            }



            for(int i = 3; i < 6 ; i++){    //Test for validity in Zip, Lon, Lat
                
                str1 = jTextField[i].getText();
                b = Pattern.matches("-?[0-9]+.?[0-9]+", str1);
                System.out.println(str1);

                if( !str1.isEmpty() ){   

                    if (!b){ //Validate numbers

                        System.out.println("Invalid number");
                        JOptionPane.showMessageDialog(null,
                        "Check for non-valid number at: " + jLabel[i].getText() ,
                        "Error entering valid number",
                        JOptionPane.ERROR_MESSAGE);
                        whatif = 0;
                        return whatif;

                    }//endIF REGEX

                }else{

                    if( i == 4 || i == 5){      //Test for emptyness in Lon and Lat
                        
                        System.out.println("Empty entry");
                        JOptionPane.showMessageDialog(null,
                        "Check for empty entry at: " + jLabel[i].getText() ,
                        "Error reading emty entry",
                        JOptionPane.ERROR_MESSAGE);
                        whatif = 0;
                        return whatif;
                    }

                }//endIF


            }//endFOR


            str = Integer.toString( index + 1);
            concat = jTextField[0].getText() + ":" + str;
            listModel.removeElementAt( index );
            listModel.insertElementAt( concat, index );
            Entries pod = new Entries();
            pod.write(jTextField, jTextArea1);
            list.setSelectedIndex( index );
            m.put( new Integer( index ), pod);
            System.out.println("Saving...");
            whatif = 1;
            return whatif;

        }


        @Override
        public void mousePressed(MouseEvent arg0) {
        //    throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
        //    throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
        //    throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
        //    throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void focusGained(FocusEvent arg0) {
        //  throw new UnsupportedOperationException("Not supported yet.");
            System.out.println("Focus ( modified ) ... ");
            //prev = true;
        }

        @Override
        public void focusLost(FocusEvent arg0) {
        //  throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
            //throw new UnsupportedOperationException("Not supported yet.");
            System.out.println("keyTyped ... ");
        }

        @Override
        public void keyPressed(KeyEvent arg0) {
            //throw new UnsupportedOperationException("Not supported yet.");
            System.out.println("keyPressed ... ");
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
            //throw new UnsupportedOperationException("Not supported yet.");
            System.out.println("keyReleased ... ");
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
            if ( e.getStateChange() == ItemEvent.DESELECTED){
                System.out.println("CheckBox DESELECTED ... ");
                list.getSelectedIndex();
                //list.add( new Font() , );
                list.setBackground(Color.MAGENTA);
                list.setSelectionBackground(Color.RED);    
            }

            if ( e.getStateChange() == ItemEvent.SELECTED){
                System.out.println("CheckBox SELECTED ... ");
                list.setSelectionBackground(Color.black);

                //list.set
            }
        }
    }//end ButtonHandler
}
