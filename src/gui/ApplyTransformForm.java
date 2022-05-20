package gui;

import javax.swing.GroupLayout;
import math.Transform;
import scene.Entity;


public class ApplyTransformForm extends javax.swing.JFrame {
    final MainForm mainForm;
    
    public ApplyTransformForm(MainForm mainForm) {
        this.mainForm = mainForm;
        initComponents();
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        area = new javax.swing.JTextArea();
        invertCheck = new javax.swing.JCheckBox();
        applyButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Apply Transform");

        area.setColumns(20);
        area.setRows(5);
        area.setText("0.0 0.0 0.0\n0.0 0.0 0.0 1.0\n1.0 1.0 1.0");
        jScrollPane1.setViewportView(area);

        invertCheck.setText("Invert");

        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(invertCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(applyButton))
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(invertCheck)
                    .addComponent(applyButton))
                .addContainerGap())
        );

        pack();
    }

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {
        final String[] l = area.getText().split("\\n");
        
        Transform t = new Transform();
        
        t.getTranslation().parse( l[0] );
        t.getRotation().parse( l[1] );
        t.getScale().parse( l[2] );
        
        if(invertCheck.isSelected()){
            t.invertLocal();
        }
        
        for(Entity e : mainForm.scene.getEntities()){
            if( (e.getTag() & (Entity.TAG_TRANSDUCER | Entity.TAG_CONTROL_POINT )) != 0){
                e.getTransform().combineWithParent(t);
            }
        }
        
        mainForm.needUpdate();
    }


    private javax.swing.JButton applyButton;
    private javax.swing.JTextArea area;
    private javax.swing.JCheckBox invertCheck;
    private javax.swing.JScrollPane jScrollPane1;

}
