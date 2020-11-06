package com.mikeycaine.reactiveposts.client.content.parsed;

import com.mikeycaine.reactiveposts.model.Forum;
import lombok.Getter;

import java.util.List;

public class MainForumIndex {

	public MainForumIndex(List<Forum> forums) {
		this.forums = forums;
	}

	@Getter
	private final List<Forum> forums;
}
