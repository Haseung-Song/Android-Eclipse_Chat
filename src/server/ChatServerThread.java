package server;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ChatServerThread extends Thread {
    private Socket s;
    private BufferedReader in; // �� Ŭ�������� readLine()�޼ҵ尡 �ֱ� ����
    private PrintWriter out;
    private String nickname; 
    public ChatServerThread(Socket s) {
        this.s = s;
    }
    // �α���
    private void login(String nickname) {
        if( nickname.length() >=  2 ){
            this.nickname = nickname;
            broadcastMsg("�ý���: "+ nickname + "���� �����߽��ϴ�.");
        } else {
            sendMsg("�ý���: ��ȭ���� �α��� �̻� �Է��ϼ���.");
        }
    }
    // �α׾ƿ�
    private void logout() throws Exception { // logout()�� ȣ���� ����� ����ó���ؾ���. �ڵ� ����ȭ
        broadcastMsg("�ý���: "+ nickname + "���� ��ȭ���� �������ϴ�.");
        ChatServer.clients.remove(this);
        out.close();
        in.close();
        s.close();
    }
    // ��� Ŭ���̾�Ʈ�� �޼��� ����
    private void sendMsg(String msg) {
        out.println(msg);
    }
    // ������ ��� Ŭ���̾�Ʈ�� �޼��� ����
    private void broadcastMsg(String msg) {
    	for(ChatServerThread ch: ChatServer.clients ){
            ch.sendMsg(msg);
        }
    }
    @Override
    public void run() {
        try {
        	// ���� ��ü�κ��� ����� ��Ʈ���� ���, ���ڴ��� ������� ���� in, out ��ü�� �غ�
            in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true); //auto flush
            // �Է� ��Ʈ������ ���� �����͸� ��� ��Ʈ������
            String readData = "";
            while ((readData = in.readLine()) != null) { //Ŭ���̾�Ʈ�� �������� ���� �޽����� �ִ��� ���پ� �б�
                if(readData.startsWith("login ")){ // Ŭ���̾�Ʈ�� login���� �����ϴ� ���ڸ� ���´ٰ� ����
                    login(readData.substring(6).trim());
                }else if(readData.equals("logout")){
                    logout();
                    break; // run()����Ǹ鼭 ������ ����
                }else{
                    if(readData.trim().length()==0){
                        sendMsg("�ý���: �޽����� �Է��ϼ���.");
                    }
                    //���� �޽����� (���� ������) ��ü Ŭ���̾�Ʈ���� ������. 
                    broadcastMsg(nickname+": "+readData);
                }
            }
        } catch (SocketException se) {
            try {
                logout();
            } catch (Exception e) {}
            System.out.println("Ŭ���̾�Ʈ ���� ���� - " + s.getInetAddress().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}