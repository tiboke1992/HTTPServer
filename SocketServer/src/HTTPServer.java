import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;

public class HTTPServer {

	private int port;
	private ServerSocket serverSocket;
	

	public static void main(String[] args) {
		try {
			HTTPServer s = new HTTPServer();
		} catch (Exception e) {
			System.out.println("fout in main ");
		}
	}

	public HTTPServer() throws Exception {
		this.port = 3030;
		init();
	}

	public void init() throws Exception {
		openSocket();
		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
				HttpRequest request = new HttpRequest(clientSocket);
				Thread thread = new Thread(request);
				thread.start();
			} catch (IOException e) {
				System.out.println("IOException");
			}
		}

	}

	public void openSocket() {
		try {
			this.serverSocket = new ServerSocket(port);

		} catch (IOException e) {
			System.out.println("IO EXCEPTIOn");
		}
	}

	final class HttpRequest implements Runnable {

		final static String CRLF = "\r\n";
		Socket socket;
		private String version;
		String getorpost = "";

		public HttpRequest(Socket s) {
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

		private void processRequest() throws Exception {
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
			if(requestLine.toLowerCase().contains("post") || requestLine.toLowerCase().contains("put")){
				getorpost = br.readLine();
			}
			
			
			os.writeBytes(makeResponse(totalHeader, requestLine));
			os.close();
			br.close();
			socket.close();
		}

		private String makeResponse(String headerRest, String head) {
			version = getVersion(head);
			String command = getCommand(head);
			String code = "200 OK";
			Calendar c = new GregorianCalendar();
			System.out.println("We are using version ; " + version);
			String content = "<HTML>" + "<HEAD><TITLE>Home</TITLE></HEAD>"
					+ "<BODY> Welkom op deze pagina </BODY></HTML>";

			String response = "";
			response = version + " " + code + "\ndate: " + c.getTime() + "\n"
					+ "Content-Type:" + " text/html \n" + "Content-Length: "
					+ content.length() + "\n\n" + content;
			
			if(command.equals("PUT") || command.equals("POST")){
				save(headerRest);
			}
			
			return response;
		}

		private String getVersion(String str) {
			String[] ar = str.split(" ");
			String result = "HTTP/1.1";
			if (ar.length == 3) {
				result = "HTTP/1.1";
			}
			return result;
		}
		
		private String getCommand(String str){
			String[] ar = str.split(" ");
			return ar[0];
		}
		
		private void save(String input){
			Writer writer = null;

			try {
			    writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream("important.txt"), "utf-8"));
			    writer.write(getorpost);
			    System.out.println("Data saved");
			} catch (IOException ex) {
			  // report
			} finally {
			   try {writer.close();} catch (Exception ex) {}
			}
		}
	}

}
