package com.mikeycaine.reactiveposts.client;

public class ValidationUtils {

	private final static int MAX_REQUEST_PAGE_COUNT = 100;

	public static void validatePageIdParam(int pageId) {
		if (pageId < 1 || pageId > 100000) {
			throw new IllegalArgumentException("Page ID " + pageId + " out of allowed range");
		}
	}

	public static int validatePageRangeParams(int startPageId, int endPageId) {
		validatePageIdParam(startPageId);
		validatePageIdParam(endPageId);
		int count = endPageId - startPageId + 1;
		if (count > MAX_REQUEST_PAGE_COUNT) {
			throw new IllegalArgumentException("Can't retrieve more than " + MAX_REQUEST_PAGE_COUNT + " pages at a time");
		}
		return count;
	}
}
