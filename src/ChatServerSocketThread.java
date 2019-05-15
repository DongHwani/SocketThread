import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;

public class ChatServerSocketThread extends Thread{

	//Field
	private BufferedReader fromClient;
	private PrintWriter toClient;
	private Socket socket;
	private List<ChatServerSocketThread> list;
	//Thread 종료 제어용
	boolean loopFlag;
	//Client 식별용
	private SocketAddress socketAddress;
	//Client 대화명
	private String clientName;
	
	//Consturctor
	public ChatServerSocketThread(){}

	public ChatServerSocketThread(Socket socket, List<ChatServerSocketThread> list) {
		
		this.socket = socket;
		this.socketAddress = socket.getRemoteSocketAddress();
		this.list = list;
		
		try {
			//Client 통신을 위한 Stream 생성
			fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"), true);
			
			loopFlag = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
				
	}
	
	public void run() {
		
		System.out.println("\n[ChatServerSocketThread "+ socketAddress+"] : data를 수신, 송신 Loop Start");
		
		String fromClientData = null;
		
		while(loopFlag) {
			try {
				 if( (fromClientData = fromClient.readLine()) != null) {
					 
					System.out.println("\n[ChatServerSocketThread "+socketAddress+"] : Client 전송 받은 Data ==> "+fromClientData);
				   execute(fromClientData.substring(0,3), fromClientData.substring(4));
				 
				 }else {
					 System.out.println("\n[ChatServerSocketThread "+socketAddress+"] : Client 접속종료로 Thread 종료함");
					 break;
				 }
			
			}catch(SocketException se) {
				se.printStackTrace();
				loopFlag = false;
			}catch(Exception e) {
				e.printStackTrace();
				loopFlag = false;
			}
				
		
	     }
		
		System.out.println("\n[ChatServerSocketThread "+socketAddress+"]: data를 수신, 송신 Loop End");
		//Socket/ Stream close
		this.close();
	}
	
		public synchronized void toAllClient(String message) {
			for(ChatServerSocketThread chatServerSocketThread : list) {
				chatServerSocketThread.getToClient().println(message);
			}
		}
		
		public PrintWriter getToClient() {
			return toClient;
		}
		
		public void execute(String protocol, String message) {
		
			if(protocol.equals("100")) {
				this.clientName = message;
				
				if(this.hasName(message)) {
					System.out.println("["+message+"] 대화명 중복");
					toClient.println("["+message+"] 대화명 중복");
					loopFlag = false;
				}else {
					this.toAllClient("["+message+"]님 입장");
				}
			
			}else if(protocol.equals("200")) {
				this.toAllClient("["+clientName+"]: "+message);
			}else if(protocol.equals("400")) {
				this.toAllClient("["+clientName+"] 님 퇴실");
			}
		}
		
		//동일 대화명 확인
		public synchronized boolean hasName(String clientName) {
			
			for(ChatServerSocketThread chatServerSocketThread:list) {
				if(chatServerSocketThread != this && clientName.equals(chatServerSocketThread.getClientName())) {
					return true;
				}
			}
			return false;
		}
		
		//Socket /Stream Close()
		public void close() {
			System.out.println("::"+socketAddress+" close() start....");
		
			try {
				if(toClient != null) {
					toClient.close();
					toClient = null;
				}
				if(fromClient!=null) {
					fromClient.close();
					fromClient=null;
				}
				if(socket!=null) {
					socket.close();
					socket = null;
				}
				
				list.remove(this);
				
				System.out.println("접속자수 : "+list.size());
			}catch(IOException e) {
				System.out.println(e.toString());
			}
			System.out.println("::"+socketAddress+" close() end...\n");
		}
		
		//getter /setter Method
		public void setLoopFlag(boolean loopFlag) {
			this.loopFlag = loopFlag;
		}
		
		public String getClientName() {
			return clientName;
		}
}