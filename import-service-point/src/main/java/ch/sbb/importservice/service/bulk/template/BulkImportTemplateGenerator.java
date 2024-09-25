package ch.sbb.importservice.service.bulk.template;

import static ch.sbb.importservice.service.bulk.template.ServicePointTemplateGenerator.getServicePointCsvTemplate;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.exception.BulkImportNotImplementedException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BusinessObjectType;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BulkImportTemplateGenerator {

  public static final String CSV_EXTENSION = ".csv";

  private final FileService fileService;

  public File generateCsvTemplate(BulkImportConfig importConfig) {
    Object example = bulkImportExample(importConfig);
    File csvFile = new File(fileService.getDir() + importConfig.getTemplateFileName());
    ObjectWriter objectWriter = new TemplateCsvMapper(example.getClass()).getObjectWriter();
    return CsvExportWriter.writeToFileWithoutOrderMark(csvFile, List.of(example), objectWriter);
  }

  private Object bulkImportExample(BulkImportConfig importConfig) {

    BulkImportNotImplementedException bulkImportNotImplementedException = new BulkImportNotImplementedException(importConfig);

    if (Objects.requireNonNull(importConfig.getApplication()) == ApplicationType.SEPODI) {
      if (Objects.requireNonNull(importConfig.getObjectType()) == BusinessObjectType.SERVICE_POINT) {
        return getServicePointCsvTemplate(importConfig);
      }
      throw bulkImportNotImplementedException;
    }
    throw bulkImportNotImplementedException;
  }

}
