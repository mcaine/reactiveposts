package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
	@Query("SELECT p FROM Post p WHERE p.thread=?1 AND p.pageNum=?2 ORDER BY p.id")
	List<Post> getPostsForThreadPage(Thread thread, int pageId);
}
