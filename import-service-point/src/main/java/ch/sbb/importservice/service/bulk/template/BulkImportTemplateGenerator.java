package ch.sbb.importservice.service.bulk.template;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.exception.BulkImportNotImplementedException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BulkImportTemplateGenerator {

  private static final Map<BulkImportConfig, Supplier<Object>> templateLookup = new HashMap<>();

  static {
    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.SERVICE_POINT)
            .importType(ImportType.UPDATE)
            .build(),
        ServicePointTemplateGenerator::getServicePointUpdateCsvModelExample
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.TRAFFIC_POINT)
            .importType(ImportType.UPDATE)
            .build(),
        TrafficPointTemplateGenerator::getTrafficPointUpdateCsvModelExample
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.PRM)
            .objectType(BusinessObjectType.PLATFORM)
            .importType(ImportType.UPDATE)
            .build(),
        PlatformTemplateGenerator::getPlatformUpdateCsvModelExample
    );
  }

  public static final String CSV_EXTENSION = ".csv";

  private final FileService fileService;

  public File generateCsvTemplate(BulkImportConfig importConfig) {
    Object example = bulkImportExample(importConfig);
    File csvFile = new File(fileService.getDir() + importConfig.getTemplateFileName());
    ObjectWriter objectWriter = new TemplateCsvMapper(example.getClass()).getObjectWriter();
    return CsvExportWriter.writeToFile(csvFile, List.of(example), objectWriter);
  }

  public Object bulkImportExample(BulkImportConfig importConfig) {
    Supplier<Object> templateGeneratorMethod = templateLookup.get(importConfig);
    if (templateGeneratorMethod != null) {
      return templateGeneratorMethod.get();
    } else {
      throw new BulkImportNotImplementedException(importConfig);
    }
  }

}
