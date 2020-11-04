package com.mikeycaine.reactiveposts.client.content;

abstract class AbstractContent {
	final protected String content;

	AbstractContent(String content) {
		this.content = content;
	}

	public String content() {
		return content;
	}
}
