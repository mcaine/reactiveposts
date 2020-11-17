package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Author;
import com.mikeycaine.reactiveposts.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

	@Query("SELECT a FROM Author a WHERE a.titleText IS NULL AND a.titleURL IS NULL")
	public List<Author> authorsWithoutTitle();
}
