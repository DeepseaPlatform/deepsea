package za.ac.sun.cs.deepsea.distributed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.MultiPartInputStreamParser;
import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Log;

import redis.clients.jedis.Jedis;
import za.ac.sun.cs.deepsea.BuildConfig;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.reporting.Banner;

/**
 * Master controller for the DEEPSEA project distributed version. It expects a single
 * command-line argument: the filename of a properties file. It creates a
 * logger, and an instance of {@link Configuration}, and uses these to create an
 * instance of {@link Diver}. It reads the settings from the properties file,
 * and runs the diver.
 */
public class Master {
	
	private static final String REDIS_HOST = "redis";
	// private static final String REDIS_HOST = "127.0.0.1";
	
	private static final int WEB_PORT = 8080;
	
	private static final String FAVICON = "favicon.ico";

	private static final String WORKDIR = "/work";
	// private static final String WORKDIR = "/tmp/work";

	public static final String LOGFILE = "/logging/deepsea.log";
	// public static final String LOGFILE = "/tmp/logging/deepsea.log";
	
	private static Logger LOGGER;

	private static DeapseaRunner runner = null;

	private static Jedis jedis;

	/**
	 * The main function.
	 * 
	 * @param args
	 *            command-line arguments.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		LOGGER = LogManager.getLogger(jvmName);
		new Banner('#').println("DEEPSEA version " + BuildConfig.VERSION + " DISTRIBUTED MASTER").display(LOGGER, Level.INFO);
		jedis = new Jedis(REDIS_HOST);
		Log.setLog(new JettyLogger());
		Server server = new Server(WEB_PORT);
        server.setHandler(new RootHandler());
        server.start();
        server.join();
	}

	private static class JettyLogger extends AbstractLogger {

		private final String name;

		private boolean debugEnabled = false;

		public JettyLogger() {
			name = "JettyToLog4j2";
		}

		private JettyLogger(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public void warn(String msg, Object... args) {
			LOGGER.warn(msg, args);
		}

		@Override
		public void warn(Throwable thrown) {
			LOGGER.warn(thrown);
		}

		@Override
		public void warn(String msg, Throwable thrown) {
			LOGGER.warn(msg, thrown);
		}

		@Override
		public void info(String msg, Object... args) {
			LOGGER.info(msg, args);
		}

		@Override
		public void info(Throwable thrown) {
			LOGGER.info(thrown);
		}

		@Override
		public void info(String msg, Throwable thrown) {
			LOGGER.info(msg, thrown);
		}

		@Override
		public boolean isDebugEnabled() {
			return debugEnabled;
		}

		@Override
		public void setDebugEnabled(boolean enabled) {
			debugEnabled = enabled;
		}

		@Override
		public void debug(String msg, Object... args) {
			LOGGER.debug(msg, args);
		}

		@Override
		public void debug(Throwable thrown) {
			LOGGER.debug(thrown);
		}

		@Override
		public void debug(String msg, Throwable thrown) {
			LOGGER.debug(msg, thrown);
		}

		@Override
		public void ignore(Throwable ignored) {
			// do nothing
		}

		@Override
		protected org.eclipse.jetty.util.log.Logger newLogger(String fullname) {
			return new JettyLogger(fullname);
		}
		
	}

	private static class RootHandler extends AbstractHandler {

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			String method = request.getMethod();
//			LOGGER.info("method: {}", method);
			if (method.equals("GET")) {
				String uri = request.getRequestURI();
				LOGGER.info("get uri: ({})", uri);
				if (uri.equals("index.html") || uri.equals("/")) {
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType("text/html; charset=utf-8");
					if (runner == null) {
						MasterHtml.outputMainPage(response.getWriter());
					} else {
						MasterHtml.outputRunningPage(response.getWriter(), runner.getPropertiesFile());
					}
					baseRequest.setHandled(true);
				} else if (uri.equals("/favicon.ico")) {
					File f = new File(Master.class.getClassLoader().getResource(FAVICON).getFile());
					if (!f.exists()) {
						f = new File("/" + FAVICON);
					}
					try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
						int size = (int) raf.length();
						byte[] data = new byte[size];
						raf.readFully(data);
						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType("image/vnd.microsoft.icon");
						response.setContentLength(size);
						response.getOutputStream().write(data);
						baseRequest.setHandled(true);
					}
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setContentType("text/html; charset=utf-8");
					MasterHtml.outputBadRequest(response.getWriter());
					baseRequest.setHandled(true);
				}
			} else {
				String uri = request.getRequestURI();
				LOGGER.info("post uri: ({})", uri);
				if (uri.equals("/run") && (runner == null)) {
					InputStream is = request.getInputStream();
				    String ct = request.getContentType();
					MultipartConfigElement mpce = new MultipartConfigElement("/tmp");
					File ctd = new File("/tmp");
					MultiPartInputStreamParser mpisp = new MultiPartInputStreamParser(is, ct, mpce, ctd);
					runner = new DeapseaRunner();
					for (Part part : mpisp.getParts()) {
						String fieldname = part.getName();
						String filename = part.getSubmittedFileName();
						if ((filename == null) || (filename.trim().isEmpty())) {
							continue;
						}
						LOGGER.info("writing {}", filename);
						InputStream in = part.getInputStream();
						byte[] buffer = new byte[in.available()];
					    in.read(buffer);
					    File f = new File(WORKDIR + "/" + filename);
					    try (OutputStream out = new FileOutputStream(f)) {
					    		out.write(buffer);
					    }
					    if (fieldname.equals("propfile")) {
					    		runner.setPropertiesFile(filename);
					    } else {
					    		runner.addFile(filename);
					    }
					}
					LOGGER.debug("Starting deepsea thread");
					runner.start();
					LOGGER.debug("Started deepsea thread");
					response.setStatus(303);
					response.setHeader("Location", "/");
					baseRequest.setHandled(true);
				} else if (uri.equals("/quit") && (runner != null)) {
					runner.shutdown();
					try {
						runner.join();
					} catch (InterruptedException ignore) {
						// ignore this exception
					}
					runner = null;
					response.setStatus(303);
					response.setHeader("Location", "/");
					baseRequest.setHandled(true);
				} else if (uri.equals("/log") && (runner != null)) {
					String rangeHeader = request.getHeader("Range");
					Pattern pattern = Pattern.compile("^bytes=(\\d*)-(\\d*)");
					Matcher matcher = pattern.matcher(rangeHeader);
					if (matcher.matches()) {
						// Read the file
						File f = new File(LOGFILE);
						InputStream in = new FileInputStream(f);
						int totalLength = in.available();
						byte[] buffer = new byte[totalLength];
						in.read(buffer);
						in.close();
						// Parse the range
						String firstString = matcher.group(1);
						String secondString = matcher.group(2);
						int first = Math.max(0, firstString.trim().isEmpty() ? 0 : Integer.parseInt(firstString));
						int second = Math.max(first, Math.min(totalLength, secondString.trim().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(secondString)));
						int length = second - first;
						LOGGER.debug("Log request: rangeHeader={} totalLength={} first={} second={} length={}", rangeHeader, totalLength, first, second, length);
						if (length > 0) {
							response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
							response.setHeader("Content-Range", "bytes " + first + "-" + (second-1) + "/" + totalLength);
							response.setContentLength(length);
							response.getOutputStream().write(buffer, first, length);
							baseRequest.setHandled(true);
						} else {
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							response.setContentType("text/html; charset=utf-8");
							MasterHtml.outputBadRequest(response.getWriter());
							baseRequest.setHandled(true);
						}
					} else {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.setContentType("text/html; charset=utf-8");
						MasterHtml.outputBadRequest(response.getWriter());
						baseRequest.setHandled(true);
					}
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setContentType("text/html; charset=utf-8");
					MasterHtml.outputBadRequest(response.getWriter());
					baseRequest.setHandled(true);
				}
			}
		}

	}

	private static class DeapseaRunner extends Thread {

		/**
		 * Signatures for those paths we have visited.
		 */
//		private final Set<String> visitedSignatures = new HashSet<>();

