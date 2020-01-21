package data;

public class UpdaterThread extends Thread {

	private boolean doneThread = false;

	public void run() {
		while (!doneThread) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			System.out.println(doneThread);
//			System.out.println("updating");
		}
	}

	public void setDoneThread(boolean doneThread) {
		this.doneThread = doneThread;
	}

}
