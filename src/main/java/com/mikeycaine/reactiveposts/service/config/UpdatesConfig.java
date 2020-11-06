package com.mikeycaine.reactiveposts.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "reactiveposts")
public class UpdatesConfig {

	@Getter @Setter
	private Duration threadsUpdateInterval = Duration.ofMinutes(15);

	@Getter @Setter
	private Duration postsUpdateInterval = Duration.ofMinutes(5);
}
