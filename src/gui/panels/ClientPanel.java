package gui.panels;

import gui.MainForm;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientPanel extends JPanel {
    GroupLayout mainLayout;
    MainForm mainForm;
    JTextField portTextField;
    JTextField hostAddressTextField;

    public ClientPanel(MainForm mainForm) {
        this.mainForm = mainForm;

        mainLayout = new GroupLayout(this);
        setLayout(mainLayout);

        JLabel portLabel = new JLabel("Port:");
        portTextField = new JTextField();

        JLabel hostAddressLabel = new JLabel("Host Address:");
        hostAddressTextField = new JTextField();

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
            .addComponent(hostAddressLabel)
            .addComponent(hostAddressTextField)
            .addGroup(mainLayout.createSequentialGroup()        
                    .addComponent(startButton)
                    .addComponent(cancelButton)));

        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
            .addComponent(portLabel)
            .addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(hostAddressLabel)
            .addComponent(hostAddressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(startButton)    
                .addComponent(cancelButton))
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    }

    public void setUp() {
        try {
            mainForm.setUpClient(Integer.parseInt(portTextField.getText()), hostAddressTextField.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        mainForm.networkConfigWindow.setVisible(false);
        mainForm.codePanel.onlineMode = true;
    }
}
