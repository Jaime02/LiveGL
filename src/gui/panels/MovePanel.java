/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.panels;

import gui.MainForm;
import math.M;
import math.Vector3f;
import scene.Entity;
import scene.MeshEntity;
import scene.Scene;
import utils.Parse;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;

public class MovePanel extends javax.swing.JPanel {
    final MainForm mf;
    final ArrayList<Vector3f> snapBeadPositions = new ArrayList<>();
    final HashMap<Integer, ArrayList<Entity>> selections = new HashMap<>();

    public MovePanel(MainForm mf) {
        this.mf = mf;

        jLabel3 = new javax.swing.JLabel();
        speedText = new javax.swing.JTextField();
        leftButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        rightButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        backwardsButton = new javax.swing.JButton();
        rXPButton = new javax.swing.JButton();
        rYPButton = new javax.swing.JButton();
        rZPButton = new javax.swing.JButton();
        rZNButton = new javax.swing.JButton();
        rYNButton = new javax.swing.JButton();
        rXNButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        angleText = new javax.swing.JTextField();

        jLabel3.setText("Step size:");

        speedText.setText("0.100");
        speedText.setToolTipText("Step size of the movements and scaling");

        leftButton.setText("←");
        leftButton.setToolTipText("move selected points left");
        leftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftButtonActionPerformed(evt);
            }
        });

        upButton.setText("↑");
        upButton.setToolTipText("move selected points up");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        rightButton.setText("→");
        rightButton.setToolTipText("move selected points right");
        rightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightButtonActionPerformed(evt);
            }
        });

        downButton.setText("↓");
        downButton.setToolTipText("move selected points down");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        forwardButton.setText("↗");
        forwardButton.setToolTipText("move selected points forward");
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });

        backwardsButton.setText("↖");
        backwardsButton.setToolTipText("move selected points back");
        backwardsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backwardsButtonActionPerformed(evt);
            }
        });

        rXPButton.setText("Rx+");
        rXPButton.setToolTipText("rotate group of particles along X, center is the center of the group of particles");
        rXPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rXPButtonActionPerformed(evt);
            }
        });

        rYPButton.setText("Ry+");
        rYPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rYPButtonActionPerformed(evt);
            }
        });

        rZPButton.setText("Rz+");
        rZPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rZPButtonActionPerformed(evt);
            }
        });

        rZNButton.setText("Rz-");
        rZNButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rZNButtonActionPerformed(evt);
            }
        });

        rYNButton.setText("Ry-");
        rYNButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rYNButtonActionPerformed(evt);
            }
        });

        rXNButton.setText("Rx-");
        rXNButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rXNButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Angle step:");

        angleText.setText("10");
        angleText.setToolTipText("Angle step in degrees");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup().addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(speedText))
                                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(leftButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                .addComponent(backwardsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup()
                                                .addComponent(downButton, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(rightButton, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(upButton, GroupLayout.PREFERRED_SIZE, 43,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(forwardButton,
                                                                GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addGroup(GroupLayout.Alignment.LEADING,
                                        layout.createSequentialGroup().addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(angleText))
                                .addGroup(GroupLayout.Alignment.LEADING,
                                        layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(rXNButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(rXPButton))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(rYNButton, GroupLayout.Alignment.TRAILING,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(rYPButton, GroupLayout.Alignment.TRAILING))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(rZPButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(rZNButton, GroupLayout.PREFERRED_SIZE, 53,
                                                                GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(upButton)
                                        .addComponent(forwardButton).addComponent(backwardsButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(downButton)
                                        .addComponent(leftButton).addComponent(rightButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel3).addComponent(speedText,
                                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(rXPButton).addComponent(rYPButton)
                                        .addComponent(rZPButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(rXNButton).addComponent(rYNButton)
                                        .addComponent(rZNButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(angleText,
                                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }

    public ArrayList<Entity> getSelection(final int n) {
        return selections.get(n);
    }

    public void snapSelection(final int n) {
        final ArrayList<Entity> sel = new ArrayList<>(mf.selectedEntities);
        selections.put(n, sel);
    }

    public Entity getBeadEntity() {
        final int n = 0;

        // check bead number n in selection
        final ArrayList<Entity> sel = mf.selectedEntities;
        if (n < 0 || n >= sel.size()) {
            return null;
        }
        return sel.get(n);
    }

    public void applyDisplacement(float x, float y, float z) {
        final Entity e = getBeadEntity();
        if (e == null) {
            return;
        }

        final Vector3f t = new Vector3f(x, y, z);
        t.multLocal(getDisplacementStep());

        e.getTransform().getTranslation().addLocal(t);

        mf.transformToGUI(e.getTransform());
        mf.needUpdate();
    }

    public void applyRotationRepeat(float rx, float ry, float rz) {
        applyRotation(rx, ry, rz);
    }

    public void applyRotation(float rx, float ry, float rz) {
        final Entity e = getBeadEntity();
        if (e == null || mf.selectedEntities.isEmpty()) {
            return;
        }

        final float angles = Parse.toFloat(angleText.getText());
        rx *= M.DEG_TO_RAD * angles;
        ry *= M.DEG_TO_RAD * angles;
        rz *= M.DEG_TO_RAD * angles;

        final Vector3f selectionCenter = Scene.calcCenter(mf.selectedEntities);

        e.rotateAround(selectionCenter, rx, ry, rz);

        mf.transformToGUI(e.getTransform());
        mf.needUpdate();
    }

    public void applyScale(final float stepScale) {
        final Entity e = getBeadEntity();
        if (e == null || mf.selectedEntities.isEmpty()) {
            return;
        }

        final float stepSize = stepScale * Parse.toFloat(speedText.getText());

        final Vector3f pos = e.getTransform().getTranslation();
        // sCenter.y = pos.y;
        pos.moveTowards(mf.scene.getSimulationCenter(), stepSize);

        mf.transformToGUI(e.getTransform());
        mf.needUpdate();
    }

    private void leftButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyDisplacement(-1, 0, 0);
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyDisplacement(0, 1, 0);
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyDisplacement(0, -1, 0);
    }

    private void rightButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyDisplacement(1, 0, 0);
    }

    private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyDisplacement(0, 0, -1);
    }

    private void backwardsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyDisplacement(0, 0, 1);
    }

    public float getDisplacementStep() {
        return Parse.toFloat(speedText.getText());
    }

    public float getRotationStep() {
        return Parse.toFloat(angleText.getText());
    }

    public void resetParticlePos() {
        final ArrayList<Entity> sel = mf.selectedEntities;
        final int n = M.min(sel.size(), snapBeadPositions.size());
        for (int i = 0; i < n; ++i) {
            sel.get(i).getTransform().getTranslation().set(snapBeadPositions.get(i));
        }

        applyDisplacement(0, 0, 0);
    }

    public void selectFirstBead() {
        Entity e = mf.scene.getFirstWithTag(Entity.TAG_CONTROL_POINT);
        if (e == null) {
            return;
        }
        mf.clearSelection();
        mf.selectedEntities.add(e);
        e.selected = true;
    }

    private void rYNButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyRotationRepeat(0, -1, 0);
    }

    private void rXPButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyRotationRepeat(1, 0, 0);
    }

    private void rXNButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyRotationRepeat(-1, 0, 0);
    }

    private void rYPButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyRotationRepeat(0, 1, 0);
    }

    private void rZPButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyRotationRepeat(0, 0, 1);
    }

    private void rZNButtonActionPerformed(java.awt.event.ActionEvent evt) {
        applyRotationRepeat(0, 0, -1);
    }

    protected void moveParticlesTowardsTarget(final ArrayList<MeshEntity> points, final HashMap<Entity, Vector3f> targetPositions, final float stepSize,
            final boolean useBFGSandAddToKeyFrames) {

        // while all the particles have not reached their destination
        boolean allParticlesReached = false;
        final Vector3f diffPos = new Vector3f();
        while (!allParticlesReached) {
            allParticlesReached = true;
            for (Entity e : points) {
                final Vector3f cPos = e.getTransform().getTranslation();
                final Vector3f tPos = targetPositions.get(e);
                if (tPos != null) {
                    diffPos.set(tPos).subtractLocal(cPos);
                    final float distance = diffPos.length();
                    if (distance > M.FLT_EPSILON) {
                        allParticlesReached = false;
                        diffPos.multLocal(M.min(distance, stepSize)).divideLocal(distance);
                        cPos.addLocal(diffPos);
                    }
                }
            }

        }
    }

    public JButton getUpButton() {
        return upButton;
    }

    public JButton getDownButton() {
        return downButton;
    }

    private javax.swing.JTextField angleText;
    private javax.swing.JButton backwardsButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton forwardButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton leftButton;
    private javax.swing.JButton rXNButton;
    private javax.swing.JButton rXPButton;
    private javax.swing.JButton rYNButton;
    private javax.swing.JButton rYPButton;
    private javax.swing.JButton rZNButton;
    private javax.swing.JButton rZPButton;
    private javax.swing.JButton rightButton;
    private javax.swing.JTextField speedText;
    private javax.swing.JButton upButton;
}
