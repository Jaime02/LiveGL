package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import gui.MainForm;

public class TCPClient {
    private DataOutputStream out;
    private MainForm mainForm;

    public TCPClient(MainForm mainForm, int port, String host) {
        this.mainForm = mainForm;
        
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
                        String shader = in.readUTF();
                        if (shader.charAt(0) == 'v') {
                            mainForm.codePanel.vertexShaderEditor.setText(shader.substring(1));
                        } else {
                            mainForm.codePanel.fragmentShaderEditor.setText(shader.substring(1));
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
