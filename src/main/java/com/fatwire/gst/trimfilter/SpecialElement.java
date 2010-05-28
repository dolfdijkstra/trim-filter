package com.fatwire.gst.trimfilter;

public enum SpecialElement {

    /*
    static final LeaveWhiteTag[] preserveList = new LeaveWhiteTag[6];
    static {
        preserveList[0] = new LeaveWhiteTag("<pre", "</pre>");
        preserveList[1] = new LeaveWhiteTag("<script", "</script>");
        preserveList[2] = new LeaveWhiteTag("<style", "</style>");
        preserveList[3] = new LeaveWhiteTag("<textarea", "</textarea>");
        preserveList[4] = new LeaveWhiteTag("<!DOCTYPE",">"); 
        preserveList[5] = new LeaveWhiteTag("<![cdata[", "]]>");
    }
    */
    PRE("<pre", "</pre>"), SCRIPT("<script", "</script>"), STYLE("<style",
            "</style>"), TEXTAREA("<textarea", "</textarea>"), DOCTYPE(
            "<!DOCTYPE", ">"), CDATA("<![cdata[", "]]>");

    char[] start;

    char[] end;

    SpecialElement(String start, String end) {
        this.start = start.toCharArray();
        this.end = end.toCharArray();
    }
    /*
        static class LeaveWhiteTag {

            char[] start;

            char[] end;

            LeaveWhiteTag(String start, String end) {
                this.start = start.toCharArray();
                this.end = end.toCharArray();
            }
        }
    */
}
