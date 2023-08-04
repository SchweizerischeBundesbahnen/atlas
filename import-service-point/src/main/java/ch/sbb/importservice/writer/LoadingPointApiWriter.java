package ch.sbb.importservice.writer;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
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
public class LoadingPointApiWriter extends BaseApiWriter implements ItemWriter<LoadingPointCsvModel> {

  @Override
  public void write(Chunk<? extends LoadingPointCsvModel> loadingPointCsvModelChunk) {
    log.info("Prepared {} loadingPointCsvModels to send to ServicePointDirectory", loadingPointCsvModelChunk.size());

    List<LoadingPointCsvModel> loadingPointCsvModelList = new ArrayList<>(loadingPointCsvModelChunk.getItems());
    LoadingPointImportRequestModel loadingPointImportRequestModel = new LoadingPointImportRequestModel(loadingPointCsvModelList);

    Long stepExecutionId = stepExecution.getId();
    List<LoadingPointItemImportResult> loadingPointItemImportResults = sePoDiClientService.postLoadingPoints(
        loadingPointImportRequestModel);

    for (LoadingPointItemImportResult result : loadingPointItemImportResults) {
      saveItemProcessed(stepExecutionId, result.getItemNumber(), result.getStatus(), result.getMessage());
    }
  }

}
