package com.ibm.usecases.indexing;

import jakarta.annotation.Nonnull;

import java.io.File;

public final class PythonIndexService extends IndexingService {

    public PythonIndexService() {
        super("python", ".py");
    }

    @Override
    boolean isModule(@Nonnull File[] files) {
        return true;
    }

    @Override
    boolean excludeFromIndexing(@Nonnull File file) {
        return file.getPath().contains("test/");
    }
}