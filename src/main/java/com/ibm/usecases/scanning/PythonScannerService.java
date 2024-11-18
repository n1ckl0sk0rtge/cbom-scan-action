package com.ibm.usecases.scanning;

import com.ibm.output.cyclondx.CBOMOutputFile;
import com.ibm.usecases.indexing.ProjectModule;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.cyclonedx.model.Bom;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.plugins.python.api.PythonCheck;
import org.sonar.plugins.python.api.PythonVisitorContext;
import org.sonar.plugins.python.api.tree.FileInput;

import java.io.File;
import java.util.List;

public class PythonScannerService extends ScannerService {

    public PythonScannerService(@Nonnull File projectDirectory, @Nonnull CBOMOutputFile cbomOutputFile) {
        super(projectDirectory, cbomOutputFile);
    }

    @Nonnull
    @Override
    public synchronized Bom scan(@Nullable String subFolder,
                                 @Nonnull List<ProjectModule> index) {
        final PythonCheck visitor = new PythonDetectionCollectionRule(this);

        for (ProjectModule project : index) {
            for (InputFile inputFile : project.inputFileList()) {
                final PythonScannableFile pythonScannableFile = new PythonScannableFile(inputFile);
                final FileInput parsedFile = pythonScannableFile.parse();
                final PythonVisitorContext context =
                        new PythonVisitorContext(
                                parsedFile,
                                pythonScannableFile,
                                this.projectDirectory,
                                project.identifier());
                visitor.scanFile(context);
            }
        }
        return this.getBOM();
    }
}
