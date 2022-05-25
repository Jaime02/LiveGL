package gui.panels;

import gui.MainForm;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionEvent;

public class ServerPanel extends JPanel {
    MainForm mainForm;
    JTextField portTextField;

    public ServerPanel(MainForm mainForm) {
        this.mainForm = mainForm;
        
        GroupLayout mainLayout = new GroupLayout(this);
        setLayout(mainLayout);

        JLabel portLabel = new JLabel("Port:");
        portTextField = new JTextField();

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUp();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainForm.networkConfigWindow.dispose();
            }
        });

        mainLayout.setHorizontalGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(portLabel)
            .addComponent(portTextField)
            .addGroup(mainLayout.createSequentialGroup()        
                    .addComponent(startButton)
                    .addComponent(cancelButton)));

        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
            .addComponent(portLabel)
            .addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(startButton)    
                .addComponent(cancelButton))
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    }

    public void setUp() {
        try {
            mainForm.setUpServer(Integer.parseInt(portTextField.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Port must be a integer number", "Error", JOptionPane.ERROR_MESSAGE);
        }
        mainForm.networkConfigWindow.setVisible(false);
        mainForm.codePanel.serverMode = true;
        mainForm.codePanel.onlineMode = true;
    }
}
