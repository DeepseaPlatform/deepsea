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

	public StreamRedirector(InputStream in, OutputStream out, boolean produceOutput) {
		super();
		InputStream interruptibleInputStream = Channels.newInputStream(Channels.newChannel(in));
		this.in = new DataInputStream(interruptibleInputStream);
		this.out = produceOutput ? out : null;
		setPriority(Thread.NORM_PRIORITY); //  MAX_PRIORITY - 1);
		setDaemon(true);
	}

	public void terminate() {
		try {
			if (out != null) {
				out.flush();
			}
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
				if (out != null) {
					out.write(cbuf, 0, count);
				}
			}
			if (out != null) {
				out.flush();
			}
		} catch (ClosedByInterruptException x) {
			// ignore
		} catch (IOException x) {
			System.err.println("Child I/O Transfer - " + x);
		}
	}

}
