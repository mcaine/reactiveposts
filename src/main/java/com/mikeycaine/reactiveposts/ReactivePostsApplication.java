package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactivePostsApplication {

	@Autowired
	Client client;

	public static void main(String[] args) {
		SpringApplication.run(ReactivePostsApplication.class, args);
	}

}
