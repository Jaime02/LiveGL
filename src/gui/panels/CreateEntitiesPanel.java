
package gui.panels;

import gui.MainForm;
import scene.Entity;
import scene.MeshEntity;
import scene.Resources;
import utils.Color;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.JButton;

public class CreateEntitiesPanel extends JPanel {
    public MainForm mf;

    private javax.swing.JTextField colorText;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton maskAddCubeButton;
    private javax.swing.JButton maskAddSphereButton;
    private javax.swing.JButton maskDelButton;

    public CreateEntitiesPanel(MainForm mf) {
        this.mf = mf;

        jPanel3 = new JPanel();
        maskAddCubeButton = new JButton();
        maskDelButton = new JButton();
        maskAddSphereButton = new JButton();
        colorText = new javax.swing.JTextField();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Add objects"));

        maskAddCubeButton.setText("Cube");
        maskAddCubeButton.setToolTipText("Create a cube");
        maskAddCubeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAddCubeButtonActionPerformed(evt);
            }
        });

        maskDelButton.setText("Delete");
        maskDelButton.setToolTipText("Deletes the selected object");
        maskDelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskDelButtonActionPerformed(evt);
            }
        });

        maskAddSphereButton.setText("Sphere");
        maskAddSphereButton.setToolTipText("Create a sphere");
        maskAddSphereButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAddSphereButtonActionPerformed(evt);
            }
        });

        colorText.setText("255,255,255,255");
        colorText.setToolTipText("Color RGBA");
        colorText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup().addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(colorText)
                                .addGroup(jPanel3Layout.createSequentialGroup().addComponent(maskAddCubeButton)
                                        .addGap(6, 6, 6).addComponent(maskAddSphereButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(maskDelButton).addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap()));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup().addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(maskAddCubeButton).addComponent(maskAddSphereButton)
                                .addComponent(maskDelButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(colorText, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(
                    jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap()));
    }

    private void maskAddCubeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        MeshEntity me = new MeshEntity(Resources.MESH_BOX);
        me.setTag(Entity.TAG_OBJ);
        me.setColor(Color.WHITE);
        mf.addMeshEntityToSceneCenterAndResizeIt(me);

        mf.needUpdate();
    }

    private void maskDelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Remove from simulation and scene
        for (Entity e : mf.selectedEntities) {
            if (e.getTag() == Entity.TAG_OBJ) {
                mf.scene.getEntities().remove(e);
            }
        }
        mf.clearSelection();
        mf.needUpdate();
    }

    private void maskAddSphereButtonActionPerformed(java.awt.event.ActionEvent evt) {
        MeshEntity me = new MeshEntity(Resources.MESH_SPHERE);
        me.setTag(Entity.TAG_OBJ);
        me.setColor(Color.WHITE);
        mf.addMeshEntityToSceneCenterAndResizeIt(me);
        mf.needUpdate();
    }

    private void colorTextActionPerformed(java.awt.event.ActionEvent evt) {
        int color = Color.parse(colorText.getText());
        for (Entity e : mf.selectedEntities) {
            if (e instanceof MeshEntity) {
                MeshEntity me = (MeshEntity)e;
                me.setColor(color);
            }
        }
        mf.needUpdate();
    }
}
