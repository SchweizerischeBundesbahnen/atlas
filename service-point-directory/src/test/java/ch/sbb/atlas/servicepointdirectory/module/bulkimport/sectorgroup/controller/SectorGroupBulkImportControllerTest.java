package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.service.SectorGroupBulkImportService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SectorGroupBulkImportControllerTest {

  @Mock
  private SectorGroupBulkImportService sectorGroupBulkImportService;

  @InjectMocks
  private SectorGroupBulkImportController sectorGroupBulkImportController;

  private static SectorGroupCreateCsvModel csvModel() {
    return SectorGroupCreateCsvModel.builder()
        .trafficPointSloid("ch:1:sloid:89008:123:123")
        .sectorSloids(Set.of("ch:1:sloid:89008:123:123:1", "ch:1:sloid:89008:123:123:2"))
        .build();
  }

  @Test
  void shouldDoBulkImportViaService() {
    BulkImportUpdateContainer<SectorGroupCreateCsvModel> createContainer =
        BulkImportUpdateContainer.<SectorGroupCreateCsvModel>builder()
            .object(csvModel())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        sectorGroupBulkImportController.bulkImportCreate(List.of(createContainer));

    verify(sectorGroupBulkImportService, never()).createSectorGroupByUserName("username", createContainer);
    verify(sectorGroupBulkImportService).createSectorGroup(createContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldDoBulkImportViaServiceWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<SectorGroupCreateCsvModel> createContainer =
        BulkImportUpdateContainer.<SectorGroupCreateCsvModel>builder()
            .object(csvModel())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        sectorGroupBulkImportController.bulkImportCreate(List.of(createContainer));

    verify(sectorGroupBulkImportService).createSectorGroupByUserName(username, createContainer);
    verify(sectorGroupBulkImportService, never()).createSectorGroup(createContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

}
