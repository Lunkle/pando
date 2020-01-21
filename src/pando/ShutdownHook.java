package pando;

import java.net.Socket;
import java.util.Scanner;

public class ShutdownHook extends Thread {
	public static Socket socket;
	public static Scanner socketStream;

	public ShutdownHook() {
		super(new Runnable() {
			@Override
			public void run() {
				System.out.println("shutting down or something, i dunno");
				try {
//					socket.close();
//					socketStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, "ShutdownThread");
	}

}
