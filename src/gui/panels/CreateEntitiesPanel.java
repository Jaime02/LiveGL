package gui.panels;

import gui.MainForm;
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
        MeshEntity me = new MeshEntity(Resources.MESH_BOX);
        me.setTag(Entity.TAG_OBJ);
        me.setColor(CustomColor.WHITE);
        mainForm.addMeshEntityToSceneCenterAndResizeIt(me);

        mainForm.needUpdate();
    }
    
    private void addSphereButtonActionPerformed(ActionEvent evt) {
        MeshEntity me = new MeshEntity(Resources.MESH_SPHERE);
        me.setTag(Entity.TAG_OBJ);
        me.setColor(CustomColor.WHITE);
        mainForm.addMeshEntityToSceneCenterAndResizeIt(me);
        mainForm.needUpdate();
    }
    
    private void deleteButtonActionPerformed(ActionEvent evt) {
        // Remove from simulation and scene
        for (Entity e : mainForm.selectedEntities) {
            if (e.getTag() == Entity.TAG_OBJ) {
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
