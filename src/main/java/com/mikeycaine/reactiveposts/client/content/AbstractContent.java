package com.mikeycaine.reactiveposts.client.content;

import java.util.Optional;

abstract class AbstractContent<T> implements Content<T> {
	final protected String content;

	AbstractContent(String content) {
		this.content = content;
	}

	@Override
	public String content() {
		return content;
	}

	public void ensureContentPresent() {
		if (null == content || content.isEmpty() || content.isBlank()) {
			throw new MissingContentException();
		}
	}
}
