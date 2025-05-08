package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.configuration.handler.ErrorResponseMapper;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BaseBulkImportControllerInternal {

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
        log.error("Data Execution Error! bulkImportId={} lineNumber={} Mapping Exception",
            bulkImportContainer.getBulkImportId(), bulkImportContainer.getLineNumber(), exception);
        results.add(BulkImportItemExecutionResult.builder()
            .lineNumber(bulkImportContainer.getLineNumber())
            .errorResponse(ErrorResponseMapper.mapToErrorResponse(exception))
            .build());
      }
    });
    return results;
  }

}
