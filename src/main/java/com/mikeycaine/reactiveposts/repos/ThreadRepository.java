package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mikeycaine.reactiveposts.model.Thread;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Integer>  {
	@Query("SELECT t FROM Thread t WHERE t.subscribed=true")
	List<Thread> subscribedThreads();

	@Query("SELECT t FROM Thread t WHERE t.forum = ?1 ORDER BY t.subscribed DESC, t.id DESC")
	List<Thread> threadsForForum(Forum forum);
}
