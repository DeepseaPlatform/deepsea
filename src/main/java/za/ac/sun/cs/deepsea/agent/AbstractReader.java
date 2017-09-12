package za.ac.sun.cs.deepsea.agent;

/**
 * An abstract reader that continuously reads.
 */
abstract public class AbstractReader {

	protected Thread readerThread;

	protected boolean isStopping = false;

	public AbstractReader() {
	}

	/**
	 * Continuously reads. Note that if the read involves waiting it can be
	 * interrupted and a InterruptedException will be thrown.
	 */
	abstract protected void readerLoop();

	/**
	 * Start the thread that reads events.
	 * 
	 */
	public void start() {
		readerThread = new Thread(new Runnable() {
			public void run() {
				readerLoop();
				isStopping = true;
			}
		});
		readerThread.setDaemon(true);
		readerThread.start();
	}

	/**
	 * Tells the reader loop that it should stop.
	 */
	public void stop() {
		isStopping = true;
		if (readerThread != null) {
			readerThread.interrupt();
		}
	}

	/**
	 * Check if the reader is stopping.
	 * 
	 * @return TODO
	 */
	public boolean isStopping() {
		return isStopping;
	}

}