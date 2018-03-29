package za.ac.sun.cs.deepsea.distributed;

import java.io.PrintWriter;

public class MasterHtml {

	private static void outputHeader0(PrintWriter out) {
		out.println("<!DOCTYPE html>");
		out.println("<html en=\"en\">");
		out.println("<head>");
		out.println("<meta charset=\"utf-8\">");
		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
		out.println("<link rel=\"shortcut icon\" href=\"/favicon.ico\" type=\"image/vnd.microsoft.icon\" id=\"favicon\"/>");
		out.println("<link rel=\"icon\" href=\"/favicon.ico\" type=\"image/png\"/>");
		out.println("<title>DEEPSEA</title>");
	}

	private static void outputHeaderEnd(PrintWriter out) {
		out.println("</head>");
	}

	private static void outputStyle(PrintWriter out) {
		out.println("<style>");
		out.println("body,html{");
		out.println("  width:100%;");
		out.println("  height:100%;");
		out.println("}");
		out.println("body{");
		out.println("  position:relative;");
		out.println("  margin:0;");
		out.println("  padding:1rem;");
		out.println("  font-family:\"Helvetica Neue\",Arial,sans-serif;");
		out.println("}");
		out.println("h1{");
		out.println("  font-weight:600;");
		out.println("  margin-top: 0;");
		out.println("}");
		out.println("input{");
		out.println("  display:block;");
		out.println("  margin-top:1rem;");
		out.println("  font-size:1rem;");
		out.println("  line-height:1.1rem;");
		out.println("  padding:.5rem;");
		out.println("}");
		out.println("input[type=submit]{");
		out.println("  font-size:1.1rem;");
		out.println("  line-height:1.1rem;");
		out.println("  font-weight:700;");
		out.println("  padding:1rem;");
		out.println("  border:0;");
		out.println("  background:#2196f3;");
		out.println("  color:#fff;");
		out.println("}");
		out.println("pre.logfile, code#data{");
		out.println("  display:block;");
		out.println("  font-size:12px;");
		out.println("  font-family:Menlo,\"DejaVu Sans Mono\",\"Liberation Mono\",Consolas,\"Ubuntu Mono\",\"Courier New\",\"andale mono\",\"lucida console\",monospace;");
		out.println("}");
		out.println("pre.logfile{");
		out.println("  position:relative;");
		out.println("  background:#000;");
		out.println("  color:#c4c4c4;");
		out.println("  white-space:pre;");
		out.println("  word-break:break-all;");
		out.println("  word-wrap:break-word;");
		out.println("  overflow-x:auto;");
		out.println("  overflow-y:scroll;");
		out.println("  max-height:60%;");
		out.println("  padding:8px;");
		//out.println("  margin-bottom:1rem;");
		out.println("}");
		out.println("code#data{");
		out.println("  background-color:transparent;");
		out.println("  color:inherit;");
		out.println("  overflow-x:auto;");
		out.println("  overflow-y:scroll;");
		out.println("  max-height:100%;");
		out.println("  white-space:pre-wrap;");
		out.println("}");
		out.println("</style>");
	}

	private static void outputJavascript0(PrintWriter out) {
		out.println("<script>");
		out.println("var numberOfInputs = 1;");
		out.println("function addMoreFiles(ev) {");
		out.println("  var flist = document.querySelector(\"#file_list\");");
		out.println("  if (flist == null) return;");
		out.println("  var ifile = document.createElement(\"input\");");
		out.println("  if (ifile == null) return;");
		out.println("  ifile.setAttribute(\"type\", \"file\");");
		out.println("  ifile.setAttribute(\"name\", \"files\" + numberOfInputs + \"[]\");");
		out.println("  ifile.setAttribute(\"id\", \"files\" + numberOfInputs);");
		out.println("  ifile.setAttribute(\"multiple\", true);");
		out.println("  flist.appendChild(ifile);");
		out.println("  numberOfInputs++;");
		out.println("}");
		out.println("document.addEventListener(\"DOMContentLoaded\", function (event) {");
		out.println("  var element = document.querySelector(\"#more_files\");");
		out.println("  if (element == null) return;");
		out.println("  element.addEventListener(\"click\", addMoreFiles, false);");
		out.println("});");
		out.println("</script>");
	}

