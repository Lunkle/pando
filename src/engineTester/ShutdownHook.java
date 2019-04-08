package engineTester;

public class ShutdownHook extends Thread {

	public ShutdownHook() {
		super(new Runnable() {
			@Override
			public void run() {
				System.out.println("shutting down or something, i dunno");
			}

		}, "ShutdownThread");
	}

}
