package com.ibm.usecases.scanning;

import com.ibm.mapper.model.INode;

import java.util.List;
import java.util.function.Consumer;

public interface IScannerService  extends Consumer<List<INode>>  {
}
