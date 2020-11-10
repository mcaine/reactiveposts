package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Integer>  {
	@Query("SELECT f FROM Forum f WHERE f.subscribed=true")
	List<Forum> subscribedForums();

	@Query("SELECT f FROM Forum f WHERE f.topLevelForum=true")
	List<Forum> topLevelForums();

	@Query("UPDATE Forum f SET f.subscribed=?2 WHERE f.id=?1")
	@Modifying
	int updateForumSubscriptionStatus(int forumId, boolean subscriptionStatus);
}
