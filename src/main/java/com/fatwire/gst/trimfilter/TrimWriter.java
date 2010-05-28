package com.fatwire.gst.trimfilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Dolf.Dijkstra
 * @since Sep 19, 2009
 */
public class TrimWriter extends Writer {
    private static final Log log = LogFactory.getLog(TrimWriter.class);

    private final Writer delegate;

    private static final Trimmer2 trimmer = new Trimmer2();;

    private final StreamState ss;

    /**
     * @param delegate
     */
    public TrimWriter(PrintWriter delegate) {
        super();
        this.delegate = delegate;

        ss = new StreamState();
    }

    @Override
    public void close() throws IOException {
        delegate.close();

    }

    @Override
    public void flush() throws IOException {
        delegate.flush();

    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("trimming: " + (len - off)); //only interested in these chars in the array.
            log.trace(new String(cbuf, off, len));
        }
        int t = trimmer.doTrim(cbuf, off, len, ss);
        if (log.isDebugEnabled())
            log.debug("removing " + (len - t) + " off " + len);
        if (log.isTraceEnabled())
            log.trace(new String(cbuf, off, t)); //trimming will make the array 'shorter'
        delegate.write(cbuf, off, t);
    }

}
