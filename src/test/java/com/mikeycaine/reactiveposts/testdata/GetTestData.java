package com.mikeycaine.reactiveposts.testdata;

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
        	IndexPageSpec.of(161),  // Goons with Spoons
	        IndexPageSpec.of(192)   // Inspect your gadgets
        };

		Arrays.stream(indexPageSpecs).forEach(IndexPageSpec::cacheIndexPage);
	}
}

