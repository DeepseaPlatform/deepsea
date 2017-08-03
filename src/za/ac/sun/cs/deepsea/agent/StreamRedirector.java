package za.ac.sun.cs.deepsea.agent;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ClosedByInterruptException;

public class StreamRedirector extends Thread {

	private final DataInputStream in;

	private final OutputStream out;

	private static final int BUFFER_SIZE = 2048;

	/**
	 * Set up for copy.
	 * 
	 * @param name
	 *            Name of the thread
	 * @param in
	 *            Stream to copy from
	 * @param out
	 *            Stream to copy to
	 */
	public StreamRedirector(InputStream in, OutputStream out) {
		super();
		InputStream interruptibleInputStream = Channels.newInputStream(Channels.newChannel(in));
		this.in = new DataInputStream(interruptibleInputStream);
		this.out = out;
		setPriority(Thread.NORM_PRIORITY); //  MAX_PRIORITY - 1);
		setDaemon(true);
	}

	public void terminate() {
		try {
			out.flush();
			join();
		} catch (IOException x) {
			// ignore
		} catch (InterruptedException x) {
			// TODO Auto-generated catch block
			x.printStackTrace();
		}
	}

	/**
	 * Copy.
	 */
	@Override
	public void run() {
		try {
			byte[] cbuf = new byte[BUFFER_SIZE];
			int count;
			while ((count = in.read(cbuf, 0, BUFFER_SIZE)) >= 0) {
				out.write(cbuf, 0, count);
			}
			out.flush();
		} catch (ClosedByInterruptException x) {
			// ignore
		} catch (IOException x) {
			System.err.println("Child I/O Transfer - " + x);
		}
	}

}
