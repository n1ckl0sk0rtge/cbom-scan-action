package com.ibm.usecases.indexing;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class IndexingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

    private final String languageIdentifier;
    private final String languageFileExtension;

    protected IndexingService(String languageIdentifier, String languageFileExtension) {
        this.languageIdentifier = languageIdentifier;
        this.languageFileExtension = languageFileExtension;
    }

    @Nonnull
    public List<ProjectModule> index(@Nonnull File projectDirectory, @Nullable String subFolder) {
        if (subFolder != null) {
            projectDirectory = new File(projectDirectory.getPath() + File.separator + subFolder);
        }
        return detectModules(projectDirectory, new ArrayList<>());
    }

    private List<ProjectModule> detectModules(
            @Nonnull File projectDirectory, @Nonnull List<ProjectModule> projectModules) {
        final File[] filesInDir = projectDirectory.listFiles();
        if (filesInDir == null) {
            return Collections.emptyList();
        }
        for (File file : filesInDir) {
            if (isModule(filesInDir)) {
                if (file.isDirectory() && !".git".equals(file.getName())) {
                    LOGGER.debug("Extracting files from project: {}", file.getPath());
                    this.detectModules(file, projectModules);
                }
            } else {
                List<InputFile> files = getFiles(file, new ArrayList<>());
                if (!files.isEmpty()) {
                    ProjectModule project =
                            new ProjectModule(getProjectIdentifier(projectDirectory, file), files);
                    projectModules.add(project);
                }
            }
        }
        return projectModules;
    }

    @Nonnull
    public List<InputFile> getFiles(
            @Nonnull File directory, @Nonnull final List<InputFile> inputFiles) {
        File[] filesInDir = directory.listFiles();
        if (filesInDir != null) {
            for (File file : filesInDir) {
                if (file.isDirectory() && !".git".equals(file.getName())) {
                    getFiles(new File(directory + File.separator + file.getName()), inputFiles);
                } else if (file.isFile()
                        && file.getName().endsWith(this.languageFileExtension)
                        && !excludeFromIndexing(file)) {
                    try {
                        TestInputFileBuilder builder = createTestFileBuilder(directory, file);
                        builder.setLanguage(this.languageIdentifier);
                        inputFiles.add(builder.build());
                    } catch (IOException iox) {
                        // ignore file
                    }
                }
            }
        }
        return inputFiles;
    }

    @Nonnull
    protected TestInputFileBuilder createTestFileBuilder(
            @Nonnull File projectDirectory, @Nonnull File file) throws IOException {
        return new TestInputFileBuilder("", file.getPath())
                .setProjectBaseDir(projectDirectory.toPath())
                .setContents(Files.readString(file.toPath()))
                .setCharset(UTF_8)
                .setType(InputFile.Type.MAIN);
    }

    @Nonnull
    protected String getProjectIdentifier(@Nonnull File projectDirectory, @Nonnull File file) {
        String path = file.getPath();
        path = path.substring(projectDirectory.getPath().length() + 1);
        // remove repo dir
        int slashIdx = path.indexOf('/');
        if (slashIdx < 0) {
            return "";
        }
        path = path.substring(slashIdx + 1);

        if (path.contains("/src")) {
            path = path.replace("/src", "");
        }
        return path;
    }

    abstract boolean isModule(@Nonnull File[] files);

    abstract boolean excludeFromIndexing(@Nonnull File file);
}

