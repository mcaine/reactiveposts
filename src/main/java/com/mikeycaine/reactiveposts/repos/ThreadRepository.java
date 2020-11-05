package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mikeycaine.reactiveposts.model.Thread;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Integer>  {
	@Query("SELECT t FROM Thread t WHERE t.subscribed=true")
	List<Thread> subscribedThreads();
}
