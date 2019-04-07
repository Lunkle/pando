package network;

import java.util.Scanner;
import java.net.Socket;
import java.io.*;
import java.util.concurrent.TimeUnit;


public class Client {
	public static boolean loadMapLocally = false;
	
	public Client(){}
	
    static private PrintWriter out;
    public void connect() throws IOException {
    	if(loadMapLocally) throw new IOException();
    	
        Socket socket = new Socket("127.0.0.1", 2438);
        Scanner in = new Scanner(socket.getInputStream());
        long sTime = System.currentTimeMillis();
        String inMessage;
        do {
        	inMessage = in.nextLine();
        } while(inMessage == null && System.currentTimeMillis() - sTime < 5000);
        
        if(inMessage.equals("ConnectionAccepted"))
        	System.out.println("Connection Accepted!");
        socket.close();
        in.close();
    }
}