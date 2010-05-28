package com.fatwire.cs.filter.trimfilter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class ReaderWriter extends Writer {
    private final StringBuilder sw = new StringBuilder();

    int srcBegin = 0;

    int srcEnd = 0;

    Reader getReader() {
        return new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                srcEnd += len;
                if (srcEnd > sw.length())
                    srcEnd = sw.length();
                sw.getChars(srcBegin, srcEnd, cbuf, off);
                int t = srcEnd - srcBegin;
                srcBegin = srcEnd + 1;
                return t;
            }

            @Override
            public void close() throws IOException {
                sw.setLength(0);
                sw.trimToSize();
            }

        };
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void flush() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void write(char[] cbuf, int offset, int len) throws IOException {
        sw.append(cbuf, offset, len);

    }

}
