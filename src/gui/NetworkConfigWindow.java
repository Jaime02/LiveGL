package gui;

import gui.panels.ClientPanel;
import gui.panels.ServerPanel;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

public class NetworkConfigWindow extends JDialog {
    public NetworkConfigWindow(MainForm mainForm) {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        setModal(true);
        setTitle("Network Configuration");

        JTabbedPane mainPanel = new JTabbedPane();
        getContentPane().add(mainPanel);

        ClientPanel clientPanel = new ClientPanel(mainForm);
        mainPanel.addTab("Client", clientPanel);

        ServerPanel serverPanel = new ServerPanel(mainForm);
        mainPanel.addTab("Server", serverPanel);

        pack();
    }
}
