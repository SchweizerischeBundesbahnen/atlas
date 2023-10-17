package ch.sbb.importservice.writer.prm.stopplace;

import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModelContainer;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
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
public class StopPlaceApiWriter extends BaseApiWriter implements ItemWriter<StopPlaceCsvModelContainer> {

  @Override
  public void write(Chunk<? extends StopPlaceCsvModelContainer> stopPlaceCsvModelContainers) {
    List<StopPlaceCsvModelContainer> stopPlaceCsvModels = new ArrayList<>(stopPlaceCsvModelContainers.getItems());
    StopPlaceImportRequestModel importRequestModel = new StopPlaceImportRequestModel();
    importRequestModel.setStopPlaceCsvModelContainers(stopPlaceCsvModels);
    Long stepExecutionId = stepExecution.getId();
    List<ItemImportResult> importResults = prmClient.postStopPlacesImport(importRequestModel);
    for (ItemImportResult response : importResults) {
      saveItemProcessed(stepExecutionId, response.getItemNumber().toString(), response.getStatus(), response.getMessage());
    }
  }
}
