import java.io.*;
import java.net.*;

public class HTTPServer {

	private int port;
	private ServerSocket serverSocket;
	
	public static void main(String[] args){
		try {
			HTTPServer s = new HTTPServer();
		} catch (Exception e) {
			System.out.println("fout in main ");
		}
	}
	
	public HTTPServer() throws Exception{
		this.port = 3030;
		init();
	}
	
	public void init() throws Exception{
		openSocket();
		while(true){
			Socket clientSocket = null;
			try{
				clientSocket = this.serverSocket.accept();
				HttpRequest request = new HttpRequest(clientSocket);
				Thread thread = new Thread(request);
				thread.start();
			}catch(IOException e){
				System.out.println("IOException");
			}
		}
		
	}
	
	public void openSocket(){
		try {
			this.serverSocket = new ServerSocket(port);
			
		} catch (IOException e) {
			System.out.println("IO EXCEPTIOn");
		}
	}

	final class HttpRequest implements Runnable {
		
		final static String CRLF = "\r\n";
        Socket socket;
		
		public HttpRequest(Socket s){
			this.socket = s;
		}

		@Override
		public void run() {
			 try {
                 processRequest();
         } catch (Exception e) {
                 System.out.println(e);
         }

		}
		
		private void processRequest() throws Exception{
			InputStream is = socket.getInputStream();
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());	
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String requestLine = br.readLine();
			System.out.println();
			System.out.println(requestLine);
			
			  String headerLine = null;
			  String totalHeader = "";
		        while ((headerLine = br.readLine()).length() != 0) {
		                System.out.println(headerLine);
		                totalHeader += headerLine + "\n";
		                
		        }
		       os.writeBytes(makeResponse(totalHeader, requestLine));
		        os.close();
		        br.close();
		        socket.close();
		}
		
		private String makeResponse(String headerRest, String head){
			String response = "";
			response = "<HTML>" + 
                    "<HEAD><TITLE>Home</TITLE></HEAD>" +
                    "<BODY> Welkom op deze pagina </BODY></HTML>";
			return response;
		}
	}

}
