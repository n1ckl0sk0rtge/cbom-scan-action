package com.ibm.usecases.scanning;

import com.ibm.mapper.model.INode;
import com.ibm.output.IOutputFileFactory;
import com.ibm.output.cyclondx.CBOMOutputFile;
import jakarta.annotation.Nonnull;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.cyclonedx.model.Evidence;
import org.cyclonedx.model.component.evidence.Occurrence;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class ScannerService implements IScannerService {
    @Nonnull protected final File projectDirectory;
    @Nonnull protected final CBOMOutputFile cbomOutputFile;

    protected ScannerService(@Nonnull File projectDirectory, @Nonnull CBOMOutputFile cbomOutputFile) {
        this.projectDirectory = projectDirectory;
        this.cbomOutputFile = cbomOutputFile;
    }

    @Override
    public void accept(@Nonnull List<INode> nodes) {
        synchronized (this) {
            this.cbomOutputFile.add(nodes);
        }
    }

    @Nonnull
    protected synchronized Bom getBOM() {
        final Bom bom = this.cbomOutputFile.getBom();
        // sanitizeOccurrence
        bom.getComponents().forEach(component -> sanitizeOccurrence(projectDirectory, component));
        // reset scanner
        final com.ibm.plugin.ScannerManager scannerMgr =
                new com.ibm.plugin.ScannerManager(IOutputFileFactory.DEFAULT);
        scannerMgr.reset();

        return bom;
    }

    static void sanitizeOccurrence(
            @Nonnull final File projectDirectory, @Nonnull Component component) {
        List<Occurrence> occurrenceList =
                Optional.ofNullable(component.getEvidence())
                        .map(Evidence::getOccurrences)
                        .orElse(Collections.emptyList());

        if (occurrenceList.isEmpty()) {
            return;
        }
        final String baseDirPath = projectDirectory.getAbsolutePath();
        occurrenceList.forEach(
                occurrence -> {
                    if (occurrence.getLocation().startsWith(baseDirPath)) {
                        occurrence.setLocation(
                                occurrence.getLocation().substring(baseDirPath.length() + 1));
                    }
                });
    }
}
