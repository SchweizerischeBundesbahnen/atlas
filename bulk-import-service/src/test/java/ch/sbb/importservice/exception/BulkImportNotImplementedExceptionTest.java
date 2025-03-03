package ch.sbb.importservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import org.junit.jupiter.api.Test;

class BulkImportNotImplementedExceptionTest {

  @Test
  void shouldRespondWithScenarioNotImplemented() {
    BulkImportNotImplementedException exception = new BulkImportNotImplementedException(
        BulkImportConfig.builder()
            .application(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.SERVICE_POINT)
            .importType(ImportType.CREATE)
            .build());
    assertThat(exception.getMessage()).isEqualTo("BulkImport Scenario BulkImportConfig(application=SEPODI, "
        + "objectType=SERVICE_POINT, importType=CREATE) not implemented yet");
  }
}