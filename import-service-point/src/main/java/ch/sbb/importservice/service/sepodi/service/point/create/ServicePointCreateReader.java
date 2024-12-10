package ch.sbb.importservice.service.sepodi.service.point.create;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.importservice.service.bulk.reader.BulkImportItemReader;
import ch.sbb.importservice.service.bulk.reader.ReaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointCreateReader extends ServicePointCreate implements BulkImportItemReader {

    @Override
    public List<BulkImportUpdateContainer<?>> apply(File file) {
        List<BulkImportUpdateContainer<ServicePointCreateCsvModel>> servicePointCreateCsvModels = ReaderUtil.readAndValidate(file,
                ServicePointCreateCsvModel.class);

        log.info("Read {} lines to import", servicePointCreateCsvModels.size());
        return new ArrayList<>(servicePointCreateCsvModels);
    }

    @Override
    public Class<?> getCsvModelClass() {
        return ServicePointCreateCsvModel.class;
    }
}
