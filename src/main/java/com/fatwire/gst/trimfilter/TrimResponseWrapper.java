package com.fatwire.gst.trimfilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * 
 * @author Dolf.Dijkstra
 * @since Sep 19, 2009
 */

public class TrimResponseWrapper extends HttpServletResponseWrapper {
    private static final Log log = LogFactory.getLog(TrimResponseWrapper.class);

    private static final ErrorReporter jsErrorReporter = new ErrorReporter() {

        public void error(String message, String sourceName, int line,
                String lineSource, int lineOffset) {
            log.error(message + " at (" + line + ")" + line);

        }

        public EvaluatorException runtimeError(String message,
                String sourceName, int line, String lineSource, int lineOffset) {

            return new EvaluatorException(message, sourceName, line,
                    lineSource, lineOffset);

        }

        public void warning(String message, String sourceName, int line,
                String lineSource, int lineOffset) {
            log.warn(message + " at (" + line + ")" + line);

        }

    };

    private PrintWriter writer = null;

    private StringWriter fullBuffer;

    public TrimResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public PrintWriter getWriter() throws IOException {
        if (this.writer != null)
            return this.writer;

        PrintWriter orig = super.getWriter(); //call this so we get the exception here if this is illegal
        String ct = this.getContentType(); //CS sets ContentType before it get's the writer
        if (isHtml(ct)) {
            log.trace("Trimming enabled");
            TrimWriter sw = new TrimWriter(orig);
            writer = new PrintWriter(sw);
        } else if (isJavaScript(ct)) {
            writer = new PrintWriter(fullBuffer = new StringWriter());
        } else if (isCSS(ct)) {
            writer = new PrintWriter(fullBuffer = new StringWriter());
        } else {
            writer = orig;
        }
        return this.writer;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#setContentLength(int)
     */
    @Override
    public void setContentLength(int len) {
        //Leave this to the servlet engine
    }

    private boolean isHtml(String ct) {
        if (ct == null)
            return false;
        return ct.toLowerCase().startsWith("text/html")
                || ct.toLowerCase().startsWith("application/xhtml+xml");
    }

    private boolean isJavaScript(String ct) {
        if (ct == null)
            return false;
        return ct.toLowerCase().startsWith("text/javascript");
    }

    private boolean isCSS(String ct) {
        if (ct == null)
            return false;
        return ct.toLowerCase().startsWith("text/css");
    }

    public void compress() throws IOException {
        if (fullBuffer == null)
            return;
        String ct = getContentType();
        if (isJavaScript(ct)) {
            compressJS();
        } else if (isCSS(ct)) {
            compressJS();
        }
        flushBuffer();

    }

    void compressJS() throws IOException {
        String end = endAsFTCSCache();

        JavaScriptCompressor compressor = new JavaScriptCompressor(
                new StringReader(fullBuffer.toString()), jsErrorReporter);
        int linebreakpos = -1;
        boolean nomunge = false;
        boolean jswarn = false;
        boolean preserveAllSemiColons = false;
        boolean preserveStringLiterals = false;

        compressor.compress(super.getWriter(), linebreakpos, !nomunge, jswarn,
                preserveAllSemiColons, preserveStringLiterals);
        if (end != null) {
            super.getWriter().write(end);
        }

    }

    private String endAsFTCSCache() {
        String end = null;
        if (fullBuffer.getBuffer().length() >= 16) {
            end = fullBuffer.getBuffer().substring(
                    fullBuffer.getBuffer().length() - 16);
            log.trace(end);
            if (!(HelperStrings.STATUS_CACHED.equals(end) || HelperStrings.STATUS_NOTCACHED
                    .equals(end))) {
                end = null;
            }
        }
        return end;
    }

    void compressCSS() throws IOException {
        String end = endAsFTCSCache();
        CssCompressor compressor = new CssCompressor(new StringReader(
                fullBuffer.toString()));

        int linebreakpos = -1;

        compressor.compress(super.getWriter(), linebreakpos);
        if (end != null) {
            super.getWriter().write(end);
        }

    }

}