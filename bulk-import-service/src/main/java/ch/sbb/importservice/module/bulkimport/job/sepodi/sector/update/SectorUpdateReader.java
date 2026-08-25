package ch.sbb.importservice.module.bulkimport.job.sepodi.sector.update;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel;
import ch.sbb.importservice.module.bulkimport.reader.BulkImportItemReader;
import ch.sbb.importservice.module.bulkimport.reader.ReaderUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectorUpdateReader extends SectorUpdate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<SectorUpdateCsvModel>> sectorUpdateCsvModels = ReaderUtil.readAndValidate(file,
        SectorUpdateCsvModel.class);
    return new ArrayList<>(sectorUpdateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return SectorUpdateCsvModel.class;
  }

}
