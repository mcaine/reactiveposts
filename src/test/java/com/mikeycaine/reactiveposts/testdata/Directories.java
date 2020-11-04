package com.mikeycaine.reactiveposts.testdata;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

interface Directories {
	final static String threadsDir = "src/test/resources/testpages/threads";
	final static String indexesDir = "src/test/resources/testpages/indexes";
	final static String [] allDirectories = {threadsDir, indexesDir};

	static void checkDirsExist() {
		Stream.of(allDirectories).forEach(path -> {
			if (!Files.exists(Path.of(path))) {
				throw new RuntimeException(new FileNotFoundException(path));
			}
		});
	}
}
