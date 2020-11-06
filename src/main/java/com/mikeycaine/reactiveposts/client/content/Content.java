package com.mikeycaine.reactiveposts.client.content;

public interface Content<T> {
	T parsed();
	String content();
}
