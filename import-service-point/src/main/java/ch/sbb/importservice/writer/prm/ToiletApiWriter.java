package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.platform.ToiletImportRequestModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
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
public class ToiletApiWriter extends BaseApiWriter implements ItemWriter<ToiletCsvModelContainer> {

  @Override
  public void write(Chunk<? extends ToiletCsvModelContainer> toiletCsvModelContainers) {
    List<ToiletCsvModelContainer> toiletContainers = new ArrayList<>(toiletCsvModelContainers.getItems());
    ToiletImportRequestModel importRequestModel = new ToiletImportRequestModel();
    importRequestModel.setToiletCsvModelContainers(toiletContainers);
    Long stepExecutionId = stepExecution.getId();
    List<ItemImportResult> importResults = prmClient.importToilets(importRequestModel);
    for (ItemImportResult response : importResults) {
      saveItemProcessed(stepExecutionId, response.getItemNumber(), response.getStatus(), response.getMessage());
    }
  }
}
