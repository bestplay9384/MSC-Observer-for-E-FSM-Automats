package pl.edu.pw.appt.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.swing.DefaultComboBoxModel;
import pl.edu.pw.appt.GUI;
import pl.edu.pw.appt.objects.Message;
import pl.edu.pw.appt.objects.Messages;

public class UdpServer extends Thread {

    private final static int port = 2222;
    byte[] receiveStuff;
    byte[] sendStuff;
    private boolean serverStopped = false;

    public Messages messages;
    int globalID = 0;
    MulticastSocket serverSocket;
    GUI gui;

    public UdpServer(GUI gui_) {
        this.gui = gui_;
        this.messages = new Messages(gui_);
    }

    @Override
    public void run() {
        startServer();
    }

    public void startServer() {
        serverSocket = null;
        String lineInput;
        String address;

        try {
            serverSocket = new MulticastSocket(port);
            serverSocket.joinGroup(InetAddress.getByName("239.0.0.222"));

            receiveStuff = new byte[1024];
            sendStuff = new byte[1024];

            while (!serverStopped) {
                DatagramPacket packet = new DatagramPacket(receiveStuff, receiveStuff.length);
                serverSocket.receive(packet);

                lineInput = new String(receiveStuff, 0, packet.getLength()).trim();
                address = packet.getAddress().toString().replace("/", "");

                System.out.println("UNIQ: " + address);
                System.out.println("Server Received ---- " + lineInput);

                String[] parts = lineInput.split("-");
                Message message = new Message(parts[0], null, null, parts[2], parts[3], parts[1], parts[4], String.valueOf(globalID++));

                messages.add(address, message);

                if (((DefaultComboBoxModel) gui.selectSystem.getModel()).getIndexOf(address) == -1) {
                    gui.selectSystem.addItem(address);
                }

                if (gui.selectSystem.getSelectedItem().equals(address)) {
                    gui.updateUml(messages.toUml(address, gui.startMsg.getText(), gui.stopMsg.getText()));
                }
            }
        } catch (IOException e) {
            System.exit(1);
        }

        serverSocket.close();
    }

    public void stopServer() {
        serverStopped = true;
        serverSocket = null;
    }

    public boolean isWorking() {
        return !this.serverStopped;
    }

    public int getPort() {
        return port;
    }
}
