package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
