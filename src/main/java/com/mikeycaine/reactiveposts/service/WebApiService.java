package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.AuthorRepository;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import com.mikeycaine.reactiveposts.webapi.ForumSubscriptionReply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WebApiService {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final AuthorRepository authorRepository;

	public List<Forum> topLevelForums() {
		return forumRepository.topLevelForums();
	}
	public Forum updateForumSubscriptionStatus(int forumId, boolean newStatus) {
		log.info("Updating forum subscription status forum={} newStatus={}", forumId, newStatus);
		int result = forumRepository.updateForumSubscriptionStatus(forumId, newStatus);
		log.info("Got result {}", result);
		Optional<Forum> forum = forumRepository.findById(forumId);

		// TODO handle the case where forum isnt found
		return forum.get();
	}

	public List<Thread> threadsForForum(int forumId) {
		return forumRepository.findById(forumId)
				.map(forum -> threadRepository.threadsForForum(forum))
				.get();
	}
}
