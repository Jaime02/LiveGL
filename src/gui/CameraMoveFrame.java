package gui;

import math.M;
import math.Transform;
import scene.Camera;
import utils.Parse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;


public class CameraMoveFrame extends javax.swing.JFrame {
    MainForm mainForm;
    
    final Transform transform = new Transform();
    float azimuth, inclination;
    
    public CameraMoveFrame(MainForm mainForm) {
        this.mainForm = mainForm;

        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        snapButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        stepsText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        rotText = new javax.swing.JTextField();
        doButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        delayText = new javax.swing.JTextField();

        jButton2.setText("jButton2");

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CameraMovements");

        snapButton.setText("Snap");
        snapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapButtonActionPerformed(evt);
            }
        });

        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Steps");

        stepsText.setText("100");

        jLabel3.setText("Rotation:");

        rotText.setText("360");

        doButton.setText("Do");
        doButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("delay(ms):");

        delayText.setText("100");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(doButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(applyButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(snapButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delayText))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(rotText)
                            .addComponent(stepsText))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(snapButton)
                    .addComponent(applyButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(stepsText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(rotText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(delayText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(doButton)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    private void snapButtonActionPerformed(java.awt.event.ActionEvent evt) {
        final Camera camera = mainForm.scene.getCamera();
        transform.set( camera.getTransform() );
        azimuth = camera.getAzimuth();
        inclination = camera.getInclination();
    }

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {
        final Camera camera = mainForm.scene.getCamera();
        camera.getTransform().set( transform );
        camera.setAzimuth( azimuth );
        camera.setInclination( inclination );
        mainForm.needUpdate();
    }

    private void doButtonActionPerformed(java.awt.event.ActionEvent evt) {
        final int steps = Parse.toInt( stepsText.getText() );
        final float rotation = Parse.toFloat( rotText.getText() );
        final float delay = Parse.toFloat( delayText.getText() );
        
        Thread task = new Thread( new Runnable() {
            @Override
            public void run() {
                final float incRot = rotation / steps;
                for(int i = 0; i < steps; ++i){
                    mainForm.scene.getCamera().moveAzimuthAndInclination(incRot * M.DEG_TO_RAD, 0.0f);
                    mainForm.scene.getCamera().updateObservation();
                    mainForm.needUpdate();
                    try {
                        Thread.sleep( (long) delay);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CameraMoveFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        task.start();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JTextField delayText;
    private javax.swing.JButton doButton;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField rotText;
    private javax.swing.JButton snapButton;
    private javax.swing.JTextField stepsText;
    // End of variables declaration//GEN-END:variables
}
