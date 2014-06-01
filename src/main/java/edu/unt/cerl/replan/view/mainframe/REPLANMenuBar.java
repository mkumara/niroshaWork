package edu.unt.cerl.replan.view.mainframe;

import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.action.ExportToShapeListener;
import edu.unt.cerl.replan.controller.action.MenuActions;
import edu.unt.cerl.replan.experiments.MultiUniformPartitioning;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.view.ResourceManagementFrame;
import edu.unt.cerl.replan.modules.type2Vulnerabilities.view.VulnerabilitySelectionFrame;
import edu.unt.cerl.replan.pubtrans.controller.PubTrans;
import edu.unt.cerl.replan.pubtrans.view.DisplayTransitNetwork;
import edu.unt.cerl.replan.pubtrans.view.DisplayTransitNetworkTask;
import edu.unt.cerl.replan.view.windows.NewScenarioFrame;
import edu.unt.cerl.replan.view.windows.OpenScenarioFrame;
import edu.unt.cerl.replan.view.windows.ProgressWindow;
import edu.unt.cerl.replan.view.windows.SaveAsFrame;
import edu.unt.cerl.replan.vulnerability.transportation.model.ClassifyPopulationBlocks;
import edu.unt.cerl.replan.vulnerability.transportation.model.ClassifyPopulationBlocksTask;
import edu.unt.cerl.replan.vulnerability.transportation.model.GtfsGraph;
import edu.unt.cerl.replan.vulnerability.transportation.model.DetermineLocsWithBestCoverage;
import edu.unt.cerl.replanexecution.Replan;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import org.geotools.filter.text.cql2.CQLException;
import tools.PODAnalysis;

/**
 * Implementation of the menu bar of the RE-PLAN main window
 */
public class REPLANMenuBar extends JMenuBar {

    private boolean enableaboutItem = true;
    private boolean enablehelpItem = false;
    private boolean enableloadScenarioItem = true;
    private boolean enablenewScenarioItem = true;
    private boolean enablequitItem = true;
    private boolean enableexportToShp = true;
    private boolean enablesaveScenarioItem = true;
    private boolean enablesaveScenarioAsItem = true;
    private boolean enableseparator = true;
    //
    private boolean enablereplanItem = true;
    private boolean enablepublicTransportation = true;
    private boolean enablepartitionItem = true;
    private boolean enablehybridItem = true;
    private boolean enablehybridAllItem = true;
    private boolean enableoptimizationItem = true;
    private boolean enabletoolsMenu = true;
    private boolean enabletmpItem = true;
    private boolean enablepodAnalyzerItem = true;
    //
    //
    private boolean enableregionalAnalysis = true;
    private boolean enableplanCreation = true;
    private boolean enableplanAnalysis = true;
    private boolean enableexperiments = false;
    private boolean enableregionalTransportationVulnerabilityAnalysis = true;
    private boolean enabledisplayTransitNetwork = true;
    private boolean enablevoronoi = true;
    private boolean enablenck = false;
    private boolean enableequalPopulation = true;
    private boolean enableproportionalPartitioning = false;
    private boolean enablecoverageAnalysis = false;
    private boolean enabletrafficAnalysis = true;
    private boolean enabletransitAnalysis = false;
    private boolean enabletransportationVulnerabilityAnalysis = false;
    private boolean enablevulnAnalysis = false;
    private boolean enablepodAnalysis = true;
    private boolean enablemultiUniformPartitioning = false;
    private boolean enablemaximizeCoverage = true;
    private boolean enablemaximizeCoverageWTransit = true;
    private boolean enablemaximizeCoverageAddTransitToPOD = true;
    private boolean enablereassignVulnPops = true;
    private boolean enabletype2Analysis = true;
    private boolean enabletype2VulnAnalysis = true;
    private boolean enabletype2ResMgmt = true;
    private boolean enablegtfsGraphBuilder = true;
    //
    private JMenuItem aboutItem;
    private JMenu editMenu;
    private JMenu fileMenu;
    private JMenuItem helpItem;
    private JMenu helpMenu;
    private JMenuItem loadScenarioItem;
    private JMenuItem newScenarioItem;
    public JMenuItem podAnalyzerItem;
    private JMenuItem quitItem;
    public JMenuItem replanItem;
    public JMenuItem publicTransportation;
    private JMenuItem partitionItem;
    private JMenuItem hybridItem;
    private JMenuItem hybridAllItem;
    private JMenuItem optimizationItem;
    public JMenuItem saveScenarioItem;
    public JMenuItem saveScenarioAsItem;
    public JMenuItem exportToShp;
    private JPopupMenu.Separator separator;
    private JMenu toolsMenu;
    private JMenuItem tmpItem;
    private Component owner;
    private JMenu regionalAnalysis;
    private JMenu planCreation;
    private JMenu planAnalysis;
    private JMenu experiments;
    private JMenu type2Analysis;
    private JMenuItem regionalTransportationVulnerabilityAnalysis;
    private JMenuItem displayTransitNetwork;
    private JMenuItem voronoi;
    private JMenuItem nck;
    private JMenuItem equalPopulation;
    private JMenuItem proportionalPartitioning;
    private JMenuItem coverageAnalysis;
    private JMenuItem trafficAnalysis;
    private JMenuItem transitAnalysis;
    private JMenuItem transportationVulnerabilityAnalysis;
    private JMenuItem vulnAnalysis;
    private JMenuItem podAnalysis;
    private JMenuItem multiUniformPartitioning;
    private JMenuItem maximizeCoverage;
    private JMenuItem maximizeCoverageWTransit;
    private JMenuItem maximizeCoverageAddTransitToPOD;
    private JMenuItem reassignVulnPops;
    private JMenuItem type2VulnAnalysis;
    private JMenuItem type2ResMgmt;
    private JMenuItem gtfsGraphBuilder;

