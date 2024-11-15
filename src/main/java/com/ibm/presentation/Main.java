package com.ibm.presentation;

import com.ibm.output.cyclondx.CBOMOutputFile;
import com.ibm.usecases.indexing.ProjectModule;
import com.ibm.usecases.indexing.JavaIndexService;
import com.ibm.usecases.scanning.JavaScannerService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.cyclonedx.Version;
import org.cyclonedx.exception.GeneratorException;
import org.cyclonedx.generators.BomGeneratorFactory;
import org.cyclonedx.generators.json.BomJsonGenerator;
import org.cyclonedx.model.Bom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("java:S106")
public class Main {

    public static void main(@Nonnull String[] args) {
        final String workspace = "/github/workspace/";
        final File projectDirectory = new File(workspace);

        final JavaIndexService javaIndexService = new JavaIndexService();
        final List<ProjectModule> projectModules = javaIndexService.index(projectDirectory, null);

        final JavaScannerService javaScannerService = new JavaScannerService(projectDirectory, new CBOMOutputFile());
        final Bom bom = javaScannerService.scan(null, projectModules);

        final BomJsonGenerator bomGenerator = BomGeneratorFactory.createJson(Version.VERSION_16, bom);
        @Nullable String bomString = null;
        try {
            bomString = bomGenerator.toJsonString();
            System.out.println(bomString);
        } catch (GeneratorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (bomString == null) {
            return;
        }
        final String githubOutput = System.getenv("GITHUB_OUTPUT");
        try (FileWriter writer = new FileWriter(githubOutput, true)){
            writer.write("cbom=" + bomString + "\n");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
