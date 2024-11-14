package com.ibm.usecases.indexing;

import jakarta.annotation.Nonnull;

import java.io.File;
import java.util.Arrays;

public final class JavaIndexService extends IndexingService {

    public JavaIndexService() {
        super("java", ".java");
    }

    @Override
    boolean isModule(@Nonnull File[] files) {
        return Arrays.stream(files)
                .anyMatch(f -> f.getName().equals("pom.xml") || f.getName().equals("build.gradle"));
    }

    @Override
    boolean excludeFromIndexing(@Nonnull File file) {
        return file.getPath().contains("src/test/") || file.getName().contains("package-info");
    }
}
