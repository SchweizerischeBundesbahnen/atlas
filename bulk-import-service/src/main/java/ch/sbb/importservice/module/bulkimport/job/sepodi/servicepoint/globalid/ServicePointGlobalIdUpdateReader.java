package ch.sbb.importservice.module.bulkimport.job.sepodi.servicepoint.globalid;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel;
import ch.sbb.importservice.module.bulkimport.reader.BulkImportItemReader;
import ch.sbb.importservice.module.bulkimport.reader.ReaderUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointGlobalIdUpdateReader extends ServicePointGlobalIdUpdate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel>> globalIdUpdateCsvModels = ReaderUtil.readAndValidate(
        file, ServicePointGlobalIdUpdateCsvModel.class);

    log.info("Read {} lines to import", globalIdUpdateCsvModels.size());
    return new ArrayList<>(globalIdUpdateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return ServicePointGlobalIdUpdateCsvModel.class;
  }

}
