package za.ac.sun.cs.deepsea.distributed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

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
	
	private static final int WEB_PORT = 8080;
	
	private static final String FAVICON = "favicon.ico";

	private static final String WORKDIR = "/work";
	
	private static Logger LOGGER;

	private static final String[] HEADER = {
		"<!DOCTYPE html>",
		"<html en=\"en\">",
		"<head>",
		"<meta charset=\"utf-8\">",
		"<title>DEEPSEA</title>",
	};
	
	private static final String[] HEADER_END = {
		"</head>",
	};
	
	private static final String[] STYLE = {
		"<style>",
			"body{",
				"position:relative;",
				"margin:0;",
				"padding:2rem;",
				"font-family:\"Helvetica Neue\",Arial,sans-serif;",
			"}",
			"h1{",
				"font-weight:600;",
			"}",
			"input{",
				"display:block;",
				"margin-top:1rem;",
				"font-size:1rem;",
				"line-height:1.1rem;",
				"padding:.5rem;",
			"}",
			"input[type=submit]{",
				"font-size:1.1rem;",
				"line-height:1.1rem;",
				"font-weight:700;",
				"padding:1rem;",
				"border:0;",
				"background:#2196f3;",
				"color:#fff;",
			"}",
		"</style>",
	};

	private static final String[] JAVASCRIPT_0 = {
		"<script>",
			"var numberOfInputs = 1;",
			"function addMoreFiles(ev) {",
				"var flist = document.querySelector(\"#file_list\");",
				"if (flist == null) return;",
				"var ifile = document.createElement(\"input\");",
				"if (ifile == null) return;",
				"ifile.setAttribute(\"type\", \"file\");",
				"ifile.setAttribute(\"name\", \"files\" + numberOfInputs + \"[]\");",
				"ifile.setAttribute(\"id\", \"files\" + numberOfInputs);",
				"ifile.setAttribute(\"multiple\", true);",
				"flist.appendChild(ifile);",
				"numberOfInputs++;",
			"}",
			"document.addEventListener(\"DOMContentLoaded\", function (event) {",
				"var element = document.querySelector(\"#more_files\");",
				"if (element == null) return;",
				"element.addEventListener(\"click\", addMoreFiles, false);",
			"});",
		"</script>",
	};

	private static final String[] JAVASCRIPT_1 = {
		"<script>",
			"var scrollelems = [\"html\", \"body\"];",
			"var load = 30 * 1024;",
			"var poll = 1000;",
			"var fix_rn = true;",
			"var kill = false;",
			"var loading = false;",
			"var pause = false;",
			"var log_data = \"\";",
			"var log_file_size = 0;",
			"function scroll(where) {",
				"for (var i = 0; i < scrollelems.length; i++) {",
					"var s = document.querySelector(scrollelems[i]);",
					"if (s == null) continue;",
					"if (where === -1) {",
						"s.scrollTop(window.getComputedStyle(s).height);",
					"} else {",
						"s.scrollTop(where);",
					"}",
				"}",
			"}",
			"function ajax(range, success, failure) {",
				"var xhr = new XMLHttpRequest();",
				"xhr.open(\"POST\", \"/log?\" + new Date().getTime(), true);",
				"xhr.setRequestHeader(\"X-Requested-With\", \"XMLHttpRequest\");",
				"xhr.setRequestHeader(\"Range\", range);",
				"xhr.responseType = \"text\";",
				"xhr.onreadystatechange = function() {",
					"if (xhr.readyState === 4) {",
						"if (xhr.status < 300) success(xhr.responseText, xhr) else failure(xhr);",
					"}",
				"};",
				"xhr.send(data);",
			"}",
			"function getLog() {",
				"if (kill | loading) return;",
				"loading = true;",
				"var range;",
				"var first_load;",
				"var must_get_206;",
				"if (log_file_size === 0) {",
					"range = \"-\" + load.toString();",
					"first_load = true;",
					"must_get_206 = false;",
				"} else {",
					"range = (log_file_size - 1).toString() + \"-\";",
					"first_load = false;",
					"must_get_206 = log_file_size > 1;",
				"}",
				"ajax(\"bytes=\" + range",
					"function (data, xhr) {",
						"loading = false;",
						"var content_size;",
						"if (xhr.status === 206) {",
							"var c_r = xhr.getResponseHeader(\"Content-Range\");",
							"if (!c_r) throw \"Server did not respond with a Content-Range\";",
							"log_file_size = parseInt2(c_r.split(\"/\")[1]);",
							"content_size = parseInt2(xhr.getResponseHeader(\"Content-Length\"));",
						"} else if (xhr.status === 200) {",
							"if (must_get_206) throw \"Expected 206 Partial Content\";",
							"content_size = log_file_size = parseInt2(xhr.getResponseHeader(\"Content-Length\"));",
						"} else {",
							"throw \"Unexpected status \" + xhr.status;",
						"}",
						"if (first_load && data.length > load) throw \"Server's response was too long\";",
						"var added = false;",
						"if (first_load) {",
							"if (content_size < log_file_size) {",
								"var start = data.indexOf(\"\\n\");",
								"log_data = data.substring(start + 1);",
							"} else {",
								"log_data = data;",
							"}",
							"added = true;",
						"} else {",
							"log_data += data.substring(1);",
							"if (log_data.length > load) {",
								"var start = log_data.indexOf(\"\\n\", log_data.length - load);",
								"log_data = log_data.substring(start + 1);",
							"}",
							"if (data.length > 1) added = true;",
						"}",
						"if (added) show_log(added);",
						"setTimeout(get_log, poll);",
					"},",
					"function (xhr) {",
						"loading = false;",
						"if (xhr.status === 416 || xhr.status == 404) {",
							"log_file_size = 0;",
							"log_data = \"\";",
							"show_log();",
							"setTimeout(get_log, poll);",
						"} else {",
							"throw \"Unknown AJAX Error (status \" + xhr.status + \")\";",
						"}",
				"});",
			"}",
			"function showLog() {",
				"if (pause) return;",
				"var t = log_data;",
				"if (fix_rn) { t = t.replace(/\n/g, \"\\r\\n\");",
				"var dataelem = document.querySelector(\"#data\");",
				"if (dataelem == null) return;",
				"dataelem.textContent = t;",
				"scroll(-1);",
			"}",
			"function togglePause(ev) {",
				"pause = !pause;",
				"var element = document.querySelector(\"#pause\");",
				"if (element == null) return;",
				"element.textContent = (pause ? \"Unpause\", \"Pause\");",
				"showLog();",
				"ev.preventDefault();",
			"}",
			"function error(what) {",
				"kill = true;",
				"var dataelem = document.querySelector(\"#data\");",
				"if (dataelem == null) return;",
				"dataelem.textContent = \"An error occured.\r\nReloading may help.\r\n\" + what);",
				"scroll(0);",
				"return false;",
			"}",		
			"document.addEventListener(\"DOMContentLoaded\", function (event) {",
				"window.onerror = error;",
				"var element = document.querySelector(\"#pause\");",
				"if (element == null) return;",
				"element.addEventListener(\"click\", togglePause, false);",
				"getLog();",
			"});",
		"</script>",
	};
		
	private static final String[] FORM_0 = {
		"<form method=\"post\" action=\"/run\" enctype=\"multipart/form-data\">",
			"<h2>Properties file</h2>",
			"<input class=\"propfile files\" type=\"file\" name=\"propfile\" id=\"propfile\">",
			"<h2>Jar libraries</h2>",
			"<div id=\"file_list\">",
				"<input class=\"jarfile files\" type=\"file\" name=\"files0[]\" id=\"files0\" multiple>",
			"</div>",
			"<input type=\"button\" id=\"more_files\" value=\"Click for more files\">",
			"<input id=\"launch\" type=\"submit\" name=\"submit\" value=\"Launch\">",
		"</form>",
	};
	
	private static final String[] FORM_1 = {
			"<form method=\"post\" action=\"/quit\" enctype=\"multipart/form-data\">",
				"<input id=\"quit\" type=\"submit\" name=\"submit\" value=\"Quit\">",
			"</form>",
		};
		
	private static final String[] BODY_0 = {
		"<body>",
		"<h1>DEEPSEA</h1>",
	};
	
	private static final String[] BODY_1 = {
		"<body>",
		"<h1>Bad request</h1>",
		"<p>Your browser sent a request that this server does not understand.</p>",
	};
	
	private static final String[] BODY_END = {
		"</body>",
		"</html>",
	};

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
		jedis = new Jedis("redis");
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
//				LOGGER.info("get uri: ({})", uri);
				if (uri.equals("index.html") || uri.equals("/")) {
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType("text/html; charset=utf-8");
					if (runner == null) {
						produceOutput(response.getWriter(), HEADER, STYLE, JAVASCRIPT_0, HEADER_END, BODY_0, FORM_0, BODY_END);
					} else {
						produceOutput(response.getWriter(), HEADER, STYLE, JAVASCRIPT_1, HEADER_END, BODY_0);
						response.getWriter().print("<p>Running <code>");
						response.getWriter().print(runner.getPropertiesFile());
						response.getWriter().println("</code></p>");
						produceOutput(response.getWriter(), FORM_1);
						response.getWriter().println("<p><a id=\"pause\" href=\"#\">Pause</a></p>");
						response.getWriter().println("<pre id=\"data\">Loading...</pre>");
						produceOutput(response.getWriter(), BODY_END);
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
					produceOutput(response.getWriter(), HEADER, STYLE, HEADER_END, BODY_1, BODY_END);
					baseRequest.setHandled(true);
				}
			} else {
				String uri = request.getRequestURI();
//				LOGGER.info("post uri: ({})", uri);
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
					    File ff = new File(WORKDIR + "/" + filename);
					    try (OutputStream out = new FileOutputStream(ff)) {
					    		out.write(buffer);
					    }
					    if (fieldname.equals("propfile")) {
					    		runner.setPropertiesFile(filename);
					    } else {
					    		runner.addFile(filename);
					    }
					}
					runner.start();
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
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.setContentType("text/html; charset=utf-8");
					produceOutput(response.getWriter(), HEADER, STYLE, HEADER_END, BODY_1, BODY_END);
					baseRequest.setHandled(true);
				}
			}
		}

		private void produceOutput(PrintWriter out, String[]... content) {
			for (String[] part : content) {
				for (String line : part) {
					out.println(line);
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
		public void start() {
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
					LOGGER.debug("waiting for results");
					int N = Integer.parseInt(jedis.brpop(0, "RESULTS").get(1));
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
			jedis.del("TASKS");
			jedis.del("RESULTS");
			jedis.del("PROPERTIES");
		}

		private void removeFiles() {
			for (String supportFile : supportFiles) {
				new File(WORKDIR + "/" + supportFile).delete();
			}
			new File(WORKDIR + "/" + propertiesFile).delete();
		}
		
		public void shutdown() {
			isRunning = false;
		}

	}

}
