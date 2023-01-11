package ch.sbb.importservice.batch;

import ch.sbb.importservice.entitiy.ImportProcessItem;
import ch.sbb.importservice.model.ServicePoint;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import ch.sbb.importservice.service.SePoDiClientService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServicePointApiWriter implements ItemWriter<ServicePoint> {

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
  public void write(List<? extends ServicePoint> servicePoints) {
    Long stepExecutionId = stepExecution.getId();
    for (ServicePoint servicePoint : servicePoints) {
      ImportProcessItem importProcessItem = ImportProcessItem.builder()
          .servicePointNumber(servicePoint.getNumber())
          .stepExecutionId(stepExecutionId)
          .build();
      importProcessedItemRepository.save(importProcessItem);
      //      sePoDiClientService.postServicePoints();
    }
  }
}
