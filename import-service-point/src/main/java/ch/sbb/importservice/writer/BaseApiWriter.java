package ch.sbb.importservice.writer;

import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.entity.ImportProcessItem;
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
  protected PrmClient prmClient;

  @Autowired
  private ImportProcessedItemRepository importProcessedItemRepository;

  @BeforeStep
  public void getStepExecutionData(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  protected void saveItemProcessed(Long stepExecutionId, String itemNumber, ItemImportResponseStatus status, String message) {
    ImportProcessItem importProcessItem = ImportProcessItem.builder()
        .itemNumber(itemNumber)
        .stepExecutionId(stepExecutionId)
        .jobExecutionName(stepExecution.getJobExecution().getJobInstance().getJobName())
        .responseStatus(status)
        .responseMessage(message)
        .build();
    importProcessedItemRepository.save(importProcessItem);
  }

}
