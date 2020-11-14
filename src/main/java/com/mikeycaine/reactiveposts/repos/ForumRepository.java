package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Integer>  {
	@Query("SELECT f FROM Forum f WHERE f.subscribed=true")
	List<Forum> subscribedForums();

	@Query("SELECT f FROM Forum f WHERE f.topLevelForum=true")
	List<Forum> topLevelForums();
}
