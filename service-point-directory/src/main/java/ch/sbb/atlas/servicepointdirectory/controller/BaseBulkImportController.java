package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseBulkImportController {

  private final AtlasExceptionHandler atlasExceptionHandler;

  protected <T> List<BulkImportItemExecutionResult> executeBulkImport(
      List<BulkImportUpdateContainer<T>> bulkImportContainers,
      BiConsumer<String, BulkImportUpdateContainer<T>> updateByUserFunction,
      Consumer<BulkImportUpdateContainer<T>> updateFunction) {
    List<BulkImportItemExecutionResult> results = new ArrayList<>();
    bulkImportContainers.forEach(bulkImportContainer -> {
      try {
        if (bulkImportContainer.getInNameOf() != null) {
          updateByUserFunction.accept(bulkImportContainer.getInNameOf(), bulkImportContainer);
        } else {
          updateFunction.accept(bulkImportContainer);
        }

        results.add(BulkImportItemExecutionResult.builder()
            .lineNumber(bulkImportContainer.getLineNumber())
            .build());
      } catch (Exception exception) {
        results.add(BulkImportItemExecutionResult.builder()
            .lineNumber(bulkImportContainer.getLineNumber())
            .errorResponse(atlasExceptionHandler.mapToErrorResponse(exception))
            .build());
      }
    });
    return results;
  }

}
