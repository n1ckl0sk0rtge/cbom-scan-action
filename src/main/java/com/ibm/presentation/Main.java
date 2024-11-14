package com.ibm.presentation;

import com.ibm.output.cyclondx.CBOMOutputFile;
import com.ibm.usecases.indexing.ProjectModule;
import com.ibm.usecases.indexing.JavaIndexService;
import com.ibm.usecases.scanning.JavaScannerService;
import jakarta.annotation.Nonnull;
import org.cyclonedx.Version;
import org.cyclonedx.exception.GeneratorException;
import org.cyclonedx.generators.BomGeneratorFactory;
import org.cyclonedx.generators.json.BomJsonGenerator;
import org.cyclonedx.model.Bom;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(@Nonnull String[] args) {
        final File projectDirectory = new File(System.getProperty("user.dir"));

        final JavaIndexService javaIndexService = new JavaIndexService();
        final List<ProjectModule> projectModules = javaIndexService.index(projectDirectory, null);

        final JavaScannerService javaScannerService = new JavaScannerService(projectDirectory, new CBOMOutputFile());
        final Bom bom = javaScannerService.scan(null, projectModules);

        final BomJsonGenerator bomGenerator = BomGeneratorFactory.createJson(Version.VERSION_16, bom);
        try {
            final String bomString = bomGenerator.toJsonString();
            System.out.println(bomString);
        } catch (GeneratorException e) {
            System.out.println("Could not generate CBOM:" + e.getMessage());
        }

    }
}
