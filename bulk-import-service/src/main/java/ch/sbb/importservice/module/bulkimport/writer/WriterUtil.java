package ch.sbb.importservice.module.bulkimport.writer;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.importservice.module.bulkimport.entity.BulkImport;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.Chunk;

@UtilityClass
public class WriterUtil {

  public static List<BulkImportUpdateContainer<?>> getContainersWithoutDataValidationErrors(
      Chunk<BulkImportUpdateContainer<?>> items) {
    return items.getItems().stream().filter(i -> !i.hasDataValidationErrors()).toList();
  }

  public static <T> List<BulkImportUpdateContainer<T>> getContainers(List<BulkImportUpdateContainer<?>> items) {
    return items.stream().map(i -> (BulkImportUpdateContainer<T>) i).toList();
  }

  public static <T> void mapExecutionResultToLogEntry(List<BulkImportItemExecutionResult> executionResults,
      List<BulkImportUpdateContainer<T>> containers) {
    containers.forEach(updateContainer -> {
      BulkImportItemExecutionResult correspondingResult = executionResults.stream()
          .filter(i -> i.getLineNumber() == updateContainer.getLineNumber()).findFirst().orElseThrow();
      updateContainer.setBulkImportLogEntry(BulkImportLogEntry.builder()
          .lineNumber(updateContainer.getLineNumber())
          .status(correspondingResult.isSuccess() ? BulkImportStatus.SUCCESS :
              correspondingResult.isInfo() ? BulkImportStatus.INFO : BulkImportStatus.DATA_EXECUTION_ERROR)
          .errors(correspondingResult.getErrors())
          .build());
    });
  }

  public static void addInNameOfTo(StepExecution stepExecution, List<BulkImportUpdateContainer<?>> updateContainers) {
    String inNameOf = stepExecution.getJobExecution().getJobParameters().getString(BulkImport.Fields.inNameOf);
    updateContainers.forEach(updateContainer -> updateContainer.setInNameOf(inNameOf));
  }
}
