package utils;

import java.util.LinkedList;
import gui.MainForm;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer extends Thread {
    public int port;
    public LinkedList<ClientThread> clients = new LinkedList<>();
    private MainForm mainForm;

    public TCPServer(MainForm mainForm, int port) {
        this.mainForm = mainForm;
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            // Repeatedly wait for connections
            while (!interrupted()) {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clients, clientSocket);
                clientThread.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class ClientThread extends Thread {
        final LinkedList<ClientThread> clients;
        final Socket socket;

        private DataOutputStream out;

        public ClientThread(LinkedList<ClientThread> clients, Socket socket) {
            this.clients = clients;
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("Connection from " + socket.getInetAddress() + ":" + socket.getPort());

                DataInputStream in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                synchronized (clients) { 
                    clients.add(this);
                }

                while (true) {
                    String shader = in.readUTF();
                    if (shader.charAt(0) == 'v') {
                        mainForm.codePanel.vertexShaderEditor.setText(shader.substring(1));
                    } else {
                        mainForm.codePanel.fragmentShaderEditor.setText(shader.substring(1));
                    }
                    
                    for (ClientThread client : clients) {
                        if (client != this) {
                            client.send(shader);
                        }
                    }
                }

            } catch (SocketException sockEx) {
                // Client closed the connection
            
            } catch (Exception ex) {
                ex.printStackTrace();

            } finally {
                // we have finished or failed so let's close the socket and remove ourselves
                // from the list
                try {
                    socket.close();
                } catch (Exception ex) {
                    // this will make sure that the socket closes
                }

                synchronized (clients) {
                    clients.remove(this);
                }
            }
        }

        public void send(String message) {
            try {
                out.writeUTF(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}