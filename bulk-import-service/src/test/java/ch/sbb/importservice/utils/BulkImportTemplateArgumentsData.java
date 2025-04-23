package ch.sbb.importservice.utils;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;

@UtilityClass
public class BulkImportTemplateArgumentsData {

  private static final List<Arguments> IMPLEMENTED_TEMPLATES_LIST = List.of(
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.SERVICE_POINT, ImportType.CREATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.SERVICE_POINT, ImportType.UPDATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.TRAFFIC_POINT, ImportType.CREATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.TRAFFIC_POINT, ImportType.UPDATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.PRM, BusinessObjectType.PLATFORM_REDUCED, ImportType.UPDATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.LIDI, BusinessObjectType.LINE, ImportType.UPDATE))
  );

  public static Stream<Arguments> implementedTemplates() {
    return IMPLEMENTED_TEMPLATES_LIST.stream();
  }

  private static final List<Arguments> NOT_IMPLEMENTED_TEMPLATES_LIST = List.of(
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.SERVICE_POINT, ImportType.TERMINATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.TRAFFIC_POINT, ImportType.TERMINATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.LOADING_POINT, ImportType.CREATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.LOADING_POINT, ImportType.UPDATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.LOADING_POINT, ImportType.TERMINATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.LIDI, BusinessObjectType.LINE, ImportType.CREATE)),
      Arguments.of(new BulkImportConfig(ApplicationType.LIDI, BusinessObjectType.LINE, ImportType.TERMINATE))
  );

  public static Stream<Arguments> notImplementedTemplates() {
    return NOT_IMPLEMENTED_TEMPLATES_LIST.stream();
  }
}
