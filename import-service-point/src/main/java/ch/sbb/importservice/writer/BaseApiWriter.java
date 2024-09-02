package ch.sbb.importservice.writer;

import ch.sbb.atlas.imports.ItemImportResponseStatus;
import ch.sbb.importservice.entity.ImportProcessItem;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import ch.sbb.importservice.service.geo.ServicePointUpdateGeoLocationService;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseApiWriter {

  protected StepExecution stepExecution;

  @Autowired
  protected ServicePointUpdateGeoLocationService sePoDiClientService;

  @Autowired
  protected ImportProcessedItemRepository importProcessedItemRepository;

  @BeforeStep
  public void getStepExecutionData(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  protected void saveItemProcessed(String itemNumber, ItemImportResponseStatus status, String message) {
    ImportProcessItem importProcessItem = ImportProcessItem.builder()
        .itemNumber(itemNumber)
        .stepExecutionId(stepExecution.getId())
        .jobExecutionName(stepExecution.getJobExecution().getJobInstance().getJobName())
        .responseStatus(status)
        .responseMessage(message)
        .build();
    importProcessedItemRepository.save(importProcessItem);
  }
}