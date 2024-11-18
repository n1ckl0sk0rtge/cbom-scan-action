package com.ibm.usecases.scanning;

import com.ibm.mapper.model.INode;
import com.ibm.usecases.indexing.ProjectModule;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.cyclonedx.model.Bom;

import java.util.List;
import java.util.function.Consumer;

public interface IScannerService  extends Consumer<List<INode>>  {

    @Nonnull
    Bom scan(@Nullable String subFolder, @Nonnull List<ProjectModule> index);
}
