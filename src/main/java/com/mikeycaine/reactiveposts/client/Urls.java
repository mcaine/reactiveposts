package com.mikeycaine.reactiveposts.client;

public class Urls {

    private static final String PAGE_ADDRESS = "/showthread.php?threadid=%d&perpage=40&pagenumber=%d";
    private static final String FORUM_ADDRESS = "/forumdisplay.php?forumid=%d&sortorder=desc&sortfield=lastpost&pagenumber=%d";
    public static final String INDEX_PHP = "/index.php";

    public static String postsPageAddress(int threadId, int pageNumber) {
        return String.format(PAGE_ADDRESS, threadId, pageNumber);
    }

    public static String forumThreadsIndexAddress(int forumId, int pageNumber) {
        return String.format(FORUM_ADDRESS, forumId, pageNumber);
    }

    public static String mainforumIndex() {
        return INDEX_PHP;
    }
}
