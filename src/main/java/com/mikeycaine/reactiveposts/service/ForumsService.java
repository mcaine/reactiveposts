package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.client.content.parsed.PostsPage;
import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
import com.mikeycaine.reactiveposts.model.Author;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.AuthorRepository;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import com.mikeycaine.reactiveposts.webapi.ForumNotFoundException;
import com.mikeycaine.reactiveposts.webapi.ThreadNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ForumsService {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final AuthorRepository authorRepository;

	public long getForumsCount() {
		return forumRepository.count();
	}

	public List<Forum> saveAll(List<Forum> forums) {
		return forumRepository.saveAll(forums);
	}

	public Forum findForumById(int forumId) {
		return forumRepository.findById(forumId).orElseThrow(() -> new ForumNotFoundException(forumId));
	}

	public List<Forum> findAll() {
		return forumRepository.findAll();
	}

	public List<Forum> subscribedForums() {
		return forumRepository.subscribedForums();
	}

	public List<Thread> subscribedThreads() {
		return threadRepository.subscribedThreads();
	}

	public PostsPage persistsPostsPage(PostsPage postsPage) {
		Thread thread = postsPage.getThread();
		int pageNum = postsPage.getPageNum();

		if (pageNum < thread.getMaxPageNumber()) {
			thread.setPagesGot(pageNum);
			threadRepository.save(thread);
		}

		postsPage.getMaxPageNum().ifPresent(maxPageNum -> {
			thread.setMaxPageNumber(maxPageNum);
			threadRepository.save(thread);
		});

		postsPage.getPosts().forEach(post -> {
			Author newPostAuthor = post.getAuthor();
			Author author = authorRepository.findById(newPostAuthor.getId()).map(dbAuthor ->{
				dbAuthor.setName(newPostAuthor.getName());
				dbAuthor.setTitleText(newPostAuthor.getTitleText());
				dbAuthor.setTitleURL(newPostAuthor.getTitleURL());
				return dbAuthor;
			}).orElse(authorRepository.save(newPostAuthor));
			post.setAuthor(author);
		});
		log.info("Persisting {} posts for page {} (of {}) for {}", postsPage.getPosts().size(), postsPage.getPageNum(), thread.getMaxPageNumber(), thread.toString());
		postRepository.saveAll(postsPage.getPosts());
		return postsPage;
	}

	public ThreadsIndex persistThreadsIndex(ThreadsIndex threadsIndex) {
		threadsIndex.getThreads().forEach(this::mergeThreadInfo);
		return threadsIndex;
	}

	public Thread mergeThreadInfo(Thread thread) {
		Author author = authorRepository.findById(thread.getAuthor().getId())
							.orElse(authorRepository.save(thread.getAuthor()));
		thread.setAuthor(author);

		return threadRepository.findById(thread.getId()).map(dbThread -> {
			dbThread.setMaxPageNumber(thread.getMaxPageNumber());
			dbThread.setName(thread.getName());
			return dbThread;
		}).orElseGet(() -> threadRepository.save(thread));
	}

	public Thread updateThreadSubscriptionStatus(int threadId, boolean newStatus) {
		log.info("Updating thread subscription status thread={} newStatus={}", threadId, newStatus);

		Thread thread = threadRepository.findById(threadId)
			.orElseThrow(() -> new ThreadNotFoundException(threadId));

		thread.setSubscribed(newStatus);
		log.info("...updated thread subscription status for thread {}, status now {}", threadId, thread.isSubscribed());

		return thread;
	}

	public Forum updateForumSubscriptionStatus(int forumId, boolean newStatus) {
		log.info("Updating forum subscription status forum={} newStatus={}", forumId, newStatus);

		Forum forum = forumRepository.findById(forumId)
			.orElseThrow(() -> new ForumNotFoundException(forumId));

		forum.setSubscribed(newStatus);
		log.info("...updated forum subscription status for forum {}, status now {}", forumId, forum.isSubscribed());

		return forum;
	}

	public void logForums() {
		forumRepository.topLevelForums().forEach(forum -> log.info(forum.prettyPrint()));
	}
}
