package io.codeforall.javatars;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClienteHandler implements Runnable {

    public static ArrayList<ClienteHandler> clienteHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bReader;
    private BufferedWriter bWriter;

    private String userName;

    public ClienteHandler(Socket socket) {

        this.socket = socket;
        try {
            this.bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = bReader.readLine();
            clienteHandlers.add(this);
            broadcastMessage("Server: " + userName + " has entered the chatroom!");
        } catch (IOException e) {
            closeAll();
        }
    }


    @Override
    public void run() {
        String clientMessages;

        while (socket.isConnected()) {
            try {
                clientMessages = bReader.readLine();
                broadcastMessage(clientMessages);
            } catch (IOException e) {
                closeAll();
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for(ClienteHandler clienteHandler : clienteHandlers) {
            try {
                if(!clienteHandler.userName.equals(userName)) {
                    clienteHandler.bWriter.write(messageToSend);
                    clienteHandler.bWriter.newLine();
                    clienteHandler.bWriter.flush();
                }
            }catch (IOException e) {
                closeAll();
            }
        }
    }

    public void removeUser() {
        clienteHandlers.remove(this);
        broadcastMessage("Server: " + userName + " has left the chat");
    }

    public void closeAll() {
        removeUser();
        try {
            if(bWriter != null) {
                bWriter.close();
            }
            if(bReader != null) {
                bReader.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
