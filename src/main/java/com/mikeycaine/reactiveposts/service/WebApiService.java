package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.repos.AuthorRepository;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebApiService {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final AuthorRepository authorRepository;

	public List<Forum> topLevelForums() {
		return forumRepository.topLevelForums();
	}
}
