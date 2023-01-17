package ch.sbb.importservice.batch;

import ch.sbb.atlas.base.service.imports.ServicePointCsvModel;
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
public class ServicePointApiWriter implements ItemWriter<ServicePointCsvModel> {

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
  public void write(List<? extends ServicePointCsvModel> servicePoints) {
    Long stepExecutionId = stepExecution.getId();
    for (ServicePointCsvModel servicePoint : servicePoints) {
      Response response = sePoDiClientService.getServicePoints(servicePoint.getDidokCode());
      ImportProcessItem importProcessItem = ImportProcessItem.builder()
          .servicePointNumber(servicePoint.getDidokCode())
          .stepExecutionId(stepExecutionId)
          .responseStatus(response.status())
          .responseMessage(response.reason())
          .build();
      importProcessedItemRepository.save(importProcessItem);
      int status = response.status();
      log.info("Response status {}", status);
    }
  }
}
