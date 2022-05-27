package gui.panels;

import gui.MainForm;
import math.M;
import math.Quaternion;
import math.Vector3f;
import scene.Entity;
import scene.MeshEntity;
import scene.Resources;
import utils.CustomColor;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;


public class CreateEntitiesPanel extends JPanel {
    public MainForm mainForm;
    private JTextField colorTextInput;

    public CreateEntitiesPanel(MainForm mainForm) {
        this.mainForm = mainForm;

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("Add objects"));

        JButton addCubeButton = new JButton("Cube");
        addCubeButton.setToolTipText("Create a cube");
        addCubeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addCubeButtonActionPerformed(evt);
            }
        });

        JButton addSphereButton = new JButton("Sphere");
        addSphereButton.setToolTipText("Create a sphere");
        addSphereButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addSphereButtonActionPerformed(evt);
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.setToolTipText("Deletes the selected object");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            } 
        });

        JLabel colorTextLabel = new JLabel("Color: (R, G, B, A)");

        colorTextInput = new javax.swing.JTextField("255, 255, 255, 255");
        colorTextInput.setToolTipText("Color RGBA");

        JButton applyColorButton = new JButton("Apply color to selected entity");
        applyColorButton.setToolTipText("Applies the RGBA color to the selected entity");
        applyColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                applyColorButtonActionPerformed(evt);
            }
        });

        GroupLayout mainLayout = new GroupLayout(this);
        mainLayout.setAutoCreateContainerGaps(true);
        this.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainLayout.createSequentialGroup()
                .addComponent(addCubeButton)
                .addComponent(addSphereButton)
                .addComponent(deleteButton))
            .addComponent(colorTextInput)
            .addComponent(colorTextLabel)
            .addComponent(applyColorButton));

        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
            .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(addCubeButton)
                .addComponent(addSphereButton)
                .addComponent(deleteButton))
            .addComponent(colorTextLabel)
            .addComponent(colorTextInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(applyColorButton));
    }

    private void addCubeButtonActionPerformed(ActionEvent evt) {
        int id = mainForm.generateId();
        MeshEntity me = new MeshEntity(id, Resources.MESH_BOX);
        me.setTag(Entity.TAG_OBJ);
        me.setColor(CustomColor.WHITE);
        mainForm.addMeshEntityToSceneCenterAndResizeIt(id, me);

        mainForm.needUpdate();
    }

    public void createEntityFromString(String[] args) {
        int id = Integer.valueOf(args[1]);
        String mesh = args[2];
        float tx = Float.valueOf(args[3]);
        float ty = Float.valueOf(args[4]);
        float tz = Float.valueOf(args[5]);
        float sx = Float.valueOf(args[6]);
        float sy = Float.valueOf(args[7]);
        float sz = Float.valueOf(args[8]);
        MeshEntity me = new MeshEntity(id, mesh);
        me.setTag(Entity.TAG_OBJ);
        me.setColor(CustomColor.WHITE);
        me.getTransform().getTranslation().set(tx, ty, tz);
        // TODO Fix quaternion rotation
        // me.getTransform().getRotation().set(rx, ry, rz, 0);
        me.getTransform().getScale().set(sx, sy, sz);
        mainForm.needUpdate();
        mainForm.scene.getEntities().add(me);
    };

    public void deleteEntityFromString(String[] args) {
        int id = Integer.valueOf(args[1]);
        for (MeshEntity e : mainForm.scene.getEntities()) {
            if (e.getTag() == Entity.TAG_OBJ && e.id == id) {
                mainForm.scene.getEntities().remove(e);
                mainForm.needUpdate();
                return;
            }
        }
        System.out.println("Entity with id " + id + " not found");
    };
    
    public void transformEntityFromString(String[] args) {
        int id = Integer.valueOf(args[1]);
        Float tx = Float.valueOf(args[2]);
        Float ty = Float.valueOf(args[3]);
        Float tz = Float.valueOf(args[4]);
        Float rx = Float.valueOf(args[5]);
        Float ry = Float.valueOf(args[6]);
        Float rz = Float.valueOf(args[7]);
        Float sx = Float.valueOf(args[8]);
        Float sy = Float.valueOf(args[9]);
        Float sz = Float.valueOf(args[10]);
        
        for (MeshEntity e : mainForm.scene.getEntities()) {
            if (e.getTag() == Entity.TAG_OBJ && e.id == id) {
                e.getTransform().getTranslation().set(tx, ty, tz);
                // TODO Fix quaternion rotation
                e.getTransform().getRotation().set(rx, ry, rz, 0);

                System.out.println("Rotation: " + rx + " " + ry + " " + rz);

                Quaternion q = e.getTransform().getRotation();
                Vector3f v = q.toAngles(new Vector3f(rx, ry, rz));
                e.getTransform().getRotation().set(q.fromAngles(v));

                e.getTransform().getScale().set(sx, sy, sz);
                mainForm.needUpdate();
                return;
            }
        }
        System.out.println("Entity not found!");
    }


    private void addSphereButtonActionPerformed(ActionEvent evt) {
        int id = mainForm.generateId();
        MeshEntity me = new MeshEntity(id, Resources.MESH_SPHERE);
        me.setTag(Entity.TAG_OBJ);
        me.setColor(CustomColor.WHITE);
        mainForm.addMeshEntityToSceneCenterAndResizeIt(id, me);
        mainForm.needUpdate();
    }
    
    private void deleteButtonActionPerformed(ActionEvent evt) {
        // Remove from simulation and scene
        for (MeshEntity e : mainForm.selectedEntities) {
            if (e.getTag() == Entity.TAG_OBJ) {
                mainForm.codePanel.sendToClients("entityDelete " + e.id);
                mainForm.scene.getEntities().remove(e);
            }
        }

        mainForm.clearSelection();
        mainForm.needUpdate();
    }

    private void applyColorButtonActionPerformed(ActionEvent evt) {
        int color = CustomColor.parse(colorTextInput.getText());
        
        for (Entity e : mainForm.selectedEntities) {
            if (e instanceof MeshEntity) {
                MeshEntity me = (MeshEntity)e;
                me.setColor(color);
            }
        }

        mainForm.needUpdate();
    }
}
