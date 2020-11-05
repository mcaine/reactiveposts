package com.mikeycaine.reactiveposts.testdata;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

interface Directories {
	String threadsDir = "src/test/resources/testpages/threads";
	String indexesDir = "src/test/resources/testpages/indexes";
	String [] allDirectories = {threadsDir, indexesDir};

	static void checkDirsExist() {
		Stream.of(allDirectories).forEach(path -> {
			if (!Files.exists(Path.of(path))) {
				throw new RuntimeException(new FileNotFoundException(path));
			}
		});
	}
}
