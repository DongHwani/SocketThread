import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientApp{
	
	public static void main(String[] args) {
		
		String connectIP = "127.0.0.1";
		int connectPort = 7000;
		
		ClientSocketThread clientSocketThread = new ClientSocketThread(connectIP, connectPort);
		clientSocketThread.start();
		
		ClientSocketThread thread1 = new ClientSocketThread();
		thread1.start();
		
		try {
			
			Thread.sleep(100);
			
			System.out.println("[전송문자입력[종료시 Quit]] : ");
			
			while(true) {
				String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
				//Quit 입력 종료
				if(message.equals("Quit")) {
					break;
				}
			}
			
			clientSocketThread.join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n[ClientApp Main Thread] : SHUTDOWN.....");
		System.out.println("======================================");
	}

}
