package utils;

import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout;

public class BufferedImageView extends javax.swing.JFrame {

    public static void showImage(String title, BufferedImage image, Component centeredOn){
        BufferedImageView biv = new BufferedImageView(title, image);
        biv.setLocationRelativeTo(centeredOn);
        biv.setVisible(true);
    }
    
    public BufferedImageView(String title, BufferedImage image) {
        super(title);
        initComponents();
        label.setIcon( new ImageIcon(image));
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Image");

        jScrollPane1.setViewportView(label);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }

    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label;
}
