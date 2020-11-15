package com.mikeycaine.reactiveposts.client.content;

import org.springframework.util.StringUtils;

public abstract class AbstractContent<T> implements Content<T> {
	final protected String content;

	public AbstractContent(String content) {
		this.content = content;
	}

	@Override
	public String content() {
		return content;
	}

	public void ensureContentPresent() {
		if (!StringUtils.hasText(this.content)) {
			throw new MissingContentException();
		}
	}
}
