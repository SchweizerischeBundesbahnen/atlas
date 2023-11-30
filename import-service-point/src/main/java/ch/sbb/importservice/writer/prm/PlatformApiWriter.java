package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.atlas.imports.prm.platform.PlatformImportRequestModel;
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
public class PlatformApiWriter extends BaseApiWriter implements ItemWriter<PlatformCsvModelContainer> {

  @Override
  public void write(Chunk<? extends PlatformCsvModelContainer> platformCsvModelContainers) {
    List<PlatformCsvModelContainer> platformContainers = new ArrayList<>(platformCsvModelContainers.getItems());
    PlatformImportRequestModel importRequestModel = new PlatformImportRequestModel();
    importRequestModel.setPlatformCsvModelContainers(platformContainers);
    Long stepExecutionId = stepExecution.getId();
    List<ItemImportResult> importResults = prmClient.importPlatforms(importRequestModel);
    for (ItemImportResult response : importResults) {
      saveItemProcessed(stepExecutionId, response.getItemNumber(), response.getStatus(), response.getMessage());
    }
  }
}
