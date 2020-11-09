package com.mikeycaine.reactiveposts.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "reactiveposts")
public class UpdatesConfig {

	/** Updating the forum's index of threads for subscribed forums*/
	@Getter @Setter
	private Duration threadsUpdateInitialDelay = Duration.ofSeconds(5);

	@Getter @Setter
	private Duration threadsUpdateInterval = Duration.ofMinutes(15);

	@Getter @Setter
	private int threadsUpdateMaxRetries = 5;

	/** Updating posts for subscribed threads */
	@Getter @Setter
	private Duration postsUpdateInitialDelay = Duration.ofSeconds(5);

	@Getter @Setter
	private Duration postsUpdateInterval = Duration.ofMinutes(5);

	@Getter @Setter
	private int postsUpdateMaxRetries = 5;

	/** How many pages of a forums index to acquire each time we update */
	@Getter @Setter
	private int indexDepth = 1;
}
