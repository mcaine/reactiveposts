package com.mikeycaine.reactiveposts.webapi;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ForumSubscriptionReply {
	@Getter
	@Setter
	private final int forumId;

	@Getter
	@Setter
	private final String subscriptionStatus;
}
