package com.fatwire.cs.filter.trimfilter;

import java.util.Arrays;

import junit.framework.TestCase;

public class Trimmer2Test extends TestCase {
    Trimmer2 trimmer = new Trimmer2();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDoTrimLeave() {
        StreamState ss = new StreamState();
        char[] body = "<html><body>Hello World</body></html>".toCharArray();
        String result = "<html><body>Hello World</body></html>";

        int t = trimmer.doTrim(body, ss);

        assertEquals(result, body, t);
    }

    public void testDoTrim() {
        StreamState ss = new StreamState();
        char[] body = "<html>\n<body>Hello   World \t  \r\nFoo bar</body>\r\n</html>\r\n"
                .toCharArray();
        String result = "<html>\n<body>Hello World\nFoo bar</body>\n</html>\n";

        int t = trimmer.doTrim(body, ss);
        //        System.out.println(Arrays
        //                .toString(new String(body, 0, t).toCharArray()));
        //        System.out.println(Arrays
        //                .toString(result.toCharArray()));

        assertEquals(result, body, t);
    }
    public void XXtestDoTrimWithComments() {
        StreamState ss = new StreamState();
        char[] body = "<html>\n<body>Hello   <!-- comment --> world   <!-- <p>old text</p> --></html>\r\n"
                .toCharArray();
        String result = "<html>\n<body>Hello <!-- comment --> world <!-- <p>old text</p> --></html>\n";

        int t = trimmer.doTrim(body, ss);
        //        System.out.println(Arrays
        //                .toString(new String(body, 0, t).toCharArray()));
        //        System.out.println(Arrays
        //                .toString(result.toCharArray()));

        assertEquals(result, body, t);
    }

    public void testDoTrimSpaces() {
        StreamState ss = new StreamState();
        char[] body = "<html>   </html>".toCharArray();
        String result = "<html> </html>";

        int t = trimmer.doTrim(body, ss);

        assertEquals(result, body, t);
    }

    public void testDoTrimSpacesInsideTag() {
        StreamState ss = new StreamState();
        char[] body = "<html foo=\"walk  the  line\"\n\n\r\n  quote='single'   >   </html>"
                .toCharArray();
        String result = "<html foo=\"walk  the  line\" quote='single'> </html>";

        int t = trimmer.doTrim(body, ss);
        assertEquals(result, body, t);
    }

    public void testDoTrimLFCR() {
        StreamState ss = new StreamState();
        char[] body = "<html>\r\n</html>".toCharArray();
        String result = "<html>\n</html>";

        int t = trimmer.doTrim(body, ss);
        assertEquals(result, body, t);
    }

    public void testDoTrimDualCR() {
        StreamState ss = new StreamState();
        char[] body = "<html>\n \n</html>".toCharArray();
        String result = "<html>\n</html>";

        int t = trimmer.doTrim(body, ss);
        assertEquals(result, body, t);
    }

    public void testDoTrimMultiCR() {
        StreamState ss = new StreamState();
        char[] body = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n\n\n\n<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\n<head>\n<link rel=\"stylesheet\" media=\"all\" href=\"/cs/FirstSiteII/core.css\" />"
                .toCharArray();
        String result = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\n<head>\n<link rel=\"stylesheet\" media=\"all\" href=\"/cs/FirstSiteII/core.css\" />";
        int t = trimmer.doTrim(body, ss);
        assertEquals(result, body, t);
    }

    public void testDoTrimCData() {
        StreamState ss = new StreamState();
        char[] body = "<html>  <![cdata[ hello \r\n   ]]> foo   bar </html>"
                .toCharArray();
        String result = "<html> <![cdata[ hello \r\n   ]]> foo bar </html>";

        int t = trimmer.doTrim(body, ss);
        //diff(body,t,result);
        assertEquals(result, body, t);
    }

    public void testDoTrimLFCRAtEnd() {
        StreamState ss = new StreamState();
        char[] body = "<html>\r\n".toCharArray();
        String result = "<html>\n";

        int t = trimmer.doTrim(body, ss);

        assertEquals(result, body, t);
    }

