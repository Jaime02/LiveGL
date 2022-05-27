package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import gui.MainForm;

public class TCPClient {
    private DataOutputStream out;

    public TCPClient(MainForm mainForm, int port, String host) {
        
        Socket socket;
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        DataInputStream in;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Thread receiveThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.startsWith("entity")) {
                            String[] parts = message.split(" ");
                            if (parts[0].equals("entityCreate")) {
                                mainForm.createEntitiesPanel.createEntityFromString(parts);
                            } else if (parts[0].equals("entityDelete")) {
                                mainForm.createEntitiesPanel.deleteEntityFromString(parts);
                            } else if (parts[0].equals("entityTransform")) {
                                mainForm.createEntitiesPanel.transformEntityFromString(parts);
                            } else {
                                System.out.println("Unknown message: " + message);
                            }

                        } else if (message.charAt(0) == 'v') {
                            mainForm.codePanel.vertexShaderEditor.setText(message.substring(1));
                        } else {
                            mainForm.codePanel.fragmentShaderEditor.setText(message.substring(1));
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        receiveThread.start();
    }

    public void send(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
