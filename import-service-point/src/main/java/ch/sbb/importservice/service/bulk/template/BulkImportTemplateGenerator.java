package ch.sbb.importservice.service.bulk.template;

import static ch.sbb.importservice.service.bulk.template.ServicePointTemplateGenerator.getServicePointUpdateCsvModelExample;
import static ch.sbb.importservice.service.bulk.template.TrafficPointTemplateGenerator.getTrafficPointUpdateCsvModelExample;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.exception.BulkImportNotImplementedException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
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
    ApplicationType applicationType = Objects.requireNonNull(importConfig.getApplication(),
        "ApplicationType cannot be null");
    BusinessObjectType businessObjectType = Objects.requireNonNull(importConfig.getObjectType(),
        "BusinessObjectType cannot be null");
    ImportType importType = Objects.requireNonNull(importConfig.getImportType(),
        "ImportType cannot be null");

    return switch (applicationType) {
      case SEPODI -> switch (businessObjectType) {
        case SERVICE_POINT -> switch (importType) {
          case UPDATE -> getServicePointUpdateCsvModelExample();
          default -> throw bulkImportNotImplementedException;
        };
        case TRAFFIC_POINT -> switch (importType) {
          case UPDATE -> getTrafficPointUpdateCsvModelExample();
          default -> throw bulkImportNotImplementedException;
        };
        default -> throw bulkImportNotImplementedException;
      };
      default -> throw bulkImportNotImplementedException;
    };
  }

}
