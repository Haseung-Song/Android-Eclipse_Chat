package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatServer {
    static HashSet<ChatServerThread> clients = new HashSet<>();
    private ServerSocket ss;
    private void startServer(){
        try {
            ss = new ServerSocket(1004);
            System.out.println("���� ���� �Ϸ�. ���� �����...");
            while(true){
                // Ŭ���̾�Ʈ ���� ���
                Socket s = ss.accept();
                System.out.println("Ŭ���̾�Ʈ ���ӵ�: " + s.getInetAddress().getHostAddress());
                ChatServerThread client = new ChatServerThread(s);
                client.start();
                clients.add(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new ChatServer().startServer();
    }
}