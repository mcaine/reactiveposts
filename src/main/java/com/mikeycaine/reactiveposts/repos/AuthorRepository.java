package com.mikeycaine.reactiveposts.repos;

import com.mikeycaine.reactiveposts.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
}
