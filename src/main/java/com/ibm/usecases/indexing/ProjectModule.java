package com.ibm.usecases.indexing;

import jakarta.annotation.Nonnull;
import org.sonar.api.batch.fs.InputFile;

import java.util.List;

public record ProjectModule(@Nonnull String identifier, @Nonnull List<InputFile> inputFileList) { }
