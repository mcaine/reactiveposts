package com.mikeycaine.reactiveposts.webapi;

public class ForumNotFoundException extends RuntimeException{
	public ForumNotFoundException(int forumId) {
		super("Couldn't find forum with id " + forumId);
	}
}
