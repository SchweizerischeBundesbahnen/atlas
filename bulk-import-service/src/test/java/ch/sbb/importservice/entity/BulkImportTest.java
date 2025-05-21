package ch.sbb.importservice.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BulkImportTest extends BaseValidatorTest {

  @Test
  void shouldValidateBusinessObjectTypeBasedOnApplicationSepodi() {
    BulkImport bulkImport = BulkImport.builder()
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .importType(ImportType.UPDATE)
        .creator("e123456")
        .build();

    Set<ConstraintViolation<BulkImport>> constraintViolations = validator.validate(bulkImport);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldValidateBusinessObjectTypeBasedOnApplicationPrm() {
    BulkImport bulkImport = BulkImport.builder()
        .application(ApplicationType.PRM)
        .objectType(BusinessObjectType.CONTACT_POINT)
        .importType(ImportType.UPDATE)
        .creator("e123456")
        .build();

    Set<ConstraintViolation<BulkImport>> constraintViolations = validator.validate(bulkImport);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldValidateBusinessObjectTypeBasedOnApplicationSepodiAndFail() {
    BulkImport bulkImport = BulkImport.builder()
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.STOP_POINT)
        .importType(ImportType.UPDATE)
        .creator("e123456")
        .build();

    Set<ConstraintViolation<BulkImport>> constraintViolations = validator.validate(bulkImport);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldValidateBusinessObjectTypeBasedOnApplicationPrmAndFail() {
    BulkImport bulkImport = BulkImport.builder()
        .application(ApplicationType.PRM)
        .objectType(BusinessObjectType.LOADING_POINT)
        .importType(ImportType.UPDATE)
        .creator("e123456")
        .build();

    Set<ConstraintViolation<BulkImport>> constraintViolations = validator.validate(bulkImport);
    assertThat(constraintViolations).hasSize(1);
  }
}
