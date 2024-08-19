package ch.sbb.importservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.ImportType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class BulkImportRepositoryTest {

  @Autowired
  private BulkImportRepository bulkImportRepository;

  @AfterEach
  void tearDown() {
    bulkImportRepository.deleteAll();
  }

  @Test
  void shouldSaveNewImport() {
    BulkImport bulkImportByThomasForJens = BulkImport.builder()
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .importType(ImportType.UPDATE)
        .importFileUrl("/sepodi/service_point/2024-08-08_15-10/import.csv")
        .creator("U150522") // Thomas Schäfer
        .inNameOf("U234014") // Jens Weinekötter
        .build();

    BulkImport savedBulkImport = bulkImportRepository.saveAndFlush(bulkImportByThomasForJens);
    assertThat(savedBulkImport.getId()).isNotNull();
    assertThat(savedBulkImport.getCreationDate()).isNotNull();
  }
}