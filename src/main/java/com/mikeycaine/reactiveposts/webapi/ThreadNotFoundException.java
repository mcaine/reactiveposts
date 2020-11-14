package com.mikeycaine.reactiveposts.webapi;

public class ThreadNotFoundException extends RuntimeException {
	public ThreadNotFoundException(int threadId) {
		super("Thread " + threadId + " no found");
	}
}
