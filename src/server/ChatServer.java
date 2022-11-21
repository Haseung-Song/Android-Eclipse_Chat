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
            System.out.println("서버 구동 완료. 접속 대기중...");
            while(true){
                // 클라이언트 접속 대기
                Socket s = ss.accept();
                System.out.println("클라이언트 접속됨: " + s.getInetAddress().getHostAddress());
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