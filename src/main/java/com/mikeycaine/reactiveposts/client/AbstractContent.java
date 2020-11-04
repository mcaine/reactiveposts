package com.mikeycaine.reactiveposts.client;

import reactor.core.publisher.Mono;

abstract class AbstractContent implements PageContent {
	final private String content;

	AbstractContent(String content) {
		this.content = content;
	}

	@Override
	public String content() {
		return content;
	}
}
