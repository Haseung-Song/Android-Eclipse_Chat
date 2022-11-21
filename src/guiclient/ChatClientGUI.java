package guiclient;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClientGUI {
	private static final String TITLE = "멀티채팅";

	JFrame frame;

	// 메시지 출력 영역
	JTextArea msgout;
	JScrollPane msgScroll;

	// 메시지 입력 영역
	JTextField msgInput;
	JButton sendBtn;
	JPanel msgPanel;
	
	// 통신 기능을 위한 변수
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	
	// 클라이언트 프로그램 구동 시 최초로 실행시킬 메소드
	public void onCreate() {
		setView(); //setContentView()에 대응
		setEvent(); //setOnclickListener()에 대응. 서버로 메시지 보내는 이벤트 처리
		connectServer("127.0.0.1:1004", "이순신"); //매개변수 추가. 서버가 보낸 메시지를 처리할 쓰레드 생성
	}

	// UI를 구성한다.
	public void setView() {
		frame = new JFrame(TITLE);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		msgout = new JTextArea("멀티챗에 오신걸 환영합니다\n", 10, 30);
		msgout.setLineWrap(true);
		msgout.setEditable(false);
		msgScroll = new JScrollPane(msgout,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		msgInput = new JTextField();
		sendBtn = new JButton("전송");

		msgPanel = new JPanel(new BorderLayout());
		msgPanel.add(msgInput, BorderLayout.CENTER);
		msgPanel.add(sendBtn, BorderLayout.EAST);

		frame.add(msgScroll, BorderLayout.CENTER);
		frame.add(msgPanel, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		msgInput.requestFocus();
	}
	// 이벤트를 등록한다
	private void setEvent() {
		// inner 혹은 local class를 사용하는 방식 (만약 외부에 있으면 msgout이나 msgInput 객체를 참조할 수 없기 때문)
		// 메소드 내에 local class로 선언하는 방식과 class 내에 inner class로 선언하는 방식의 차이는? (기본적으로 접근범위)
		// local class가 감싸는 메소드의 local 변수를 참조하려면 변수가 final로 선언되어야 함
		// https://docs.oracle.com/javase/tutorial/java/javaOO/localclasses.html
		// http://stackoverflow.com/questions/20583056/inner-classes-defined-within-a-method-require-variables-declared-in-the-method-t

		//		int a = 10;
		//		System.out.println("setEvent호출됨: a=" + a++);
		
		
		
		class MyActionListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// (1)TextField 입력창에서 엔터키를 눌렀거나 (2) 전송 버튼을 클릭했을때 임
				// 즉, if(e.getSource()==msgInput || e.getSource()==sendBtn){ }
				//				System.out.println("actionPerformed: a="+a);
				sendMsg();
			}
		}
		
		msgInput.addActionListener(new MyActionListener());
		sendBtn.addActionListener(new MyActionListener());

		//		ActionListener a = new ActionListener(){
		//			public void actionPerformed(ActionEvent e) {
		//				sendMsg();
		//			}
		//		};
		//		msgInput.addActionListener(a);
		//		sendBtn.addActionListener(a);


		//익명 클래스를 사용하는 방식
		//		msgInput.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				sendMsg();
		//			}
		//		});
	}
	private void sendMsg() {
//		msgout.append("Me:"+msgInput.getText()+"\n"); // 주석처리
		out.println(msgInput.getText()); //  네트워크로 보내기
		msgInput.setText("");
		msgout.setCaretPosition(msgout.getDocument().getLength());
		msgInput.requestFocus();
	}

	// (1)서버와 접속하고, (2)입출력 설정을 하고, (3)수신처리를 위한 쓰레드 실행, (4)로그인 닉네임 서버로 보내기
	public void connectServer(String server, String nickname){
		try {
			String str[] = server.split(":");
			Socket s = new Socket(str[0], Integer.parseInt(str[1]));
			System.out.println("서버 접속 완료");
			in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true); //auto flush
			new Thread() {
				public void run() {
					// 서버에서 받은 데이터를 화면에 출력
					try {
						String recvData = "";
						//서버가 나에게 보낸 문자 한 줄을 계속 받아서 처리 
						while( (recvData = in.readLine()) != null ) {
							msgout.append(recvData+"\n");
							msgout.setCaretPosition(msgout.getDocument().getLength());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
			
			// 쓰레드를 별도로 만들어 실행시킨 이후, 최초로 서버로 보낼 메시지는 로그인 메시지임
			out.println("login " + nickname);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ChatClientGUI client = new ChatClientGUI();
		client.onCreate(); //시작
	}
}
// [참고] ActionListener는 대략 아래와 같은 구조로 되어 있을것
class Componet{
	ActionListener listener;
	public void addActionlistener(ActionListener a){
		this.listener = a;
	}
	private void emitEvent(){
		listener.actionPerformed(null);
	}
}