    public void testDoTrimSpaceCR() {
        StreamState ss = new StreamState();
        char[] body = "<html> \n</html>".toCharArray();
        String result = "<html>\n</html>";

        int t = trimmer.doTrim(body, ss);

        assertEquals(result, body, t);
    }

    public void testDoTrimSpaceCRSpace() {
        StreamState ss = new StreamState();
        char[] body = "<html> \n </html>".toCharArray();
        String result = "<html>\n</html>";
        int t = trimmer.doTrim(body, ss);

        assertEquals(result, body, t);

    }

    void assertEquals(String expect, char[] test, int t) {
        assertEquals(expect, new String(test, 0, t));
    }

    void diff(char[] body, int t, String result) {
        System.out.println("*** diff");
        System.out.println(result);
        System.out.println(new String(body, 0, t));
        for (int i = 0; (i < body.length) && (i < result.length()); i++) {
            System.out.print(result.charAt(i));
            if (body[i] != result.charAt(i)) {
                System.out
                        .println((int) result.charAt(i) + " " + (int) body[i]);
            }
        }

    }

    public void testDoTrimWithPre() {
        StreamState ss = new StreamState();
        char[] body = "<html>\n<body>Hello   World \t  \r\n<pre>Foo   \r\n\r\n   bar</pre></body>\r\n</html>\r\n"
                .toCharArray();
        String result = "<html>\n<body>Hello World\n<pre>Foo   \r\n\r\n   bar</pre></body>\n</html>\n";

        int t = trimmer.doTrim(body, ss);
        //diff(body, t, result);
        assertEquals(result, body, t);
    }

    public void testDoTrimWithWhiteAtStart() {
        StreamState ss = new StreamState();
        char[] body = "  <html>\n<body>Hello   World".toCharArray();
        String result = "<html>\n<body>Hello World";

        int t = trimmer.doTrim(body, ss);
        assertEquals(result, body, t);
    }

    public void testDoTrimWithPreOnly() {
        StreamState ss = new StreamState();
        char[] body = "   <pre>Foo   \r\n\r\n   bar</pre>".toCharArray();
        String result = "<pre>Foo   \r\n\r\n   bar</pre>";

        int t = trimmer.doTrim(body, ss);
        assertEquals(result, body, t);

    }

    public void testDoTrimScript() {
        StreamState ss = new StreamState();
        char[] body = "<html><head><script src=\"\"></script></head><body>Hello  World</body></html>"
                .toCharArray();
        String result = "<html><head><script src=\"\"></script></head><body>Hello World</body></html>";

        int t = trimmer.doTrim(body, ss);
        assertEquals(result, body, t);

    }

    public void testDoTrimWithTwoArrays() {
        StreamState ss = new StreamState();
        char[] body = "   <p>Foo   \r\n".toCharArray();
        char[] body2 = "\r\n   bar</p>".toCharArray();

        String result = "<p>Foo\nbar</p>";
        int t = trimmer.doTrim(body, ss);

        int t2 = trimmer.doTrim(body2, ss);
        StringBuilder b = new StringBuilder(new String(body, 0, t));
        b.append(new String(body2, 0, t2));
        //        print(b.toString().toCharArray(),b.length());
        //        print(result.toString().toCharArray(),result.length());
        //        diff(b.toString().toCharArray(),b.length(), result);
        assertEquals(result, b.toString());

    }

    void print(char[] body, int t) {
        System.out.println(Arrays
                .toString(new String(body, 0, t).toCharArray()));
    }

    public void testDoTrimWithTwoArraysAndPre() {
        StreamState ss = new StreamState();
        char[] body = "   <pre>Foo   \r\n".toCharArray();
        char[] body2 = "\r\n   bar</pre>".toCharArray();

        String result = "<pre>Foo   \r\n\r\n   bar</pre>";
        int t = trimmer.doTrim(body, ss);
        int t2 = trimmer.doTrim(body2, ss);
        StringBuilder b = new StringBuilder(new String(body, 0, t));
        b.append(new String(body2, 0, t2));
        //diff(b.toString().toCharArray(), b.length(), result);
        assertEquals(result, b.toString());

    }

}
