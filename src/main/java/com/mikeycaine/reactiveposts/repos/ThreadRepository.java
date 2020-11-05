package com.mikeycaine.reactiveposts.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikeycaine.reactiveposts.model.Thread;


public interface ThreadRepository extends JpaRepository<Thread, Integer>  {
}
