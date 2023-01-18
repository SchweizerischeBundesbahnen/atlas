package ch.sbb.importservice.batch;

import ch.sbb.atlas.base.service.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.importservice.entitiy.ImportProcessItem;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import ch.sbb.importservice.service.SePoDiClientService;
import feign.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoadingPointApiWriter implements ItemWriter<LoadingPointCsvModel> {

  @Autowired
  private SePoDiClientService sePoDiClientService;

  @Autowired
  private ImportProcessedItemRepository importProcessedItemRepository;

  private StepExecution stepExecution;

  @BeforeStep
  public void getStepExecutionData(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  @Override
  public void write(List<? extends LoadingPointCsvModel> loadingPointCsvModels) {
    Long stepExecutionId = stepExecution.getId();
    for (LoadingPointCsvModel loadingPointCsvModel : loadingPointCsvModels) {
      Response response = sePoDiClientService.getServicePoints(loadingPointCsvModel.getNumber());
      ImportProcessItem importProcessItem = ImportProcessItem.builder()
          .itemNumber(loadingPointCsvModel.getNumber())
          .stepExecutionId(stepExecutionId)
          .jobExecutionName(stepExecution.getJobExecution().getJobInstance().getJobName())
          .responseStatus(response.status())
          .responseMessage(response.reason())
          .build();
      importProcessedItemRepository.save(importProcessItem);
      int status = response.status();
      log.info("Response status {}", status);
    }
  }
}
