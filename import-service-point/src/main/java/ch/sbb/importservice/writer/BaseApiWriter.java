package ch.sbb.importservice.writer;

import ch.sbb.atlas.imports.servicepoint.model.ItemImportResponseStatus;
import ch.sbb.importservice.entitiy.ImportProcessItem;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import ch.sbb.importservice.service.SePoDiClientService;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseApiWriter {

  protected StepExecution stepExecution;

  @Autowired
  protected SePoDiClientService sePoDiClientService;

  @Autowired
  private ImportProcessedItemRepository importProcessedItemRepository;

  @BeforeStep
  public void getStepExecutionData(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  protected void saveItemProcessed(Long stepExecutionId, Integer number, ItemImportResponseStatus status, String message) {
    ImportProcessItem importProcessItem = ImportProcessItem.builder()
        .itemNumber(number)
        .stepExecutionId(stepExecutionId)
        .jobExecutionName(stepExecution.getJobExecution().getJobInstance().getJobName())
        .responseStatus(status)
        .responseMessage(message)
        .build();
    importProcessedItemRepository.save(importProcessItem);
  }

}
