package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.importservice.service.bulk.BulkImportValidationService;
import ch.sbb.importservice.service.bulk.reader.BulkImportItemReader;
import ch.sbb.importservice.service.bulk.reader.ReaderUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointUpdateReader extends ServicePointUpdate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdateCsvModels = ReaderUtil.readAndValidate(file,
        ServicePointUpdateCsvModel.class);

    BulkImportValidationService.validateUniqueness(servicePointUpdateCsvModels, ServicePointUpdateCsvModel::getSloid, "sloid");
    BulkImportValidationService.validateUniqueness(servicePointUpdateCsvModels, ServicePointUpdateCsvModel::getNumber, "number");

    log.info("Read {} lines to import", servicePointUpdateCsvModels.size());
    return new ArrayList<>(servicePointUpdateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return ServicePointUpdateCsvModel.class;
  }

}
