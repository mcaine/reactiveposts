package com.mikeycaine.reactiveposts.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mikeycaine.reactiveposts.model.Thread;

import javax.transaction.Transactional;
import java.util.Optional;

//@Service
//@RequiredArgsConstructor
//@Transactional
public class DBService {
//	private final AuthorRepository authorRepository;
//	private final ThreadRepository threadRepository;
//	private final PostRepository postRepository;
//
//	public void saveThread(Thread thread) {
//		Author newAuthor = thread.getAuthor();
//		if (newAuthor != null) {
//			Optional<Author> optDBAuthor = authorRepository.findById(thread.getAuthor().getId());
//			optDBAuthor.ifPresent(dbAuthor -> {
//				if (null != newAuthor.getTitleText() && null == dbAuthor.getTitleText()) {
//					dbAuthor.setTitleText(newAuthor.getTitleText());
//				}
//				if (null != newAuthor.getTitleURL() && null == dbAuthor.getTitleURL()) {
//					dbAuthor.setTitleURL(newAuthor.getTitleURL());
//				}
//				//newAuthor.getThreads().remove(thread);
//				dbAuthor.getThreads().add(thread);
//				thread.setAuthor(dbAuthor);
//				dbAuthor = authorRepository.save(dbAuthor);
//			});
//		}
//		threadRepository.save(thread);
//	}
//
//	public void savePost(Post post) {
//		Author newAuthor = post.getAuthor();
//		if (newAuthor != null) {
//			Optional<Author> optDBAuthor = authorRepository.findById(post.getAuthor().getId());
//			optDBAuthor.ifPresent(dbAuthor -> {
//				if (null != newAuthor.getTitleText() && null == dbAuthor.getTitleText()) {
//					dbAuthor.setTitleText(newAuthor.getTitleText());
//				}
//				if (null != newAuthor.getTitleURL() && null == dbAuthor.getTitleURL()) {
//					dbAuthor.setTitleURL(newAuthor.getTitleURL());
//				}
//				dbAuthor.getPosts().add(post);
//				dbAuthor = authorRepository.save(dbAuthor);
//				post.setAuthor(dbAuthor);
//			});
//		}
//		postRepository.save(post);
//	}
}
