package io.codeforall.javatars;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bReader;
    private BufferedWriter bWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessages() {
        try {
            bWriter.write(username);
            bWriter.newLine();
            bWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bWriter.write(username + ": " + messageToSend);
                bWriter.newLine();
                bWriter.flush();
            }
        } catch (IOException e) {
            closeAll();
        }
    }

    public void messageListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String chatMessages;
                try {
                    while((chatMessages = bReader.readLine()) != null) {
                        System.out.println(chatMessages);
                    }
                } catch (IOException e) {
                    closeAll();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please choose a username: ");
        String username = scanner.nextLine();
        Socket socket1 = new Socket("localhost", 8040);
        Client client = new Client(socket1, username);
        client.messageListener();
        client.sendMessages();
    }

    public void closeAll() {
        try {
            if(!socket.isConnected()) {
                socket.close();
            }
            if(bReader != null) {
                bReader.close();
            }
            if(bWriter != null) {
                bWriter.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