	private static void outputJavascript1(PrintWriter out) {
		out.println("<script>");
		out.println("var load = 30 * 1024;");
		out.println("var poll = 1000;");
		out.println("var fix_rn = true;");
		out.println("var kill = false;");
		out.println("var loading = false;");
		out.println("var pause = false;");
		out.println("var log_data = \"\";");
		out.println("var log_file_size = 0;");
		out.println("function parseInt2(value) {");
		out.println("  if (!(/^[0-9]+$/.test(value))) throw \"Invalid integer \" + value;");
		out.println("  var v = Number(value);");
		out.println("  if (isNaN(v)) throw \"Invalid integer \" + value;");
		out.println("  return v;");
		out.println("}");
		out.println("function scroll(where) {");
		out.println("  var s = document.querySelector(\"#prebottom\");");
		out.println("  if (s == null) return;");
		out.println("  s.scrollIntoView();");
		out.println("}");
		out.println("function ajax(range, success, failure) {");
		out.println("  var xhr = new XMLHttpRequest();");
		out.println("  xhr.open(\"POST\", \"/log?\" + new Date().getTime(), true);");
		out.println("  xhr.setRequestHeader(\"X-Requested-With\", \"XMLHttpRequest\");");
		out.println("  xhr.setRequestHeader(\"Range\", range);");
		out.println("  xhr.responseType = \"text\";");
		out.println("  xhr.onreadystatechange = function() {");
		out.println("    if (xhr.readyState === 4) {");
		out.println("      if (xhr.status < 300) success(xhr.responseText, xhr); else failure(xhr);");
		out.println("    }");
		out.println("  };");
		out.println("  xhr.send(data);");
		out.println("}");
		out.println("function getLog() {");
		out.println("  if (kill | loading) return;");
		out.println("  loading = true;");
		out.println("  var range;");
		out.println("  var first_load;");
		out.println("  var must_get_206;");
		out.println("  if (log_file_size === 0) {");
		out.println("    range = \"-\" + load.toString();");
		out.println("    first_load = true;");
		out.println("    must_get_206 = false;");
		out.println("  } else {");
		out.println("    range = (log_file_size - 1).toString() + \"-\";");
		out.println("    first_load = false;");
		out.println("    must_get_206 = log_file_size > 1;");
		out.println("  }");
		out.println("  ajax(\"bytes=\" + range,");
		out.println("    function (data, xhr) {");
		out.println("      loading = false;");
		out.println("      var content_size;");
		out.println("      if (xhr.status === 206) {");
		out.println("        var c_r = xhr.getResponseHeader(\"Content-Range\");");
		out.println("        if (!c_r) throw \"Server did not respond with a Content-Range\";");
		out.println("        log_file_size = parseInt2(c_r.split(\"/\")[1]);");
		out.println("        content_size = parseInt2(xhr.getResponseHeader(\"Content-Length\"));");
		out.println("      } else if (xhr.status === 200) {");
		out.println("        if (must_get_206) throw \"Expected 206 Partial Content\";");
		out.println("        content_size = log_file_size = parseInt2(xhr.getResponseHeader(\"Content-Length\"));");
		out.println("      } else {");
		out.println("        throw \"Unexpected status \" + xhr.status;");
		out.println("      }");
		out.println("      if (first_load && data.length > load) throw \"Server's response was too long\";");
		out.println("      var added = false;");
		out.println("      if (first_load) {");
		out.println("        if (content_size < log_file_size) {");
		out.println("          var start = data.indexOf(\"\\n\");");
		out.println("          log_data = data.substring(start + 1);");
		out.println("        } else {");
		out.println("          log_data = data;");
		out.println("        }");
		out.println("        added = true;");
		out.println("      } else {");
		out.println("        log_data += data.substring(1);");
		out.println("        if (log_data.length > load) {");
		out.println("          var start = log_data.indexOf(\"\\n\", log_data.length - load);");
		out.println("          log_data = log_data.substring(start + 1);");
		out.println("        }");
		out.println("        if (data.length > 1) added = true;");
		out.println("      }");
		out.println("      if (added) showLog(added);");
		out.println("      setTimeout(getLog, poll);");
		out.println("    },");
		out.println("    function (xhr) {");
		out.println("      loading = false;");
		out.println("      if (xhr.status === 416 || xhr.status == 404) {");
		out.println("        log_file_size = 0;");
		out.println("        log_data = \"\";");
		out.println("        showLog();");
		out.println("        setTimeout(getLog, poll);");
		out.println("      } else {");
		out.println("        throw \"Unknown AJAX Error (status \" + xhr.status + \")\";");
		out.println("      }");
		out.println("  });");
		out.println("}");
		out.println("function showLog() {");
		out.println("  if (pause) return;");
		out.println("  var t = log_data;");
		out.println("  if (fix_rn) t = t.replace(/\\n/g, \"\\r\\n\");");
		out.println("  var dataelem = document.querySelector(\"#data\");");
		out.println("  if (dataelem == null) return;");
		out.println("  dataelem.textContent = t;");
		out.println("  scroll(-1);");
		out.println("}");
		out.println("function togglePause(ev) {");
		out.println("  pause = !pause;");
		out.println("  var element = document.querySelector(\"#pause\");");
		out.println("  if (element == null) return;");
		out.println("  element.textContent = (pause ? \"Unpause\" : \"Pause\");");
		out.println("  showLog();");
		out.println("  ev.preventDefault();");
		out.println("}");
		out.println("function error(what) {");
		out.println("  kill = true;");
		out.println("  var dataelem = document.querySelector(\"#data\");");
		out.println("  if (dataelem == null) return;");
		out.println("  dataelem.textContent = \"An error occured.\\r\\nReloading may help.\\r\\n\" + what;");
		out.println("  scroll(0);");
		out.println("  return false;");
		out.println("}");
		out.println("document.addEventListener(\"DOMContentLoaded\", function (event) {");
		out.println("  window.onerror = error;");
		out.println("  var element = document.querySelector(\"#pause\");");
		out.println("  if (element == null) return;");
		out.println("  element.addEventListener(\"click\", togglePause, false);");
		out.println("  getLog();");
		out.println("});");
		out.println("</script>");
	}

