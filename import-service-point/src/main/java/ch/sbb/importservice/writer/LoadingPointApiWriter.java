package ch.sbb.importservice.writer;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointItemImportResult;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoadingPointApiWriter extends BaseApiWriter implements ItemWriter<LoadingPointCsvModelContainer> {

  @Override
  public void write(Chunk<? extends LoadingPointCsvModelContainer> loadingPointCsvModelContainerChunk) {
    log.info("Prepared {} loadingPointCsvModelContainers to send to ServicePointDirectory",
        loadingPointCsvModelContainerChunk.size());

    List<LoadingPointCsvModelContainer> loadingPointCsvModelContainerList = new ArrayList<>(
        loadingPointCsvModelContainerChunk.getItems());
    LoadingPointImportRequestModel loadingPointImportRequestModel = new LoadingPointImportRequestModel(
        loadingPointCsvModelContainerList);

    Long stepExecutionId = stepExecution.getId();
    List<LoadingPointItemImportResult> loadingPointItemImportResults = sePoDiClientService.postLoadingPoints(
        loadingPointImportRequestModel);

    for (LoadingPointItemImportResult result : loadingPointItemImportResults) {
      saveItemProcessed(stepExecutionId, result.getItemNumber(), result.getStatus(), result.getMessage());
    }
  }

}
