package guiclient;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClientGUI {
	private static final String TITLE = "��Ƽä��";

	JFrame frame;

	// �޽��� ��� ����
	JTextArea msgout;
	JScrollPane msgScroll;

	// �޽��� �Է� ����
	JTextField msgInput;
	JButton sendBtn;
	JPanel msgPanel;
	
	// ��� ����� ���� ����
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	
	// Ŭ���̾�Ʈ ���α׷� ���� �� ���ʷ� �����ų �޼ҵ�
	public void onCreate() {
		setView(); //setContentView()�� ����
		setEvent(); //setOnclickListener()�� ����. ������ �޽��� ������ �̺�Ʈ ó��
		connectServer("127.0.0.1:1004", "�̼���"); //�Ű����� �߰�. ������ ���� �޽����� ó���� ������ ����
	}

	// UI�� �����Ѵ�.
	public void setView() {
		frame = new JFrame(TITLE);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		msgout = new JTextArea("��Ƽê�� ���Ű� ȯ���մϴ�\n", 10, 30);
		msgout.setLineWrap(true);
		msgout.setEditable(false);
		msgScroll = new JScrollPane(msgout,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		msgInput = new JTextField();
		sendBtn = new JButton("����");

		msgPanel = new JPanel(new BorderLayout());
		msgPanel.add(msgInput, BorderLayout.CENTER);
		msgPanel.add(sendBtn, BorderLayout.EAST);

		frame.add(msgScroll, BorderLayout.CENTER);
		frame.add(msgPanel, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		msgInput.requestFocus();
	}
	// �̺�Ʈ�� ����Ѵ�
	private void setEvent() {
		// inner Ȥ�� local class�� ����ϴ� ��� (���� �ܺο� ������ msgout�̳� msgInput ��ü�� ������ �� ���� ����)
		// �޼ҵ� ���� local class�� �����ϴ� ��İ� class ���� inner class�� �����ϴ� ����� ���̴�? (�⺻������ ���ٹ���)
		// local class�� ���δ� �޼ҵ��� local ������ �����Ϸ��� ������ final�� ����Ǿ�� ��
		// https://docs.oracle.com/javase/tutorial/java/javaOO/localclasses.html
		// http://stackoverflow.com/questions/20583056/inner-classes-defined-within-a-method-require-variables-declared-in-the-method-t

		//		int a = 10;
		//		System.out.println("setEventȣ���: a=" + a++);
		
		
		
		class MyActionListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// (1)TextField �Է�â���� ����Ű�� �����ų� (2) ���� ��ư�� Ŭ�������� ��
				// ��, if(e.getSource()==msgInput || e.getSource()==sendBtn){ }
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


		//�͸� Ŭ������ ����ϴ� ���
		//		msgInput.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				sendMsg();
		//			}
		//		});
	}
	private void sendMsg() {
//		msgout.append("Me:"+msgInput.getText()+"\n"); // �ּ�ó��
		out.println(msgInput.getText()); //  ��Ʈ��ũ�� ������
		msgInput.setText("");
		msgout.setCaretPosition(msgout.getDocument().getLength());
		msgInput.requestFocus();
	}

	// (1)������ �����ϰ�, (2)����� ������ �ϰ�, (3)����ó���� ���� ������ ����, (4)�α��� �г��� ������ ������
	public void connectServer(String server, String nickname){
		try {
			String str[] = server.split(":");
			Socket s = new Socket(str[0], Integer.parseInt(str[1]));
			System.out.println("���� ���� �Ϸ�");
			in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true); //auto flush
			new Thread() {
				public void run() {
					// �������� ���� �����͸� ȭ�鿡 ���
					try {
						String recvData = "";
						//������ ������ ���� ���� �� ���� ��� �޾Ƽ� ó�� 
						while( (recvData = in.readLine()) != null ) {
							msgout.append(recvData+"\n");
							msgout.setCaretPosition(msgout.getDocument().getLength());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
			
			// �����带 ������ ����� �����Ų ����, ���ʷ� ������ ���� �޽����� �α��� �޽�����
			out.println("login " + nickname);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ChatClientGUI client = new ChatClientGUI();
		client.onCreate(); //����
	}
}
// [����] ActionListener�� �뷫 �Ʒ��� ���� ������ �Ǿ� ������
class Componet{
	ActionListener listener;
	public void addActionlistener(ActionListener a){
		this.listener = a;
	}
	private void emitEvent(){
		listener.actionPerformed(null);
	}
}
