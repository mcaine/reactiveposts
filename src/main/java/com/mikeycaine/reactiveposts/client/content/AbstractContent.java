package com.mikeycaine.reactiveposts.client.content;

abstract class AbstractContent implements PageContent {
	final protected String content;

	AbstractContent(String content) {
		this.content = content;
	}

	@Override
	public String content() {
		return content;
	}
}