    //
    // Constructor
    //
    public REPLANMenuBar(Component owner) {
        //ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        //ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();

        this.owner = owner;
        this.initComponents();
        this.setVisible(true);
        //this.reAdjustToolsMenu();
        //task = new Replan(this, s);
    }

    //
    // Initializes the individual menus
    //
    private void initComponents() {

        // initialize menus
        this.initFileMenu();
        this.initToolsMenu();
        this.initHelpMenu();

        // add menus to menu bar
        this.add(fileMenu);
        this.add(toolsMenu);
        this.add(helpMenu);

    }

    public Component getOwner() {
        return this.owner;
    }

    /**
     * This method initializes the menu items of the file menu
     */
    private void initFileMenu() {
        // create menus and items
        fileMenu = new JMenu("File");
        newScenarioItem = new JMenuItem("New...");
        loadScenarioItem = new JMenuItem("Load...");
        saveScenarioItem = new JMenuItem("Save...");
        saveScenarioAsItem = new JMenuItem("Save as...");
        exportToShp = new JMenuItem("Export to shapefile...");
        separator = new JPopupMenu.Separator();
        quitItem = new JMenuItem("Quit");

        // set keystroke and action listener for new scenario menu item
        newScenarioItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newScenarioItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                new NewScenarioFrame(owner);
            }
        });

        // set keystroke and action listener for load menu item
        loadScenarioItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, InputEvent.CTRL_MASK));
        loadScenarioItem.setActionCommand("load");
        loadScenarioItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                new OpenScenarioFrame(owner);
            }
        });

        // set keystroke and action listener for save menu item
        saveScenarioItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveScenarioItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                MenuActions.saveScenario();
            }
        });
        saveScenarioItem.setActionCommand("save");

        saveScenarioAsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                new SaveAsFrame(owner);
            }
        });
        saveScenarioAsItem.setActionCommand("save_as");

        // set keystroke and action listener for export to shapefile menu item
        exportToShp.setEnabled(true);
        exportToShp.addActionListener(new ExportToShapeListener(REPLAN.getMainFrame()));

