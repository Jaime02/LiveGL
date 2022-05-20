package utils;

import java.awt.Component;
import javax.swing.GroupLayout;

public class TextFrame extends javax.swing.JFrame {

    public static void showText(String title, String text, Component father) {
        TextFrame tf = new TextFrame(title, text);
        tf.setLocationRelativeTo(father);
        tf.setVisible(true);
    }

    public TextFrame(String title, String text) {
        super(title);
        initComponents();
        area.setText(text);
    }

    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        area = new javax.swing.JTextArea();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("title");

        area.setColumns(20);
        area.setRows(5);
        jScrollPane1.setViewportView(area);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addGap(10, 10, 10)
                        .addComponent(closeButton, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE).addContainerGap())
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(closeButton).addContainerGap()));

        pack();
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private javax.swing.JTextArea area;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane jScrollPane1;
}
