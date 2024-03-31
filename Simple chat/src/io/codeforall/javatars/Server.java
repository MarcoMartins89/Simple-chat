package io.codeforall.javatars;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    void serverStart () {
        try {

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                ClienteHandler clientHandler = new ClienteHandler(socket);
                ExecutorService myThread = Executors.newCachedThreadPool();
                myThread.submit(clientHandler);
            }
        }   catch(IOException e){
            System.out.println("Error client connection not established");
            closeSocket();
            }
    }

    public void closeSocket() {

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8040);
        Server server = new Server(serverSocket);
        server.serverStart();
    }
}
