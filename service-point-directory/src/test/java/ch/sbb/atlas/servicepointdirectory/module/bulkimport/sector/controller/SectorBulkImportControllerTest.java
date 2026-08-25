package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sector.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.SectorCreateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.sector.service.SectorBulkImportService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SectorBulkImportControllerTest {

  @Mock
  private SectorBulkImportService sectorBulkImportService;

  @InjectMocks
  private SectorBulkImportController sectorBulkImportController;

  @Test
  void shouldDoBulkImportViaService() {
    BulkImportUpdateContainer<SectorCreateCsvModel> updateContainer =
        BulkImportUpdateContainer.<SectorCreateCsvModel>builder()
            .object(SectorCreateCsvModel.builder()
                .trafficPointSloid("ch:1:sloid:89008:123:123")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        sectorBulkImportController.bulkImportCreate(List.of(updateContainer));

    verify(sectorBulkImportService, never()).createSectorByUserName("username", updateContainer);
    verify(sectorBulkImportService).createSector(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldDoBulkUpdateViaServiceWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<SectorCreateCsvModel> updateContainer =
        BulkImportUpdateContainer.<SectorCreateCsvModel>builder()
            .object(SectorCreateCsvModel.builder()
                .trafficPointSloid("ch:1:sloid:89008:123:123")
                .build())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        sectorBulkImportController.bulkImportCreate(List.of(updateContainer));

    verify(sectorBulkImportService).createSectorByUserName(username, updateContainer);
    verify(sectorBulkImportService, never()).createSector(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldDoBulkImportUpdateViaService() {
    BulkImportUpdateContainer<SectorUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<SectorUpdateCsvModel>builder()
            .object(SectorUpdateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123:1")
                .build())
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        sectorBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(sectorBulkImportService, never()).updateSectorByUserName("username", updateContainer);
    verify(sectorBulkImportService).updateSector(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

  @Test
  void shouldDoBulkImportUpdateViaServiceWithUsername() {
    String username = "e123456";
    BulkImportUpdateContainer<SectorUpdateCsvModel> updateContainer =
        BulkImportUpdateContainer.<SectorUpdateCsvModel>builder()
            .object(SectorUpdateCsvModel.builder()
                .sloid("ch:1:sloid:89008:123:123:1")
                .build())
            .inNameOf(username)
            .build();

    List<BulkImportItemExecutionResult> bulkImportItemExecutionResults =
        sectorBulkImportController.bulkImportUpdate(List.of(updateContainer));

    verify(sectorBulkImportService).updateSectorByUserName(username, updateContainer);
    verify(sectorBulkImportService, never()).updateSector(updateContainer);
    assertThat(bulkImportItemExecutionResults).hasSize(1).first()
        .extracting(BulkImportItemExecutionResult::isSuccess).isEqualTo(true);
  }

}