		/**
		 * Name of the properties file.
		 */
		private String propertiesFile;

		private final Set<String> supportFiles = new HashSet<>();

		private boolean isRunning;

		/**
		 * @param propertiesFile
		 */
		public void setPropertiesFile(String propertiesFile) {
			this.propertiesFile = propertiesFile;
		}
	
		public String getPropertiesFile() {
			return propertiesFile;
		}
	
		public void addFile(String filename) {
			supportFiles.add(filename);
		}
	
		@Override
		public void run() {
			isRunning = true; // do this as early as possible
			cleanJedisQueues();
			String propFile = WORKDIR + "/" + propertiesFile;
			Configuration config = new Configuration();
			if (!config.processProperties(propFile)) {
				new Banner('@').println("DEEPSEA PROBLEM\n").println("COULD NOT READ PROPERTY FILE \"" + propFile + "\"").display(LOGGER, Level.FATAL);
				return;
			}
			if (config.getTarget() == null) {
				new Banner('@').println("SUSPICIOUS PROPERTIES FILE\n").println("ARE YOU SURE THAT THE ARGUMENT IS A .properties FILE?").display(LOGGER, Level.FATAL);
				return;
			}
			new Banner('O').println("DEEPSEA version " + BuildConfig.VERSION + " DISTRIBUTED MASTER").display(LOGGER, Level.INFO);
			LOGGER.info("");
			jedis.set("PROPERTIES", propertiesFile);
			LOGGER.debug("set the propertiesFile: {}", propertiesFile);
			try {
				jedis.lpush("TASKS", TaskResult.EMPTY.intoString());
				LOGGER.debug("sent the first task");
				int nrOfIncompleteTasks = 1;
				while (isRunning && (nrOfIncompleteTasks > 0)) {
					LOGGER.debug("waiting for results...");
					List<String> results = null;
					try {
						results = jedis.brpop(3, "RESULTS");
					} catch (ClassCastException x) {
						// ignore
					}
					if (results == null) {
						continue;
					}
					int N = Integer.parseInt(results.get(1));
					nrOfIncompleteTasks--;
					LOGGER.debug("received the next result set ({} results)", N);
					while (N-- > 0) {
						String resultString = jedis.brpop(0, "RESULTS").get(1);
						TaskResult result = TaskResult.fromString(resultString);
						LOGGER.debug("processing result {}", result);
						if (mustExplore(LOGGER, result.getPath())) {
							jedis.lpush("TASKS", result.intoString());
						}
					}
				}
			} catch (ClassNotFoundException x) {
				LOGGER.fatal("class-not-found while de-serializing result", x);
			} catch (IOException x) {
				LOGGER.fatal("IO problem while de-serializing result", x);
			} finally {
				isRunning = false;
				cleanJedisQueues();
				removeFiles();
			}
			LOGGER.info("");
			new Banner('O').println("DEEPSEA DONE").display(LOGGER, Level.INFO);
		}

		private boolean mustExplore(Logger lOGGER, String path) {
			// TODO Auto-generated method stub
			return false;
		}

		private void cleanJedisQueues() {
			LOGGER.debug("Deleting redis queues");
			jedis.del("TASKS");
			jedis.del("RESULTS");
			jedis.del("PROPERTIES");
		}

		private void removeFiles() {
			LOGGER.debug("Deleting working files");
			for (String supportFile : supportFiles) {
				String filename = WORKDIR + "/" + supportFile; 
				if (new File(filename).delete()) {
					LOGGER.info("Deleted {}", filename);
				} else {
					LOGGER.info("Failed to delete {}", filename);
				}
			}
			{
				String filename = WORKDIR + "/" + propertiesFile; 
				if (new File(filename).delete()) {
					LOGGER.info("Deleted {}", filename);
				} else {
					LOGGER.info("Failed to delete {}", filename);
				}
			}
		}
		
		public void shutdown() {
			LOGGER.debug("Shutting down deepsea");
			isRunning = false;
			jedis.del("RESULTS");
		}

	}

}
