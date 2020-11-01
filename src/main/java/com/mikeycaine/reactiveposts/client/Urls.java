package com.mikeycaine.reactiveposts.client;

public class Urls {

    private static final String PAGE_ADDRESS = "/showthread.php?threadid=%d&perpage=40&pagenumber=%d";
    private static final String FORUM_ADDRESS = "/forumdisplay.php?forumid=%d";
    public static final String INDEX_PHP = "/index.php";

    public static String pageAddress(long threadId, long pageNumber) {
        return String.format(PAGE_ADDRESS, threadId, pageNumber);
    }

    public static String forumIndexAddress(long forumId) {
        return String.format(FORUM_ADDRESS, forumId);
    }

    public static String forumIndex() {
        return INDEX_PHP;
    }
}