//        exportToShp.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, InputEvent.CTRL_MASK));
//        exportToShp.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent evt) {
//                MenuActions.quitAction();
//            }
//        });
        // set keystroke and action listener for quit menu item
        quitItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        quitItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                MenuActions.quitAction();
            }
        });

        // create menu structure and add items
        fileMenu.add(newScenarioItem);
        fileMenu.add(loadScenarioItem);
        fileMenu.add(saveScenarioItem);
        fileMenu.add(saveScenarioAsItem);
        fileMenu.add(exportToShp);
        fileMenu.add(separator);
        fileMenu.add(quitItem);

        if (enablenewScenarioItem == false) {
            newScenarioItem.setEnabled(false);
        }
        if (enableloadScenarioItem == false) {
            loadScenarioItem.setEnabled(false);
        }
        if (enablesaveScenarioItem == false) {
            saveScenarioItem.setEnabled(false);
        }
        if (enablesaveScenarioAsItem == false) {
            saveScenarioAsItem.setEnabled(false);
        }
        if (enableexportToShp == false) {
            exportToShp.setEnabled(false);
        }
        if (enableseparator == false) {
            separator.setEnabled(false);
        }
        if (enablequitItem == false) {
            quitItem.setEnabled(false);
        }

    }

    public void reAdjustToolsMenu() {
        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        System.out.println("In readjustToolsMenu: " + REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().getName() + "\n");

        if (!state.arePodsSelected()) {
            if (enablevoronoi == true) {
                voronoi.setEnabled(false);
            }

            if (enableproportionalPartitioning == true) {
                proportionalPartitioning.setEnabled(false);
            }

            if (enablenck == true) {
                nck.setEnabled(false);
            }

            if (enableequalPopulation == true) {
                equalPopulation.setEnabled(true);
            }

            if (enabletrafficAnalysis == true) {
                trafficAnalysis.setEnabled(false);
            }

            if (enabletransitAnalysis == true) {
                transitAnalysis.setEnabled(false);
            }

            if (enablevulnAnalysis == true) {
                vulnAnalysis.setEnabled(false);
            }

            if (enablepodAnalysis == true) {
                podAnalysis.setEnabled(false);
            }

            if (enabletype2VulnAnalysis == true) {
                type2VulnAnalysis.setEnabled(false);
            }

            if (enabletype2ResMgmt == true) {
                type2ResMgmt.setEnabled(false);
            }

            if (enablegtfsGraphBuilder == true) {
                gtfsGraphBuilder.setEnabled(false);
            }

            if (enablemultiUniformPartitioning == true) {
                multiUniformPartitioning.setEnabled(true);
            }

            if (enablemaximizeCoverage == true) {
                maximizeCoverage.setEnabled(true);
            }

            if (enablemaximizeCoverageWTransit == true) {
                maximizeCoverageWTransit.setEnabled(true);
            }

            if (enablemaximizeCoverageAddTransitToPOD == true) {
                maximizeCoverageAddTransitToPOD.setEnabled(true);
            }

            if (enablereassignVulnPops == true) {
                reassignVulnPops.setEnabled(true);
            }

            System.out.println("In readjustToolsMenu: pods not selected\n");
        } else {
            if (state.getCatchmentAreasGiven()) {
                System.out.println("catchment areas given\n");
                if (state.isTrafficAnalysisPerformed()) {
                    if (enablevoronoi == true) {
                        voronoi.setEnabled(true);
                    }

                    if (enableproportionalPartitioning == true) {
                        proportionalPartitioning.setEnabled(true);
                    }

                    if (enablenck == true) {
                        nck.setEnabled(true);
                    }

                    if (enableequalPopulation == true) {
                        equalPopulation.setEnabled(true);
                    }

                    if (enablepodAnalysis == true) {
                        podAnalysis.setEnabled(true);
                    }

                    if (enabletype2VulnAnalysis == true) {
                        type2VulnAnalysis.setEnabled(true);
                    }

                    if (enablegtfsGraphBuilder == true) {
                        gtfsGraphBuilder.setEnabled(true);
                    }

                    if (enablecoverageAnalysis == true) {
                        coverageAnalysis.setEnabled(true);
                    }

                    if (enabletrafficAnalysis == true) {
                        trafficAnalysis.setEnabled(true);
                    }

                    if (enabletransitAnalysis == true) {
                        transitAnalysis.setEnabled(true);
                    }

                    if (enabletransportationVulnerabilityAnalysis == true) {
                        transportationVulnerabilityAnalysis.setEnabled(true);
                    }

                    if (enablevulnAnalysis == true) {
                        vulnAnalysis.setEnabled(true);
                    }

                    if (enablepodAnalysis == true) {
                        podAnalysis.setEnabled(true);
                    }

                    if (enablemultiUniformPartitioning == true) {
                        multiUniformPartitioning.setEnabled(true);
                    }

                    if (enablemaximizeCoverage == true) {
                        maximizeCoverage.setEnabled(true);
                    }

                    if (enablemaximizeCoverageWTransit == true) {
                        maximizeCoverageWTransit.setEnabled(true);
                    }

                    if (enablemaximizeCoverageAddTransitToPOD == true) {
                        maximizeCoverageAddTransitToPOD.setEnabled(true);
                    }

                    if (enablereassignVulnPops == true) {
                        reassignVulnPops.setEnabled(true);
                    }

                    if (state.isType2VulnAnalysisPerformed()) {
                        type2ResMgmt.setEnabled(true);
                    } else {
                        type2ResMgmt.setEnabled(false);
                    }
                    System.out.println("catchment areas given and replan executed\n");
                } else {

                    if (enablevoronoi == true) {
                        voronoi.setEnabled(true);
                    }

                    if (enableproportionalPartitioning == true) {
                        proportionalPartitioning.setEnabled(true);
                    }

                    if (enablenck == true) {
                        nck.setEnabled(true);
                    }

                    if (enableequalPopulation == true) {
                        equalPopulation.setEnabled(true);
                    }

                    if (enablecoverageAnalysis == true) {
                        coverageAnalysis.setEnabled(true);
                    }

                    if (enabletrafficAnalysis == true) {
                        trafficAnalysis.setEnabled(true);
                    }

                    if (enabletransitAnalysis == true) {
                        transitAnalysis.setEnabled(true);
                    }

                    if (enabletransportationVulnerabilityAnalysis == true) {
                        transportationVulnerabilityAnalysis.setEnabled(true);
                    }

                    if (enablevulnAnalysis == true) {
                        vulnAnalysis.setEnabled(true);
                    }

                    if (enablepodAnalysis == true) {
                        podAnalysis.setEnabled(true);
                    }

                    if (enabletype2VulnAnalysis == true) {
                        type2VulnAnalysis.setEnabled(true);
                    }
                    if (enablegtfsGraphBuilder == true) {
                        gtfsGraphBuilder.setEnabled(true);
                    }

                    if (enablemultiUniformPartitioning == true) {
                        multiUniformPartitioning.setEnabled(true);
                    }

                    if (enablemaximizeCoverage == true) {
                        maximizeCoverage.setEnabled(true);
                    }

                    if (enablemaximizeCoverageWTransit == true) {
                        maximizeCoverageWTransit.setEnabled(true);
                    }

                    if (enablemaximizeCoverageAddTransitToPOD == true) {
                        maximizeCoverageAddTransitToPOD.setEnabled(true);
                    }

                    if (enablereassignVulnPops == true) {
                        reassignVulnPops.setEnabled(true);
                    }

                    if (state.isType2VulnAnalysisPerformed()) {
                        type2ResMgmt.setEnabled(true);
                    } else {
                        type2ResMgmt.setEnabled(false);
                    }
                    System.out.println("catchment areas given and replan not executed\n");
                }
            } else {
                if (enablevoronoi == true) {
                    voronoi.setEnabled(true);
                }

                if (enableproportionalPartitioning == true) {
                    proportionalPartitioning.setEnabled(true);
                }

                if (enablenck == true) {
                    nck.setEnabled(true);
                }

                if (enableequalPopulation == true) {
                    equalPopulation.setEnabled(true);
                }

                if (enablecoverageAnalysis == true) {
                    coverageAnalysis.setEnabled(false);
                }

                if (enabletrafficAnalysis == true) {
                    trafficAnalysis.setEnabled(false);
                }

                if (enabletransitAnalysis == true) {
                    transitAnalysis.setEnabled(false);
                }

                if (enablevulnAnalysis == true) {
                    vulnAnalysis.setEnabled(false);
                }

                if (enablepodAnalysis == true) {
                    podAnalysis.setEnabled(false);
                }

                if (enabletype2ResMgmt == true) {
                    type2ResMgmt.setEnabled(false);
                }

                if (enabletype2VulnAnalysis == true) {
                    type2VulnAnalysis.setEnabled(false);
                }

                if (enablegtfsGraphBuilder == true) {
                    gtfsGraphBuilder.setEnabled(true);
                }

                if (enablemultiUniformPartitioning == true) {
                    multiUniformPartitioning.setEnabled(true);
                }

                if (enablemaximizeCoverage == true) {
                    maximizeCoverage.setEnabled(true);
                }

                if (enablemaximizeCoverageWTransit == true) {
                    maximizeCoverageWTransit.setEnabled(true);
                }

                if (enablemaximizeCoverageAddTransitToPOD == true) {
                    maximizeCoverageAddTransitToPOD.setEnabled(true);
                }

                if (enablereassignVulnPops == true) {
                    reassignVulnPops.setEnabled(true);
                }

                System.out.println("catchment areas not given\n");
            }
        }
        this.setVisible(false);
        this.setVisible(true);
        //this.repaint();
    }

    private void equalPopulationPartitionItemActionPerformed(java.awt.event.ActionEvent evt) {
        ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        //s.setTask(new Replan( s));
        //new SelectTableNames( REPLAN.getMainFrame() );
//if pods selected pop warning message
        if (state.arePodsSelected()) {
            System.out.println("PODS selected, showing yes no dialog\n");
            JFrame frame = new JFrame();
            String message = "Existing PODs, Catchment Areas will be deleted. Do you want to proceed?\n";
            int answer = JOptionPane.showConfirmDialog(frame, message);
            if (answer == JOptionPane.YES_OPTION) {
                // User clicked YES.
                System.out.println("EQPOP confirm dialog - yes clicked");
            } else if (answer == JOptionPane.NO_OPTION) {
                // User clicked NO.
                System.out.println("EQPOP confirm dialog - no clicked");
                frame.dispose();
                return;
            }

        }

        s.setTask(new Replan(s));

        System.out.println("Current Thread[2] - > " + Thread.currentThread().getName());
        s.getTask().initEqPopPartitioning();
    }

    private void podAnalysisActionPerformed(java.awt.event.ActionEvent evt) {
        try {

            ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
            @SuppressWarnings("static-access")
            String user = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().getAuthor();
            String prefix = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().getName();
            String command = "java -jar Analyzer.jar " + user + " " + prefix;
            System.out.println("Running command: " + command);
            //Runtime.getRuntime().exec(command);

            String[] args = new String[2];
            args[0] = user;
            args[1] = prefix;
            PODAnalysis p = new PODAnalysis();
            p.startAnalyzer(args);

            System.out.println("executed command podanalyzer\n");
        } catch (IOException ex) {
            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void type2VulnAnalysisActionPerformed(java.awt.event.ActionEvent evt) {

        VulnerabilitySelectionFrame frame = new VulnerabilitySelectionFrame();
        frame.setVisible(true);
    }

    private void type2ResManActionPerformed(java.awt.event.ActionEvent evt) {

        ResourceManagementFrame frame = new ResourceManagementFrame();
        frame.setVisible(true);
    }

    private void transitAnalysisActionPerformed(java.awt.event.ActionEvent evt) {
        System.out.println("Here is where we do the analysis of the public transportation network");
        PubTrans.performAnalysis();
        JOptionPane.showMessageDialog(this.owner, "This feature will be implemented in upcoming versions");
        /*
         ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
         ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
         Connection c = REPLAN.getController().getConnection();
         edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateStation2POD(state, c, 0.01, "class01");
         edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateStation2POD(state, c, 0.02, "class02");
         edu.unt.cerl.replan.pubtrans.model.DBQueries.calculateStation2POD(state, c, 0.04, "class04");
         */

    }

    private void voronoiActionPerformed(java.awt.event.ActionEvent evt) {
        ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        ScenarioState state = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        s.setTask(new Replan(s));
        ProgressWindow progressWin = new ProgressWindow(s.getTask());
        s.getTask().initVoronoi();
    }

    private void proportionalPartitioningActionPerformed(java.awt.event.ActionEvent evt) {
        ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        s.setTask(new Replan(s));

        s.getTask().initProportionalPartitioning();
    }

    private void regionalTransportationVulnerabilityAnalysisActionPerformed(java.awt.event.ActionEvent evt) {
        //At this point, we need to take care of garbage collection tasks if RE-PLAN had already been run previously
        ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        s.setTask(new Replan(s));
//        CoverageOptions coverageOptions = new CoverageOptions(REPLAN.getMainFrame(), true);
//        coverageOptions.setVisible(true);
//        s.setCoverageOptions(coverageOptions.returnCoverageOptions());
//        ProgressWindow progressWin = new ProgressWindow(s.getTask());
        s.getTask().initCoverageAnalysis();
    }

    private void coverageAnalysisActionPerformed(java.awt.event.ActionEvent evt) {
        //At this point, we need to take care of garbage collection tasks if RE-PLAN had already been run previously
        ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        s.setTask(new Replan(s));
//        CoverageOptions coverageOptions = new CoverageOptions(REPLAN.getMainFrame(), true);
//        coverageOptions.setVisible(true);
//        s.setCoverageOptions(coverageOptions.returnCoverageOptions());
//        ProgressWindow progressWin = new ProgressWindow(s.getTask());
        s.getTask().initCoverageAnalysis();
    }

    private void trafficAnalysisActionPerformed(java.awt.event.ActionEvent evt) {
        //At this point, we need to take care of garbage collection tasks if RE-PLAN had already been run previously
        ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        ProgressWindow progressWin = new ProgressWindow(s.getTask());
        s.getTask().initTrafficAnalysis();
    }

    private void nckActionPerformed(java.awt.event.ActionEvent evt) {
        ScenarioPanel s = REPLAN.getMainFrame().getTabs().getSelectedScenario();
        s.setTask(new Replan(s));
        //ProgressWindow progressWin = new ProgressWindow(s.getTask());
        s.getTask().initNcK();
        //new OptimizationTableNames(REPLAN.getMainFrame().getTabs().getSelectedScenario().getState());
//        JOptionPane.showMessageDialog(this.owner, "This feature will be implemented in upcoming versions");

    }

    private void vulnAnalysisActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this.owner, "This feature will be implemented in upcoming versions");

    }

    private void continuousActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this.owner, "This feature will be implemented in upcoming versions");

    }

    /**
     * This method initializes the menu items of the tools menu
     */
    private void initToolsMenu() {
        toolsMenu = new JMenu("Tools");
        regionalAnalysis = new JMenu("Regional Analysis");
        planCreation = new JMenu("Plan Creation");
        planAnalysis = new JMenu("Plan Analysis");
        experiments = new JMenu("Experiments");
        type2Analysis = new JMenu("Type 2 Analysis");
        regionalTransportationVulnerabilityAnalysis = new JMenuItem("Transportational Vulnerable Population Analysis");
        displayTransitNetwork = new JMenuItem("Display Transit Network");
        voronoi = new JMenuItem("Closest POD Partitioning");
        equalPopulation = new JMenuItem("Equal Population Partitioning");
        proportionalPartitioning = new JMenuItem("Proportional Partitioning");
        nck = new JMenuItem("POD Selection Optimization (n-choose-k)");
        coverageAnalysis = new JMenuItem("Coverage Analysis");
        trafficAnalysis = new JMenuItem("Traffic Analysis");
        transitAnalysis = new JMenuItem("Transit Analysis");
        transportationVulnerabilityAnalysis = new JMenuItem("Transportation Vulnerability Distance-to-POD Analysis");
        vulnAnalysis = new JMenuItem("Vulnerability Analysis");
        podAnalysis = new JMenuItem("POD Analysis");
        multiUniformPartitioning = new JMenuItem("Multi-Uniform Partitioning");
        maximizeCoverage = new JMenuItem("Maximize Coverage of Transportation Vulnerable Populations");
        maximizeCoverageWTransit = new JMenuItem("Maximize Coverage of Transportation Vulnerable Populations Using Public Transit");
        maximizeCoverageAddTransitToPOD = new JMenuItem("Maximize Coverage of Transportation Vulnerable Populations after Adding a Public Transit Stop at each POD");
        reassignVulnPops = new JMenuItem("Reassign Vulnerable Populations to Different Catchment Areas to Increase Coverage");
        type2VulnAnalysis = new JMenuItem("Type 2 Vulnerability Analysis");
        type2ResMgmt = new JMenuItem("Resource Management");
        gtfsGraphBuilder = new JMenuItem("Build GTF DiGraph");
        ;

        regionalAnalysis.add(regionalTransportationVulnerabilityAnalysis);
        regionalAnalysis.add(displayTransitNetwork);

        planCreation.add(voronoi);
        planCreation.add(equalPopulation);
        planCreation.add(proportionalPartitioning);
        planCreation.add(nck);
        planAnalysis.add(coverageAnalysis);
        planAnalysis.add(trafficAnalysis);
        planAnalysis.add(transitAnalysis);
        planAnalysis.add(transportationVulnerabilityAnalysis);
        planAnalysis.add(vulnAnalysis);
        planAnalysis.add(podAnalysis);
        planAnalysis.add(type2VulnAnalysis);

        experiments.add(multiUniformPartitioning);
        experiments.add(maximizeCoverage);
        experiments.add(maximizeCoverageWTransit);
        experiments.add(maximizeCoverageAddTransitToPOD);
        experiments.add(reassignVulnPops);
        experiments.add(gtfsGraphBuilder);

        type2Analysis.add(type2VulnAnalysis);
        type2Analysis.add(type2ResMgmt);

        if (enableregionalAnalysis == true) {
            toolsMenu.add(regionalAnalysis);
        }

        if (enableplanCreation == true) {
            toolsMenu.add(planCreation);
        }

        if (enableplanAnalysis == true) {
            toolsMenu.add(planAnalysis);
        }

        if (enabletype2Analysis == true) {
            toolsMenu.add(type2Analysis);
        }

        if (enableexperiments == true) {
            toolsMenu.add(experiments);
        }

        if (enableregionalTransportationVulnerabilityAnalysis == true) {
            regionalTransportationVulnerabilityAnalysis.setEnabled(true);
        } else {
            regionalTransportationVulnerabilityAnalysis.setEnabled(false);
        }

        if (enabledisplayTransitNetwork == true) {
            displayTransitNetwork.setEnabled(true);
        } else {
            displayTransitNetwork.setEnabled(false);
        }

        if (enablevoronoi == true) {
            voronoi.setEnabled(true);
        } else {
            voronoi.setEnabled(false);
        }

        if (enableproportionalPartitioning == true) {
            proportionalPartitioning.setEnabled(true);
        } else {
            proportionalPartitioning.setEnabled(false);
        }

        if (enablenck == true) {
            nck.setEnabled(true);
        } else {
            nck.setEnabled(false);
        }

        if (enableequalPopulation == true) {
            equalPopulation.setEnabled(true);
        } else {
            equalPopulation.setEnabled(false);
        }

        if (enablecoverageAnalysis == true) {
            coverageAnalysis.setEnabled(true);
        } else {
            coverageAnalysis.setEnabled(false);
        }

        if (enabletrafficAnalysis == true) {
            trafficAnalysis.setEnabled(true);
        } else {
            trafficAnalysis.setEnabled(false);
        }

        if (enabletransitAnalysis == true) {
            transitAnalysis.setEnabled(true);
        } else {
            transitAnalysis.setEnabled(false);
        }

        if (enabletransportationVulnerabilityAnalysis == true) {
            transportationVulnerabilityAnalysis.setEnabled(true);
        } else {
            transportationVulnerabilityAnalysis.setEnabled(false);
        }

        if (enablevulnAnalysis == true) {
            vulnAnalysis.setEnabled(true);
        } else {
            vulnAnalysis.setEnabled(false);
        }

        if (enabletype2Analysis == true) {
            type2Analysis.setEnabled(true);
        } else {
            type2Analysis.setEnabled(false);
        }

        if (enablegtfsGraphBuilder == true) {
            gtfsGraphBuilder.setEnabled(true);
        } else {
            gtfsGraphBuilder.setEnabled(false);
        }

        if (enablepodAnalysis == true) {
            podAnalysis.setEnabled(true);
        } else {
            podAnalysis.setEnabled(false);
        }

        if (enablemultiUniformPartitioning == true) {
            multiUniformPartitioning.setEnabled(true);
        } else {
            multiUniformPartitioning.setEnabled(false);
        }

        if (enablemaximizeCoverage == true) {
            maximizeCoverage.setEnabled(true);
        } else {
            maximizeCoverage.setEnabled(false);
        }

        if (enablemaximizeCoverageWTransit == true) {
            maximizeCoverageWTransit.setEnabled(true);
        } else {
            maximizeCoverageWTransit.setEnabled(false);
        }

        if (enablemaximizeCoverageAddTransitToPOD == true) {
            maximizeCoverageAddTransitToPOD.setEnabled(true);
        } else {
            maximizeCoverageAddTransitToPOD.setEnabled(false);
        }

        if (enablereassignVulnPops == true) {
            reassignVulnPops.setEnabled(true);
        } else {
            reassignVulnPops.setEnabled(false);
        }

//        toolsMenu = new JMenu("Tools");
//        replanItem = new JMenuItem("RE-PLAN!");
//        publicTransportation = new JMenuItem("Public Transportation Analysis");
//        partitionItem = new JMenuItem("Equal Population Partitioning");
//        hybridAllItem = new JMenuItem("Hybrid try out all method");
//        hybridItem = new JMenuItem("Hybrid method");
        optimizationItem = new JMenuItem("POD Selection (N choose K)");
//        podAnalyzerItem = new JMenuItem("PODAnalyzer...");

        // set keystroke and action listener for replan menu item
        trafficAnalysis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_T,
                java.awt.event.InputEvent.CTRL_MASK));
        coverageAnalysis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_C,
                java.awt.event.InputEvent.CTRL_MASK));
        transitAnalysis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_P,
                java.awt.event.InputEvent.CTRL_MASK));
        voronoi.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_V,
                java.awt.event.InputEvent.CTRL_MASK));
        proportionalPartitioning.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_P,
                java.awt.event.InputEvent.CTRL_MASK));
        nck.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_O,
                java.awt.event.InputEvent.CTRL_MASK));
        vulnAnalysis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_W,
                java.awt.event.InputEvent.CTRL_MASK));
        equalPopulation.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_E,
                java.awt.event.InputEvent.CTRL_MASK));
        podAnalysis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_D,
                java.awt.event.InputEvent.CTRL_MASK));

        //transitAnalysis.setEnabled(true);
        regionalTransportationVulnerabilityAnalysis.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                                
                new ClassifyPopulationBlocksTask().classifyPopBlocks();
                 
                /*
                try {
                    System.out.println("You clicked on Tools>Regional Analysis>Regional Transportation Vulnerability Analysis");
                    // do classification of polygons according to number of transportation vulnerable individuals
                    ClassifyPopulationBlocks.classifyPopBlocks();
                } catch (SQLException ex) {
                    Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CQLException ex) {
                    Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
                */
            
            }
            
        });

        displayTransitNetwork.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    new DisplayTransitNetworkTask().DisplayTransitNetwork();
                    /*
                    System.out.println("You clicked on Tools>Regional Analysis>Display Transit Network");
                    try {
                    DisplayTransitNetwork.DisplayTransitNetwork();
                    //ClassifyPopulationBlocks.classifyPopBlocks();
                    //ClassifyPopulationBlocks.classifyPopBlocks();
                    } catch (CQLException ex) {
                    Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                     */
                } catch (CQLException ex) {
                    Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        coverageAnalysis.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Coverage Analysis");
                        coverageAnalysisActionPerformed(evt);
                    }
                });

        type2VulnAnalysis.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Type2 Vuln Analysis");
                        type2VulnAnalysisActionPerformed(evt);
                    }
                });

        type2ResMgmt.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Type2 Res Mgmt");
                        type2ResManActionPerformed(evt);
                    }
                });

        gtfsGraphBuilder.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Experiments> Build GTFS Graph");
                        try {
                            //type2VulnAnalysisActionPerformed(evt);
                            new GtfsGraph();
                        } catch (SQLException ex) {
                            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

        //trafficAnalysis.setEnabled(true);
        trafficAnalysis.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Traffic Analysis");
                        trafficAnalysisActionPerformed(evt);
                    }
                });

        //transitAnalysis.setEnabled(true);
        transitAnalysis.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Public Transportation Analysis");
                        transitAnalysisActionPerformed(evt);
                    }
                });

        transportationVulnerabilityAnalysis.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        //  try {
                        //   try {
                        // do classification of polygons according to number of transportation vulnerable individuals
                        // ClassifyPopulationBlocks.classifyPopBlocks();
                        //add layer to map
                        //     } catch (CQLException ex) {
                        //   Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        // }
                        // } catch (SQLException ex) {
                        //   Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        //}

                        try {
                            System.out.println("You clicked on Tools > Transportation Vulnerability Analysis");
                            String command = "java -jar ./VulnerabilityAnalyzer/VulnerabilityAnalyzer.jar " + REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().getAuthor() + " " + REPLAN.getMainFrame().getTabs().getSelectedScenario().getState().getName() + " 0";
                            System.out.println("command: " + command);
                            Runtime.getRuntime().exec(command);
                        } catch (IOException ex) {
                            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

        voronoi.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Closest POD Partitioning\n");
                        voronoiActionPerformed(evt);
                    }
                });

        proportionalPartitioning.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Proportional Partitioning\n");
                        proportionalPartitioningActionPerformed(evt);
                    }
                });

        nck.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>nck\n");
                        nckActionPerformed(evt);
                    }
                });

        vulnAnalysis.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>vuln\n");
                        vulnAnalysisActionPerformed(evt);
                    }
                });

        //voronoi.setEnabled(true);
        equalPopulation.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("You clicked on Tools>Equal Population\n");
                        equalPopulationPartitionItemActionPerformed(evt);
                    }
                });

        //podAnalysis.setEnabled(true);
        podAnalysis.addActionListener(
                new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        podAnalysisActionPerformed(evt);
                    }
                });

        multiUniformPartitioning.addActionListener(
                new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                            new MultiUniformPartitioning();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

        maximizeCoverage.addActionListener(
                new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                            try {
                                DetermineLocsWithBestCoverage.maximizeReach(1000);
                            } catch (CQLException ex) {
                                Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

        maximizeCoverageWTransit.addActionListener(
                new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                            try {
                                DetermineLocsWithBestCoverage.maximizeReach_transit(1000);
                            } catch (CQLException ex) {
                                Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

        maximizeCoverageAddTransitToPOD.addActionListener(
                new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        try {
                            DetermineLocsWithBestCoverage.maximizeReach_add_transit_to_POD(1000, 150);
                        } catch (SQLException ex) {
                            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (CQLException ex) {
                            Logger.getLogger(REPLANMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
        reassignVulnPops.addActionListener(
                new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        //DetermineLocsWithBestCoverage.reassignVulnPops(1000);
                    }
                });

    }

    /**
     * This method initializes the menu items of the help menu
     */
    private void initHelpMenu() {

        // initialize menus
        helpMenu = new JMenu("Help");
        helpItem = new JMenuItem("Help...");
        aboutItem = new JMenuItem("About...");

        // set action listener for help item
        helpItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openPDF();

            }
        });

        //set action listener for about item
        aboutItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showInfo();

            }
        });

        // add menu items to help menu
        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

        if (enablehelpItem == true) {
            helpItem.setEnabled(true);
        } else {
            helpItem.setEnabled(false);
        }

        if (enableaboutItem == true) {
            aboutItem.setEnabled(true);
        } else {
            aboutItem.setEnabled(false);
        }

    }

    /**
     * This method opens the user manual in PDF format
     */
    private void openPDF() {

        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "
                    + "manual.pdf");   //open the file chart.pdf
            /**
             * uncomment for MAC or gnome environments
             */
            // mac Runtime.getRuntime().exec("open /Users/source/abc.pdf");
            // gnome String[] cmd = {"gnome-open", filename};
            // gnome process = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) //catch any exceptions here
        {
            System.out.println("Error" + e);  //print the error

        }

    }

    /**
     * This metho displays information about the current version of replan
     */
    public void showInfo() {
        // TODO read this from a file
        String[] msg = new String[5];
        msg[

0] = "RE-PLAN 2.5";
        msg[

1] = "2014";
        msg[

2] = "CERL";
        msg[

3] = "University of North Texas";
        JOptionPane.showMessageDialog(this, msg, "Info",
                JOptionPane.INFORMATION_MESSAGE);

    }

    public void setExportToShape(boolean value) {
        exportToShp.setEnabled(value);
        //this.repaint();

    }
}
