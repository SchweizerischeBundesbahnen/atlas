package ch.sbb.importservice.module.bulkimport.job.sepodi.sectorgroup.create;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;
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
public class SectorGroupCreateReader extends SectorGroupCreate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<SectorGroupCreateCsvModel>> sectorGroupCreateCsvModels = ReaderUtil.readAndValidate(file,
        SectorGroupCreateCsvModel.class);

    log.info("Read {} sector groups to import", sectorGroupCreateCsvModels.size());
    return new ArrayList<>(sectorGroupCreateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return SectorGroupCreateCsvModel.class;
  }

}
