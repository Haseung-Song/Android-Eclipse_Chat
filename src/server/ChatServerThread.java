package server;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ChatServerThread extends Thread {
    private Socket s;
    private BufferedReader in; // 이 클래스에는 readLine()메소드가 있기 때문
    private PrintWriter out;
    private String nickname; 
    public ChatServerThread(Socket s) {
        this.s = s;
    }
    // 로그인
    private void login(String nickname) {
        if( nickname.length() >=  2 ){
            this.nickname = nickname;
            broadcastMsg("시스템: "+ nickname + "님이 입장했습니다.");
        } else {
            sendMsg("시스템: 대화명은 두글자 이상 입력하세요.");
        }
    }
    // 로그아웃
    private void logout() throws Exception { // logout()을 호출한 사람이 예외처리해야함. 코드 간결화
        broadcastMsg("시스템: "+ nickname + "님이 대화방을 나갔습니다.");
        ChatServer.clients.remove(this);
        out.close();
        in.close();
        s.close();
    }
    // 담당 클라이언트에 메세지 전송
    private void sendMsg(String msg) {
        out.println(msg);
    }
    // 접속한 모든 클라이언트에 메세지 전송
    private void broadcastMsg(String msg) {
    	for(ChatServerThread ch: ChatServer.clients ){
            ch.sendMsg(msg);
        }
    }
    @Override
    public void run() {
        try {
        	// 소켓 객체로부터 입출력 스트림을 얻고, 문자단위 입출력을 위한 in, out 객체를 준비
            in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true); //auto flush
            // 입력 스트림에서 읽은 데이터를 출력 스트림으로
            String readData = "";
            while ((readData = in.readLine()) != null) { //클라이언트가 서버에게 보낸 메시지가 있는지 한줄씩 읽기
                if(readData.startsWith("login ")){ // 클라이언트가 login으로 시작하는 문자를 보냈다고 가정
                    login(readData.substring(6).trim());
                }else if(readData.equals("logout")){
                    logout();
                    break; // run()종료되면서 쓰레드 종료
                }else{
                    if(readData.trim().length()==0){
                        sendMsg("시스템: 메시지를 입력하세요.");
                    }
                    //받은 메시지를 (나를 포함한) 전체 클라이언트에게 보내자. 
                    broadcastMsg(nickname+": "+readData);
                }
            }
        } catch (SocketException se) {
            try {
                logout();
            } catch (Exception e) {}
            System.out.println("클라이언트 접속 종료 - " + s.getInetAddress().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}