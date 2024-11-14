package com.ibm.usecases.scanning;

import com.ibm.usecases.indexing.ProjectModule;
import com.ibm.output.cyclondx.CBOMOutputFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.cyclonedx.model.Bom;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.java.SonarComponents;
import org.sonar.java.ast.JavaAstScanner;
import org.sonar.java.classpath.ClasspathForMain;
import org.sonar.java.classpath.ClasspathForTest;
import org.sonar.java.model.JavaVersionImpl;
import org.sonar.java.model.VisitorsBridge;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaScannerService extends ScannerService {
    private static final JavaVersion JAVA_VERSION =
            new JavaVersionImpl(JavaVersionImpl.MAX_SUPPORTED);

    public JavaScannerService(@Nonnull File projectDirectory, @Nonnull CBOMOutputFile cbomOutputFile) {
        super(projectDirectory, cbomOutputFile);
    }

    public synchronized Bom scan(@Nullable String subFolder,
                                 @Nonnull List<ProjectModule> index) {
        final List<JavaCheck> visitors = List.of(new JavaDetectionCollectionRule(this));
        final SensorContextTester sensorContext = SensorContextTester.create(projectDirectory);
        sensorContext.setSettings(
                new MapSettings()
                        .setProperty(SonarComponents.FAIL_ON_EXCEPTION_KEY, false)
                        .setProperty(SonarComponents.SONAR_AUTOSCAN, false));
        final DefaultFileSystem fileSystem = sensorContext.fileSystem();
        final ClasspathForMain classpathForMain =
                new ClasspathForMain(sensorContext.config(), fileSystem);
        final ClasspathForTest classpathForTest =
                new ClasspathForTest(sensorContext.config(), fileSystem);
        final SonarComponents sonarComponents =
                new SonarComponents(
                        null, fileSystem, classpathForMain, classpathForTest, null, null);
        sonarComponents.setSensorContext(sensorContext);

        for (ProjectModule project : index) {
            final JavaAstScanner jscanner =
                    new JavaAstScanner(sonarComponents);
            VisitorsBridge visitorBridge =
                    new VisitorsBridge(
                            visitors, new ArrayList<>(), sonarComponents, JAVA_VERSION);
            jscanner.setVisitorBridge(visitorBridge);
            jscanner.scan(project.inputFileList());
        }

        return this.getBOM();
    }
}
