package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.service.PostCachingService;
import com.mikeycaine.reactiveposts.service.WebApiService;
import com.mikeycaine.reactiveposts.webapi.ForumNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebApiTest {

	@Autowired
	WebApiService webApiService;

	@Autowired
	PostCachingService postCachingService;

	@BeforeEach
	public void makeSureForumsArePresent() {
		StepVerifier.create(postCachingService.updateForums())
			.expectNextCount(1)
			.verifyComplete();
	}

	@Test
	public void testUpdateForumSubscriptionStatus() {
		// We expect this forum to exist already
		Forum forum = webApiService.updateForumSubscriptionStatus(269, true);
		assertEquals(269, forum.getId());
		assertTrue(forum.isSubscribed());

		Forum forum2 = webApiService.updateForumSubscriptionStatus(269, false);
		assertEquals(269, forum2.getId());
		assertFalse(forum2.isSubscribed());
	}

	@Test
	public void testUpdateForumSubscriptionStatusIfForumMissing() {
		// forum -1 shouldn't exist
		assertThrows(ForumNotFoundException.class, () -> {
			Forum noForum = webApiService.updateForumSubscriptionStatus(-1, false);
		});
	}
}
