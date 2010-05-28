/**
 * 
 */
package com.fatwire.cs.filter.trimfilter;

import com.fatwire.cs.filter.trimfilter.Trimmer2.DocumentState;

class StreamState {

    private char previousChar = ' ';

    private char[] body;

    private int readPos = 0;

    private int end = 0;

    private int writePos = 0;

    private DocumentState current = Trimmer2.content;

    private SpecialElement currentSpecial = null;

    private StringBuilder tagName = new StringBuilder();

    private boolean recordTagName = false;

    private char quoteChar = 'x';

    public StreamState() {
        super();
    }

    public void doNextChar() {
        current.nextChar(this);
        ++readPos;
    }

    public void current(DocumentState state) {
        //System.out.println("setting to " + state);
        this.current = state;
    }

    char currentChar() {
        return body[readPos];
    }

    boolean atEnd() {
        return readPos >= end;
    }

    public void writeChar() {
        if (readPos != writePos) {
            body[writePos] = body[readPos];
        }
        previousChar = body[readPos];
        writePos++;

    }

    public void skipChar() {
        previousChar = body[readPos];
    }

    public void leaveLastChar() {
        // do nothing, leave preChar and write pos

    }

    public void overWritePrevious() {
        if (writePos > 0)
            body[writePos - 1] = body[readPos];
        previousChar = body[readPos];
    }

    public void appendToTagName() {
        //System.out.println("appendToTagName " + currentChar());
        switch (currentChar()) {
        case ' ':
        case '\r':
        case '\n':
        case '\t':
            /*
             * after first whitespace we do not care
             * what characters are written unless they are a '[' or a '>'.
             */
            recordTagName = false; 
            break;
        case ']':
            tagName.append(currentChar());
            break;
        case '>':
            tagName.append(currentChar());
            break;
        default:
            if (recordTagName) {
                tagName.append(currentChar());

            }
            break;
        }
    }

    public boolean tagStartsWith(char[] c) {
        if (tagName.length() < c.length)
            return false;
        for (int i = 0; i < c.length; i++) {
            if (tagName.charAt(i) != c[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean endsWith(char[] c) {
        if (tagName.length() < c.length)
            return false;

        for (int i = c.length - 1, e = tagName.length() - 1; i > 0; --i) {
            if (tagName.charAt(e) != c[i]) {
                return false;
            }
            e--;
        }
        return true;
    }

    public void startTag() {
        tagName.setLength(0);
        recordTagName = true;
        appendToTagName();

    }

    public void endTag() {
        recordTagName = false;
    }

    public boolean atEndOfCurrentWhiteList(){
        return endsWith(currentSpecial.end);
    }
    public void initArray(char[] body, int offset, int len) {
        end = len + offset;
        this.body = body;
        readPos = offset;
        writePos = offset;

    }

    public int finishArray() {
        body = null;
        return writePos;
    }

    public int previousChar() {
        return this.previousChar;
    }

    /**
     * 
     * 
     * @return a String around the current read position, for debugging and error detection
     */
    
    public String toStateString() {
        int start = readPos;
        if (readPos - 15 > 0) {
            start = readPos - 15;
        }

        return new String(body, start, end - start);
    }

    public StringBuilder tagName() {
        return this.tagName;
    }

    public char quoteState() {
        return this.quoteChar;
    }

    public void quoteState(char c) {
        this.quoteChar = c;

    }

    public void currentSpecialElement(SpecialElement i) {
        this.currentSpecial = i;

    }

    public SpecialElement currentLeaveReason() {
        return this.currentSpecial;
    }

    public void writeSpace() {
        body[writePos] = ' ';
        previousChar = ' ';
        writePos++;

        
    }

}