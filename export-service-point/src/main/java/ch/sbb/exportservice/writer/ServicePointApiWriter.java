package ch.sbb.exportservice.writer;

import ch.sbb.atlas.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
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
public class ServicePointApiWriter extends BaseApiWriter implements ItemWriter<ServicePointCsvModelContainer> {

  @Override
  public void write(Chunk<? extends ServicePointCsvModelContainer> servicePoints) {
    // TODO: create and send CSV and JSON to S3 Bucket

    List<ServicePointCsvModelContainer> servicePointCsvModels = new ArrayList<>(servicePoints.getItems());
    ServicePointImportReqModel servicePointImportReqModel = new ServicePointImportReqModel();
    servicePointImportReqModel.setServicePointCsvModelContainers(servicePointCsvModels);
    Long stepExecutionId = stepExecution.getId();
  }
}
