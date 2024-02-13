package ch.sbb.importservice.writer.sepodi;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.importservice.writer.BaseApiWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrafficPointApiWriter extends BaseApiWriter implements ItemWriter<TrafficPointCsvModelContainer> {

  @Override
  public void write(Chunk<? extends TrafficPointCsvModelContainer> trafficPointCsvModelContainerChunk) {
    log.info("Prepared {} trafficPointCsvModelContainers to send to ServicePointDirectory",
        trafficPointCsvModelContainerChunk.size());

    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainerList = new ArrayList<>(
        trafficPointCsvModelContainerChunk.getItems());
    TrafficPointImportRequestModel trafficPointImportRequestModel = new TrafficPointImportRequestModel();
    trafficPointImportRequestModel.setTrafficPointCsvModelContainers(trafficPointCsvModelContainerList);

    Long stepExecutionId = stepExecution.getId();
    List<ItemImportResult> trafficPointItemImportResults =
        sePoDiClientService.postTrafficPoints(trafficPointImportRequestModel);

    for (ItemImportResult result : trafficPointItemImportResults) {
      saveItemProcessed(stepExecutionId, result.getItemNumber(), result.getStatus(), result.getMessage());
    }
  }

}
