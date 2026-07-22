package ch.sbb.importservice.module.bulkimport.template;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.module.bulkimport.exception.BulkImportNotImplementedException;
import ch.sbb.importservice.module.bulkimport.model.BulkImportConfig;
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
        () -> ServicePointTemplateGenerator.SERVICE_POINT_UPDATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.SERVICE_POINT)
            .importType(ImportType.CREATE)
            .build(),
        () -> ServicePointTemplateGenerator.SERVICE_POINT_CREATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.SERVICE_POINT)
            .importType(ImportType.TERMINATE)
            .build(),
        () -> ServicePointTemplateGenerator.SERVICE_POINT_TERMINATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.TRAFFIC_POINT)
            .importType(ImportType.UPDATE)
            .build(),
        () -> TrafficPointTemplateGenerator.TRAFFIC_POINT_UPDATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.TRAFFIC_POINT)
            .importType(ImportType.CREATE)
            .build(),
        () -> TrafficPointTemplateGenerator.TRAFFIC_POINT_CREATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.TRAFFIC_POINT)
            .importType(ImportType.TERMINATE)
            .build(),
        () -> TrafficPointTemplateGenerator.SLOID_TERMINATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.SECTOR)
            .importType(ImportType.CREATE)
            .build(),
        () -> SectorTemplateGenerator.SECTOR_CREATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.PRM)
            .objectType(BusinessObjectType.PLATFORM_REDUCED)
            .importType(ImportType.UPDATE)
            .build(),
        () -> PlatformTemplateGenerator.PLATFORM_REDUCED_UPDATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.PRM)
            .objectType(BusinessObjectType.PLATFORM_COMPLETE)
            .importType(ImportType.UPDATE)
            .build(),
        () -> PlatformTemplateGenerator.PLATFORM_COMPLETE_UPDATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.PRM)
            .objectType(BusinessObjectType.PLATFORM)
            .importType(ImportType.TERMINATE)
            .build(),
        () -> PlatformTemplateGenerator.SLOID_TERMINATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.PRM)
            .objectType(BusinessObjectType.STOP_POINT)
            .importType(ImportType.TERMINATE)
            .build(),
        () -> StopPointTemplateGenerator.SLOID_TERMINATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.LIDI)
            .objectType(BusinessObjectType.LINE)
            .importType(ImportType.CREATE)
            .build(),
        () -> LineTemplateGenerator.LINE_CREATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.LIDI)
            .objectType(BusinessObjectType.LINE)
            .importType(ImportType.UPDATE)
            .build(),
        () -> LineTemplateGenerator.LINE_UPDATE_CSV_MODEL
    );

    templateLookup.put(
        BulkImportConfig.builder()
            .application(ApplicationType.LIDI)
            .objectType(BusinessObjectType.SUBLINE)
            .importType(ImportType.UPDATE)
            .build(),
        () -> SublineTemplateGenerator.SUBLINE_UPDATE_CSV_MODEL
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
