package ch.sbb.importservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import org.junit.jupiter.api.Test;

class BulkImportConfigTest {

  @Test
  void shouldGetTemplateFileName() {
    //given
    BulkImportConfig config = BulkImportConfig.builder()
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .importType(ImportType.CREATE)
        .build();
    //when
    String result = config.getTemplateFileName();

    //then
    assertThat(result).isNotNull().isEqualTo("SEPODI_SERVICE_POINT_CREATE.csv");

  }

}