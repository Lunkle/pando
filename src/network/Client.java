package network;

import java.util.ArrayList;
import java.util.Scanner;
import java.net.Socket;
import java.io.*;

public class Client {
	public static boolean loadMapLocally = false;
	public Socket socket;
	public Scanner in;
	public long seed;

	public Client(Socket s, Scanner sc) {
		//Fix later. Socket and in are being reassigned when connecting
		socket = s;
		in = sc;
	}

	static private PrintWriter out;

	public void connect() throws IOException {
		if (loadMapLocally)
			throw new IOException();

		socket = new Socket("192.168.1.25", 2438);
		in = new Scanner(socket.getInputStream());
		long sTime = System.currentTimeMillis();
		String inMessage;
		do {
			inMessage = in.nextLine();
		} while (inMessage == null && System.currentTimeMillis() - sTime < 5000);

		if (inMessage.equals("Connection Accepted")) {
			System.out.println("Connection Accepted!");

			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("mr");
			ArrayList<String> mapData = new ArrayList<>();
			boolean doneMap = false;
			while(!doneMap) {
				String nextLine = in.nextLine();
				if(nextLine.equals("done")) doneMap = true;
				else mapData.add(nextLine);
			}
			
			System.out.println(mapData.size());
			
			out.println("sd");
			seed = Long.parseLong((in.nextLine()));
		}

	}
}