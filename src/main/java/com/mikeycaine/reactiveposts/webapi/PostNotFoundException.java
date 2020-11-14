package com.mikeycaine.reactiveposts.webapi;

public class PostNotFoundException extends RuntimeException {
	public PostNotFoundException(int postId) {
		super("Can't find post with id " + postId);
	}
}
