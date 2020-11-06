package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.mikeycaine.reactiveposts.service.config")
public class ReactivePostsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReactivePostsApplication.class, args);
	}
}
