package com.ibm.presentation;

import com.ibm.output.cyclondx.CBOMOutputFile;
import com.ibm.usecases.indexing.ProjectModule;
import com.ibm.usecases.indexing.JavaIndexService;
import com.ibm.usecases.indexing.PythonIndexService;
import com.ibm.usecases.scanning.JavaScannerService;
import com.ibm.usecases.scanning.PythonScannerService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.cyclonedx.Version;
import org.cyclonedx.exception.GeneratorException;
import org.cyclonedx.generators.BomGeneratorFactory;
import org.cyclonedx.generators.json.BomJsonGenerator;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.cyclonedx.model.Dependency;
import org.cyclonedx.model.Metadata;
import org.cyclonedx.model.OrganizationalEntity;
import org.cyclonedx.model.Service;
import org.cyclonedx.model.metadata.ToolInformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("java:S106")
public class Main {
    private static final String ACTION_NAME = "cbom-scan-action";
    private static final String ACTION_ORG = "";

    public static void main(@Nonnull String[] args) {
        final String workspace = System.getenv("GITHUB_WORKSPACE");
        final File projectDirectory = new File(workspace);

        // java
        final JavaIndexService javaIndexService = new JavaIndexService();
        final List<ProjectModule> javaProjectModules = javaIndexService.index(projectDirectory, null);

        final JavaScannerService javaScannerService = new JavaScannerService(projectDirectory, new CBOMOutputFile());
        final Bom javaBom = javaScannerService.scan(null, javaProjectModules);

        // python
        final PythonIndexService pythonIndexService = new PythonIndexService();
        final List<ProjectModule> pythonProjectModules = pythonIndexService.index(projectDirectory, null);

        final PythonScannerService pythonScannerService = new PythonScannerService(projectDirectory, new CBOMOutputFile());
        final Bom pythonBom = pythonScannerService.scan(null, pythonProjectModules);

        // create bom
        final Bom bom = new Bom();
        bom.setSerialNumber("urn:uuid:" + UUID.randomUUID());

        final Metadata metadata = new Metadata();
        metadata.setTimestamp(new Date());

        final ToolInformation scannerInfo = new ToolInformation();

        final Service scannerService = new Service();
        scannerService.setName(ACTION_NAME);

        final OrganizationalEntity organization = new OrganizationalEntity();
        organization.setName(ACTION_ORG);

        scannerService.setProvider(organization);

        scannerInfo.setServices(List.of(scannerService));

        metadata.setToolChoice(scannerInfo);

        bom.setMetadata(metadata);

        final List<Component> components = new ArrayList<>(javaBom.getComponents());
        components.addAll(pythonBom.getComponents());
        bom.setComponents(components);

        final List<Dependency> dependencies = new ArrayList<>(javaBom.getDependencies());
        dependencies.addAll(pythonBom.getDependencies());
        bom.setDependencies(dependencies);

        final BomJsonGenerator bomGenerator = BomGeneratorFactory.createJson(Version.VERSION_16, bom);

        @Nullable String bomString = null;
        try {
            bomString = bomGenerator.toJsonString();
            System.out.println(bomString);
        } catch (GeneratorException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        if (bomString == null) {
            return;
        }

        final String githubOutput = System.getenv("GITHUB_OUTPUT");
        try (FileWriter writer = new FileWriter(githubOutput, true)) {
            writer.write("cbom=" + bomString + "\n");
        } catch (IOException e) {
            System.out.println("Error: Could not write CBOM to output. " + e.getMessage());
            System.exit(1);
        }
    }
}
