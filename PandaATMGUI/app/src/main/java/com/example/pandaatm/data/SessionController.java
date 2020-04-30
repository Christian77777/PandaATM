package com.example.pandaatm.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SessionController {

    private static final String url = "192.168.1.100";
    private static final int port = 6924;

    private ObjectInputStream dataInput;
    private ObjectOutputStream dataOutput;

    private static SessionController c;

    public static SessionController getInstance() throws IOException {
        if(c == null) {
            c = new SessionController();
        }
        return c;
    }

    //Throws Exception, must be handled with GUi to show alert that Internet is not working (server is down)
    private SessionController() throws IOException {
        Socket serverConnection = new Socket(url, port);
        System.out.println("Sending Message1");
        dataInput = new ObjectInputStream(serverConnection.getInputStream());
        System.out.println("Sending Message2");
        dataOutput = new ObjectOutputStream(serverConnection.getOutputStream());
        System.out.println("Sending Message3");
    }

    public void sendMessage(Message m) throws IOException {
        dataOutput.writeObject(m);
    }

    public Message readMessage() throws IOException {
        try {
            return (Message) dataInput.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Message Object received does not match Client's Message");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
