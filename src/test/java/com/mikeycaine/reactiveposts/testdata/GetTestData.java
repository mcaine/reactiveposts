package com.mikeycaine.reactiveposts.testdata;

import com.mikeycaine.reactiveposts.model.Forum;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class GetTestData implements Directories {

	final static int CONNECT_TIMEOUT = 5000;
	final static int READ_TIMEOUT = 5000;

	public static void main(String[] args) throws Exception {
		log.info("Looking for files...");

		Directories.checkDirsExist();

		ThreadPageSpec[] threadPageSpecs = {
			ThreadPageSpec.of(3942499, 1),
			ThreadPageSpec.of(3913301, 10) // what if a Big Mac
		};

        Arrays.stream(threadPageSpecs).forEach(ThreadPageSpec::cacheThreadPage);

        IndexPageSpec[] indexPageSpecs = {
        	IndexPageSpec.of(new Forum(161, "Goons with Spoons"),  4),
	        IndexPageSpec.of(new Forum(192, "Inspect your gadgets"), 5),
	        IndexPageSpec.of(new Forum(273, "GBS"),6)
        };

		Arrays.stream(indexPageSpecs).forEach(IndexPageSpec::cacheIndexPage);
	}
}

