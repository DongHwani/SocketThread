import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class ChatServerApp{
	
	//Main method
	public static void main(String[] args) {
		
		System.out.println("=============");
		System.out.println("ChatServerApp Main Thread : Start Up");
		
		//ChatServerSocketThread ���� ����
		List<ChatServerSocketThread> list = new Vector<ChatServerSocketThread>(10,10);
		
		
		//Client ������ ���� ServerSocket, Socket
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		//Client ��� Thread
		ChatServerSocketThread chatServerSocketThread = null;
		
		//���� ���� �����
		boolean loopFlag = false;
		
		try {
			
			//ServerSocket ����
			serverSocket = new ServerSocket(7000);
			loopFlag = true;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		while(loopFlag) {
			try {
				
				System.out.println("[ChatServerApp Main Thread] : Client Connection Wait");
				
				socket = serverSocket.accept();
				
				System.out.println("\n\t\t\t Client "+socket.getRemoteSocketAddress()+"����");
				
				//Client�� ����ϴ� ServerSocketThread ����
				chatServerSocketThread = new ChatServerSocketThread(socket, list);
				list.add(chatServerSocketThread);
				
				System.out.println("\t\t\t\t[Host Main Thread] : ���� ������ �� "+list.size());
				
				//Thread Start
				chatServerSocketThread.start();
				
			}catch(IOException ioe) {
				ioe.printStackTrace();
				loopFlag = false;
			}
		}
		
		System.out.println("\t\t\t\t[ChatServerApp Main Thread] : Client Connection Wait END");
		System.out.println("\n\t\t\t\t ///////////////////////////////////////////////////////\n");
		
		
		synchronized (list) {
			for(ChatServerSocketThread thread : list) {
				thread.setLoopFlag(false);
			}
		}
		
		while(true) {
			if(list.size() != 0) {
				try {
					Thread.sleep(1000);					
				}catch(InterruptedException e) {}
			}else {
				break;			
			}
		}
		
		//SocketServer Close()
		try {
			if(serverSocket != null)
				serverSocket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n[ChatServerApp Main Thread] : SHUTDOWN....");
		System.out.println("=============================================");
	}
}
	