	private static void outputBody0(PrintWriter out) {
		out.println("<body>");
		out.println("<h1>DEEPSEA</h1>");
	}

	private static void outputBody1(PrintWriter out) {
		out.println("<body>");
		out.println("<h1>Bad request</h1>");
		out.println("<p>Your browser sent a request that this server does not understand.</p>");
	}
	
	private static void outputBodyEnd(PrintWriter out) {
		out.println("</body>");
		out.println("</html>");
	}

	private static void outputForm0(PrintWriter out) {
		out.println("<form method=\"post\" action=\"/run\" enctype=\"multipart/form-data\">");
		out.println("  <h2>Properties file</h2>");
		out.println("  <input class=\"propfile files\" type=\"file\" name=\"propfile\" id=\"propfile\">");
		out.println("  <h2>Jar libraries</h2>");
		out.println("  <div id=\"file_list\">");
		out.println("    <input class=\"jarfile files\" type=\"file\" name=\"files0[]\" id=\"files0\" multiple>");
		out.println("  </div>");
		out.println("  <input type=\"button\" id=\"more_files\" value=\"Click for more files\">");
		out.println("  <input id=\"launch\" type=\"submit\" name=\"submit\" value=\"Launch\">");
		out.println("</form>");
	}

	private static void outputForm1(PrintWriter out) {
		out.println("<form method=\"post\" action=\"/quit\" enctype=\"multipart/form-data\">");
		out.println("  <input id=\"quit\" type=\"submit\" name=\"submit\" value=\"Quit\">");
		out.println("</form>");
	}
	
	public static void outputMainPage(PrintWriter out) {
		outputHeader0(out);
		outputStyle(out);
		outputJavascript0(out);
		outputHeaderEnd(out);
		outputBody0(out);
		outputForm0(out);
		outputBodyEnd(out);
	}

	public static void outputRunningPage(PrintWriter out, String propertiesFile) {
		outputHeader0(out);
		outputStyle(out);
		outputJavascript1(out);
		outputHeaderEnd(out);
		outputBody0(out);
		out.print("<p>Running <code>" + propertiesFile + "</code></p>");
		outputForm1(out);
		out.println("<p><a id=\"pause\" href=\"#\">Pause</a></p>");
		out.println("<pre class=\"logfile\"><code id=\"data\">Loading...</code><span id=\"prebottom\">&nbsp;</span></pre>");
		outputBodyEnd(out);
	}

	public static void outputBadRequest(PrintWriter out) {
		outputHeader0(out);
		outputStyle(out);
		outputHeaderEnd(out);
		outputBody1(out);
		outputBodyEnd(out);
	}

}
