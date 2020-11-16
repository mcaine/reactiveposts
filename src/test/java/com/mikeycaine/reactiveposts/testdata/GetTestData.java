package com.mikeycaine.reactiveposts.testdata;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@Slf4j
public class GetTestData implements TestDirectories {

	final static int CONNECT_TIMEOUT = 5000;
	final static int READ_TIMEOUT = 5000;

	public static void main(String[] args) {
		log.info("Looking for files...");
		TestDirectories.checkDirsExist();

		Stream.of(
			ThreadTestPage.of(Thread.withId(3942499), 1),
			ThreadTestPage.of(Thread.withId(3913301), 10), // what if a Big Mac
			ThreadTestPage.of(Thread.withId(3946225), 3),
			ThreadTestPage.of(Thread.withId(3946206), 1),

			ForumIndexTestPage.of(new Forum(161, "Goons with Spoons"),  4),
			ForumIndexTestPage.of(new Forum(192, "Inspect your gadgets"), 5),
			ForumIndexTestPage.of(new Forum(273, "GBS"),6),
			ForumIndexTestPage.of(new Forum(273, "GBS"),1)
		).forEach(TestPage::cachePage);
	}
}

