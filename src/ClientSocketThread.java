import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class ClientSocketThread extends Thread {

	private BufferedReader fromServer;
	private PrintWriter toServer;
	private Socket socket;
	private int timeOut = 3000;
	private boolean loopFlag;
	
	public ClientSocketThread() {}
	
	public ClientSocketThread(String connectIP, int connectPort) {
		try {
			
			this.socket = new Socket();
			socket.setSoLinger(true, timeOut);
			
			SocketAddress socketAddress = new InetSocketAddress(connectIP, connectPort);
			socket.connect(socketAddress, timeOut);
			
			toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"), true);
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			
			loopFlag= true;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		System.out.println("[Client Thread] : ClientSocketThread.run() START");
		
		while(loopFlag) {
			
			try {
				String fromHostData = fromServer.readLine();
				if(fromHostData==null) {
					break;
				}
				
				System.out.println("::Server에서 수신 Data :"+fromHostData);
				System.out.print("[전송문자입력]");
			}catch(SocketTimeoutException stoe) {
				stoe.printStackTrace();
			}catch(Exception e) {
				e.printStackTrace();
				loopFlag = false;
			}
		}
		
		this.close();
		
		System.out.println("[Client Thread] : ClientSocketThread.run() END");
	}
	
	public void close() {
		
		System.out.println("::close() start");
		
		try {
			
			if(toServer != null) {
				toServer.close();
				toServer = null;
			}
			if(fromServer != null) {
				fromServer.close();
				fromServer = null;
			}
			
			if(socket != null) {
				socket.close();
				socket = null;
			}
			
			Thread.sleep(timeOut);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(":: close() end...");
	}
	
	public void toServerMessage(String message) {
		toServer.println(message);
	}
}
