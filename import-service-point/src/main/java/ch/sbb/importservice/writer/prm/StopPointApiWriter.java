package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointImportRequestModel;
import ch.sbb.importservice.writer.BaseApiWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@StepScope
public class StopPointApiWriter extends BaseApiWriter implements ItemWriter<StopPointCsvModelContainer> {

  @Override
  public void write(Chunk<? extends StopPointCsvModelContainer> stopPointCsvModelContainers) {
    List<StopPointCsvModelContainer> stopPointCsvModels = new ArrayList<>(stopPointCsvModelContainers.getItems());
    StopPointImportRequestModel importRequestModel = new StopPointImportRequestModel();
    importRequestModel.setStopPointCsvModelContainers(stopPointCsvModels);
    Long stepExecutionId = stepExecution.getId();
    List<ItemImportResult> importResults = prmClient.postStopPointImport(importRequestModel);
    for (ItemImportResult response : importResults) {
      saveItemProcessed(stepExecutionId, response.getItemNumber(), response.getStatus(), response.getMessage());
    }
  }
}
