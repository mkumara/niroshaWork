/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OptimizationTableNames.java
 *
 * Created on Sep 11, 2010, 6:27:37 PM
 */
package edu.unt.cerl.replan.optimization;

import edu.unt.cerl.applicationframework.model.DefaultConstants;
import edu.unt.cerl.replan.REPLAN;
import edu.unt.cerl.replan.controller.action.ProgressBar;
import edu.unt.cerl.replan.model.ScenarioState;
import edu.unt.cerl.replan.model.UserState;
import edu.unt.cerl.replan.view.mainframe.ScenarioPanel;
import edu.unt.cerl.replanexecution.Replan;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author tamara
 */
public class OptimizationTableNames extends javax.swing.JFrame {

    Map<String, String> m;
    ScenarioState scenario;
    //private Replan task;

    /** Creates new form OptimizationTableNames */
    public OptimizationTableNames(ScenarioState owner, ScenarioPanel currentScenario, Replan task) {
        this.m = new HashMap<String, String>();
        this.scenario = owner;
//        scenario.setEnabled(false);
        //this.task = task;
        task.setCurrent(0);
        task.setStatMessage("Initializing n-choose-k.");
        task.setStatusChanged(true);
        System.out.println("Initializing n-choose-k");
        initComponents();
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jRadioButton1 = new javax.swing.JRadioButton();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        podsField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        fixedButton = new javax.swing.JRadioButton();
        optimaButton = new javax.swing.JRadioButton();
        standardDeviationButton = new javax.swing.JRadioButton();
        stdDevFixedButton = new javax.swing.JRadioButton();
        standardDeviationButton1 = new javax.swing.JRadioButton();
        standardDeviationButton2 = new javax.swing.JRadioButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        jRadioButton1.setText("jRadioButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        podsField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        podsField.setText("10");
        podsField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                podsFieldActionPerformed(evt);
            }
        });

        jLabel4.setText("number of PODs");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Optimization Metric"));

        buttonGroup1.add(fixedButton);
        fixedButton.setText("Global Optimum - Population Goal");
        fixedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(optimaButton);
        optimaButton.setText("Local Optimum - Population Goal");
        optimaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optimaButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(standardDeviationButton);
        standardDeviationButton.setText("Local Optimum - Standard Deviation");
        standardDeviationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                standardDeviationButtonActionPerformed(evt);
            }
        });

        stdDevFixedButton.setText("Global Optimum - Standard Deviation");
        stdDevFixedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stdDevFixedButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(standardDeviationButton1);
        standardDeviationButton1.setText("POD by POD - Minimum Blackout");
        standardDeviationButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                greedyPODbyPODButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(standardDeviationButton2);
        standardDeviationButton2.setText("Imbalance Centrality");
        standardDeviationButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                standardDeviationButton2greedyPODbyPODButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(standardDeviationButton2)
                    .addComponent(standardDeviationButton1)
                    .addComponent(standardDeviationButton)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(optimaButton)
                        .addComponent(fixedButton)
                        .addComponent(stdDevFixedButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addContainerGap(197, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(fixedButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optimaButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stdDevFixedButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(standardDeviationButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(standardDeviationButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(standardDeviationButton2)
                .addContainerGap())
        );

        stdDevFixedButton.getAccessibleContext().setAccessibleName("Standard deviation with fixed optimum");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(podsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(445, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(60, 60, 60))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(podsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(cancelButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void podsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_podsFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_podsFieldActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
//        this.scenario.setEnabled(true);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        //if(this.podsField.getText())
        if (this.podsField.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "You must select a value for number of PODs!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } else if (!this.podsField.getText().matches(("[0-9_]*"))) {

            JOptionPane.showMessageDialog(this,
                    "Number of PODs should be a numerical value!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } else if (Integer.parseInt(this.podsField.getText()) > (REPLAN.getMainFrame().getTabs().getSelectedScenario().getPodList().get_number_of_pods())  ) {

            JOptionPane.showMessageDialog(this,
                    "Number of PODs should be less than total available number!",
                    "Naming error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }



        scenario = REPLAN.getMainFrame().getTabs().getSelectedScenario().getState();
        String base = scenario.getWorkingCopyName();
        m.put(Tables.BLOCKS, base + DefaultConstants.BLOCK_SUFFIX);
        m.put(Tables.POPULATION, base + DefaultConstants.POPULATION_SUFFIX);
        m.put(Tables.CENTROIDS, base + DefaultConstants.CENTROID_SUFFIX);
        m.put(Tables.NUM_PODS, this.podsField.getText());
        m.put(Tables.PODS, UserState.userId + "." + base + "_pods");
//        this.scenario.setEnabled(true);
        //new PMedian(m, scenario);

        PMedianTask task = new PMedianTask(m, scenario);
        ProgressBar pb = new ProgressBar();
        pb.setTask(task);
        task.addPropertyChangeListener(pb);
        task.execute();
        //task.setCurrent(10);
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void optimaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optimaButtonActionPerformed
        m.remove(Tables.CRITERION);
//        task.setCurrent(20);
//        task.setStatMessage("Finished preparing for Optimization");
//        task.setStatusChanged(true);
        m.put(Tables.CRITERION, Tables.ADJUSTING_OPTIMA);
//        task.setCurrent(100);
    }//GEN-LAST:event_optimaButtonActionPerformed

    private void fixedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedButtonActionPerformed
        m.remove(Tables.CRITERION);
//        task.setCurrent(20);
//        task.setStatMessage("Finished preparing for Optimization");
//        task.setStatusChanged(true);
        m.put(Tables.CRITERION, Tables.FIXED);
//        task.setCurrent(100);
    }//GEN-LAST:event_fixedButtonActionPerformed

    private void standardDeviationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_standardDeviationButtonActionPerformed
        m.remove(Tables.CRITERION);
//        task.setCurrent(20);
//        task.setStatMessage("Finished preparing for Optimization");
//        task.setStatusChanged(true);
        m.put(Tables.CRITERION, Tables.STD_DEV);
//        task.setCurrent(100);
    }//GEN-LAST:event_standardDeviationButtonActionPerformed

    private void stdDevFixedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stdDevFixedButtonActionPerformed
        m.remove(Tables.CRITERION);
//        task.setCurrent(20);
//        task.setStatMessage("Finished preparing for Optimization");
//        task.setStatusChanged(true);
        m.put(Tables.CRITERION, Tables.FIXED);
//        task.setCurrent(100);
    }//GEN-LAST:event_stdDevFixedButtonActionPerformed

    private void greedyPODbyPODButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_greedyPODbyPODButton1ActionPerformed
        // TODO add your handling code here:
        m.remove(Tables.CRITERION);
//        task.setCurrent(20);
//        task.setStatMessage("Finished preparing for Optimization");
//        task.setStatusChanged(true);
        m.put(Tables.CRITERION, Tables.MINBLACKOUT);
//        task.setCurrent(100);

    }//GEN-LAST:event_greedyPODbyPODButton1ActionPerformed

    private void standardDeviationButton2greedyPODbyPODButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_standardDeviationButton2greedyPODbyPODButton1ActionPerformed
        // TODO add your handling code here:
                m.remove(Tables.CRITERION);
                m.put(Tables.CRITERION, Tables.CENIMBALANCE);

    }//GEN-LAST:event_standardDeviationButton2greedyPODbyPODButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton fixedButton;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JButton okButton;
    private javax.swing.JRadioButton optimaButton;
    private javax.swing.JTextField podsField;
    private javax.swing.JRadioButton standardDeviationButton;
    private javax.swing.JRadioButton standardDeviationButton1;
    private javax.swing.JRadioButton standardDeviationButton2;
    private javax.swing.JRadioButton stdDevFixedButton;
    // End of variables declaration//GEN-END:variables
}