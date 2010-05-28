package com.fatwire.gst.trimfilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Trims whitespace from a a html fragment, but leaves whitespace in pre,textarea,style and script tags, as well as in cdata tags.
 * 
 * @author Dolf.Dijkstra
 * @since Sep 19, 2009
 */
public class Trimmer2 {
    private static final Log log = LogFactory.getLog(Trimmer2.class);

    interface DocumentState {
        static boolean unforgiven = true;

        void nextChar(StreamState ss);

    }

    private static final DocumentState inTag = new DocumentState() {

        public String toString() {
            return "inTag";
        }

        public void nextChar(StreamState ss) {
            switch (ss.currentChar()) {
            case '<':
                log.warn("uhh, a new tag in another tag at "
                        + ss.toStateString());
                if (unforgiven)
                    throw new IllegalStateException(
                            "uhh, a new tag in another tag.");
                ss.writeChar();
                break;
            case '>':
                switch (ss.previousChar()) {
                case '\n':
                case ' ':
                case '\t':
                case '\r':
                    ss.overWritePrevious();
                    break;
                default:
                    ss.writeChar();
                    break;
                }
                endTag(ss);
                break;
            case '\'':
            case '"':
                if (ss.currentChar() == ss.quoteState()) {
                    ss.quoteState('x'); //close the quote
                } else if (ss.quoteState() == 'x') {
                    ss.quoteState(ss.currentChar());
                }
                ss.writeChar();
                break;
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                ss.appendToTagName();
                //endTag(ss);
                if (ss.quoteState() == 'x') {
                    switch (ss.previousChar()) {
                    case ' ':
                        ss.leaveLastChar();
                        break;
                    case '\t':
                    case '\r':
                    case '\n':
                        ss.skipChar();
                        break;
                    default:
                        ss.writeSpace();
                        break;
                    }
                } else {
                    ss.writeChar();
                }
                break;
            case '[':
                ss.writeChar();
                ss.appendToTagName();
                if (ss.tagName().length() == 9) {
                    endTag(ss);
                }
                break;
            default:
                ss.appendToTagName();
                ss.writeChar();

                break;
            }

        }

        private void endTag(StreamState ss) {
            ss.currentSpecialElement(null);
            //System.out.println(ss.tagName().toString());
            for (SpecialElement s : SpecialElement.values()) {
                if (ss.tagStartsWith(s.start)) { //TODO: does not return correct value is current body array is shorter then start.length;
                    ss.currentSpecialElement(s);

                    ss.tagName().setLength(0);
                    if (s == SpecialElement.DOCTYPE) {
                        //TODO: next state should come off SpecialElement
                        ss.current(content);
                    } else {
                        ss.current(leaveWhiteSpace);
                    }
                    return;
                }
            }
            ss.tagName().setLength(0);
            ss.quoteState('x');
            ss.current(content);

        }

    };

    static final DocumentState content = new DocumentState() {
        public String toString() {
            return "content";
        }

        public void nextChar(StreamState ss) {
            switch (ss.currentChar()) {
            case '<':
                ss.writeChar();
                ss.startTag();
                ss.current(inTag);
                break;
            case '>':
                log.warn("uhh, a tag end ('>') in content "
                        + ss.toStateString());
                if (unforgiven)
                    throw new IllegalStateException(
                            "uhh, a tag end ('>') in content ");
                ss.writeChar();
                break;

            case ' ':
            case '\t':
            case '\r':
                switch (ss.previousChar()) {
                //we want to maintain LF even if it is followed by other whitespace
                //case '>':
                case '\n':
                    ss.leaveLastChar();
                    break;
                case ' ':
                case '\t':
                case '\r':
                    ss.skipChar();
                    break;
                default:
                    ss.writeChar();
                    break;
                }

                break;
            case '\n':
                switch (ss.previousChar()) {
                //we want to maintain LF even if it is followed by other whitespace
                //case '>':
                case '\n':
                    ss.leaveLastChar();
                    break;
                case ' ':
                case '\t':
                case '\r':
                    ss.overWritePrevious();
                    break;
                default:
                    ss.writeChar();
                    break;
                }

                break;

            default:
                ss.writeChar();
            }

        }
    };

    private static final DocumentState leaveWhiteSpace = new DocumentState() {
        public String toString() {
            return "leaveWhiteSpace";
        }

        public void nextChar(StreamState ss) {
            switch (ss.currentChar()) {
            case '<':
                ss.startTag();
                ss.writeChar();
                break;
            case '>':
                ss.appendToTagName();
                ss.writeChar();
                endTag(ss);
                break;
            default:
                ss.appendToTagName();
                ss.writeChar();
                break;

            }

        }

        void endTag(StreamState ss) {
            //System.out.println(ss.tagName().toString());
            if (ss.atEndOfCurrentWhiteList()) {
                ss.endTag();
                ss.current(content);
            }

        }

    };

    //DocumentState current = content;

    /**
     * 
     */
    public Trimmer2() {
        super();

    }

    public int doTrim(char[] body, StreamState ss) {
        return doTrim(body, 0, body.length, ss);
    }

    /*
     *         //<pre ... </pre>
            //textarea
            //cdata
            //script
            //

     */

    /**
     * <p>Removes all the whitespace characters from the body array.</p>
     * <p>The basis of the algorythm is that only characters will be removed and as a result the resulting array is shorter or of equal size of the body array. 
     * With this understanding the body array is not copied but only manipulated. 
     * Whitespace is removed and latter chatacters are moved to the front of the array.
     * This should result in a very memory and cpu effecient algorythm</p> 
     * 
     * 
     */

    public int doTrim(final char[] body, final int offset, final int len,
            StreamState ss) {

        ss.initArray(body, offset, len);

        while (!ss.atEnd()) {
            //debug(ss.readPos + " " + body[ss.readPos]);
            ss.doNextChar();
        }

        return ss.finishArray();
    }

}
