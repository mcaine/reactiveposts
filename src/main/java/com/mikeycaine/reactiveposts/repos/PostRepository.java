package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Author;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
	@Query("SELECT p FROM Post p WHERE p.thread=?1 AND p.pageNum=?2 ORDER BY p.id")
	public List<Post> getPostsForThreadPage(Thread thread, int pageId);

	@Query("SELECT p FROM Post p WHERE p.author=?1")
	public List<Post> getPostsForAuthor(Author author);

	@Query("SELECT COUNT(p) FROM Post p WHERE p.author=?1")
	public int getPostCountForAuthor(Author author);
}
