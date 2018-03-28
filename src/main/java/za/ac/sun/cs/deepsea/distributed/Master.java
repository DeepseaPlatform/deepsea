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

	private static final String[] JAVASCRIPT = {
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
		Server server = new Server(WEB_PORT);
        server.setHandler(new RootHandler());
        server.start();
        server.join();
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
						produceOutput(response.getWriter(), HEADER, STYLE, JAVASCRIPT, HEADER_END, BODY_0, FORM_0, BODY_END);
					} else {
						produceOutput(response.getWriter(), HEADER, STYLE, HEADER_END, BODY_0);
						response.getWriter().print("<p>Running <code>");
						response.getWriter().print(runner.getPropertiesFile());
						response.getWriter().println("</code></p>");
						produceOutput(response.getWriter(), FORM_1, BODY_END);
					}
					baseRequest.setHandled(true);
				} else if (uri.equals("/favicon.ico")) {
					File f = new File(Master.class.getClassLoader().getResource(FAVICON).getFile());
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
					    File ff = new File("/tmp/" + filename);
					    try (OutputStream out = new FileOutputStream(ff)) {
					    		out.write(buffer);
					    }
					    if (fieldname.equals("propfile")) {
					    		runner.setPropertiesFile(filename);
					    } else {
					    		runner.addFile(filename);
					    }
					}
					// runner.start();
					response.setStatus(303);
					response.setHeader("Location", "/");
					baseRequest.setHandled(true);
				} else if (uri.equals("/quit") && (runner != null)) {
//					runner.interrupt();
//					try {
//						runner.join();
//					} catch (InterruptedException ignore) {
//						// ignore this exception
//					}
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
		}

	}

//				errorPage(socket, pout, "403", "Forbidden", "You do not have permission to access the requested URL.");

//	private static void runOnce(String[] args) {
//		if (args.length < 1) {
//			new Banner('@').println("DEEPSEA PROBLEM\nMISSING PROPERTIES FILE\n").println("USAGE: deepsea <properties file>").display(LOGGER, Level.FATAL);
//			return;
//		}
//		LOGGER.info("loading configuration file {}",  args[0]);
//		Configuration config = new Configuration();
//		if (!config.processProperties(args[0])) {
//			new Banner('@').println("DEEPSEA PROBLEM\n").println("COULD NOT READ PROPERTY FILE \"" + args[0] + "\"").display(LOGGER, Level.FATAL);
//			return;
//		}
//		if (config.getTarget() == null) {
//			new Banner('@').println("SUSPICIOUS PROPERTIES FILE\n").println("ARE YOU SURE THAT THE ARGUMENT IS A .properties FILE?").display(LOGGER, Level.FATAL);
//			return;
//		}
//		LOGGER.info("");
//		try (Jedis jedis = new Jedis("redis")) {
//			LOGGER.debug("established jedis connection");
//			jedis.lpush("TASKS", TaskResult.EMPTY.intoString());
//			LOGGER.debug("sent the first task");
//			int nrOfIncompleteTasks = 1;
//			while (nrOfIncompleteTasks > 0) {
//				LOGGER.debug("waiting for results");
//				int N = Integer.parseInt(jedis.brpop(0, "RESULTS").get(1));
//				nrOfIncompleteTasks--;
//				LOGGER.debug("received the next result set ({} results)", N);
//				while (N-- > 0) {
//					String resultString = jedis.brpop(0, "RESULTS").get(1);
//					TaskResult result = TaskResult.fromString(resultString);
//					LOGGER.debug("processing result {}", result);
//					if (mustExplore(LOGGER, result.getPath())) {
//						jedis.lpush("TASKS", result.intoString());
//					}
//				}
//			}
//		} catch (ClassNotFoundException x) {
//			LOGGER.fatal("class-not-found while de-serializing result", x);
//		} catch (IOException x) {
//			LOGGER.fatal("IO problem while de-serializing result", x);
//		}
//		LOGGER.info("");
//		new Banner('#').println("DEEPSEA DONE").display(LOGGER, Level.INFO);
//	}
//
//	private static boolean mustExplore(Logger LOGGER, String signature) {
////		char[] signArray = signature.toCharArray();
////		int signLength = signature.length();
////		if (!visitedSignatures.add(signArray.toString())) {
////			LOGGER.debug("revisit of signature \"" + signature + "\", truncating");
////			revisitCounter++;
////			while ((signature.length() > 0) && (visitedSignatures.contains(signature)
////					|| infeasibleSignatures.contains(signature))) {
////				signature = signature.substring(0, signature.length());
////			}
////		}
////		LOGGER.info("path signature: " + signature);
////		while (signature.length() > 0) {
////			/*
////			 * Flip the first char ('0' <-> '1') of signature.
////			 */
////			SegmentedPathCondition npc = spc.getNegated1st();
////			if (visitedSignatures.contains(npc.getSignature())) {
////				/*
////				 * We now know that both this path condition and the
////				 * complementary path condition (i.e., with the first conjunct
////				 * negated) have been visited. We therefore record that their
////				 * common prefix has been visited, and we drop the first
////				 * conjunct and try to negate the new first conjunct.
////				 */
////				visitedSignatures.add(spc.getParent().getSignature());
////				spc = spc.getParent();
////				logger.debug("dropping first conjunct -> " + spc.getPathCondition());
////			} else {
////				// negate first conjunct of path condition and check if it has a model
////				logger.debug("trying <" + npc.getSignature() + "> " + npc.getPathCondition());
////				Instance instance = new Instance(solver, null, npc.getPathCondition());
////				@SuppressWarnings("unchecked")
////				Map<IntVariable, Object> model = (Map<IntVariable, Object>) instance.request("model");
////				if (model == null) {
////					infeasibleSignatures.add(npc.getSignature());
////					logger.debug("no model");
////					unSatCounter++;
////					// again drop the first conjunct
////					spc = spc.getParent();
////				} else {
////					// translate model
////					Map<String, Constant> newModel = new HashMap<>();
////					for (IntVariable variable : model.keySet()) {
////						String name = variable.getName();
////						Constant value = new IntConstant((Integer) model.get(variable));
////						newModel.put(name, value);
////					}
////					String modelString = newModel.entrySet().stream().filter(p -> !p.getKey().startsWith("$"))
////							.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).toString();
////					logger.debug("new model: {}", modelString);
////					if (visitedModels.add(modelString)) {
////						return newModel;
////					} else {
////						logger.debug("model {} has been visited before, recurring", modelString);
////						return refine(spc.getParent());
////					}
////				}
////			}
////		}
////		logger.debug("all signatures explored");
////		return null;
////		
////		
////		
//		return false;
//	}

}
