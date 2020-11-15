package com.mikeycaine.reactiveposts.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "reactiveposts")
public class UpdatesConfig {

	/* Updating the forum's index of threads for subscribed forums*/

	/** Interval between each read of the index of subscribed forums */
	@Getter @Setter
	private Duration threadsUpdateInterval = Duration.ofMinutes(15);

	/** Number of times the thread updater can die before we give up */
	@Getter @Setter
	private int threadsUpdateMaxRetries = 5;

	/* Updating posts for subscribed threads */

	/** Interval between each check for the latest page for subscribed threads */
	@Getter @Setter
	private Duration postsUpdateInterval = Duration.ofMinutes(5);

	/** Number of times the posts updater can die before we give up */
	@Getter @Setter
	private int postsUpdateMaxRetries = 5;

	/** How many pages of a forums index to acquire each time we update */
	@Getter @Setter
	private int indexDepth = 1;
}
