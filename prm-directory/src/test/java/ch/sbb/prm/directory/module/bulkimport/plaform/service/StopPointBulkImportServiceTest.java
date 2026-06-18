package ch.sbb.prm.directory.module.bulkimport.plaform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.module.bulkimport.service.StopPointBulkImportService;
import ch.sbb.prm.directory.module.stoppoint.StopPointTestData;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.repository.StopPointRepository;
import ch.sbb.prm.directory.security.PrmUserAdministrationService;
import ch.sbb.prm.directory.shared.servicepoint.service.SharedServicePointService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class StopPointBulkImportServiceTest {

  @MockitoBean
  private PrmUserAdministrationService prmUserAdministrationService;

  @MockitoBean
  private SharedServicePointService sharedServicePointService;

  @Autowired
  private StopPointRepository stopPointRepository;

  @Autowired
  private StopPointBulkImportService stopPointBulkImportService;

  private StopPointVersion stopPointVersion;

  @BeforeEach
  void setUp() {
    stopPointVersion = StopPointTestData.builderVersionCompleteFull().build();
    stopPointRepository.save(stopPointVersion);
    SharedServicePointVersionModel sharedServicePointVersionModel =
        SharedServicePointVersionModel.builder().servicePointSloid(stopPointVersion.getParentServicePointSloid()).build();
    doReturn(true).when(prmUserAdministrationService).hasUserRightsToCreateOrEditPrmObject(any());
    doReturn(sharedServicePointVersionModel).when(sharedServicePointService).validateServicePointExists(any());
  }

  @AfterEach
  void tearDown() {
    stopPointRepository.deleteAll();
  }

  @Test
  void shouldTerminateStopPoint() {
    String sloid = stopPointVersion.getSloid();
    LocalDate validTo = stopPointVersion.getValidTo().minusDays(10);

    stopPointBulkImportService.terminateStopPoint(BulkImportUpdateContainer.<SloidTerminateCsvModel>builder()
        .object(SloidTerminateCsvModel.builder()
            .sloid(sloid)
            .validTo(validTo)
            .build())
        .build());

    StopPointVersion stopPointVersion1 = stopPointRepository.findAllBySloidOrderByValidFrom(
        sloid).getFirst();
    assertThat(stopPointVersion1.getValidTo()).isNotNull().isEqualTo(validTo);
  }

  @Test
  void shouldTerminateStopPointElementByUsername() {
    String sloid = stopPointVersion.getSloid();
    LocalDate validTo = stopPointVersion.getValidTo().minusDays(10);

    stopPointBulkImportService.terminateStopPointByUsername("e123456",
        BulkImportUpdateContainer.<SloidTerminateCsvModel>builder()
            .object(SloidTerminateCsvModel.builder()
                .sloid(sloid)
                .validTo(validTo)
                .build())
            .build());

    StopPointVersion stopPointVersion1 = stopPointRepository.findAllBySloidOrderByValidFrom(
        sloid).getFirst();
    assertThat(stopPointVersion1.getValidTo()).isNotNull().isEqualTo(validTo);
  }
}
