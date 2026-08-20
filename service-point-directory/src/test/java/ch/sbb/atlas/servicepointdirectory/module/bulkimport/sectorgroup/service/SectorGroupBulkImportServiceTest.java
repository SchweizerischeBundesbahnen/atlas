package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SectorGroupBulkImportServiceTest {

  @Mock
  private SectorGroupApiClient sectorGroupApiClient;

  @InjectMocks
  private SectorGroupBulkImportService sectorGroupBulkImportService;

  @Captor
  private ArgumentCaptor<CreateSectorGroupVersionModel> createModelCaptor;

  private static BulkImportUpdateContainer<SectorGroupCreateCsvModel> container() {
    return BulkImportUpdateContainer.<SectorGroupCreateCsvModel>builder()
        .object(SectorGroupCreateCsvModel.builder()
            .trafficPointSloid("ch:1:sloid:7000:1:2")
            .validFrom(LocalDate.of(2021, 4, 1))
            .validTo(LocalDate.of(2099, 12, 31))
            .designation("AB")
            .length(35.0)
            .sectorSloids(Set.of("ch:1:sloid:7000:1:2:1", "ch:1:sloid:7000:1:2:2"))
            .build())
        .build();
  }

  @Test
  void shouldCreateSectorGroupViaApiClient() {
    sectorGroupBulkImportService.createSectorGroup(container());

    verify(sectorGroupApiClient).createSectorGroupVersion(createModelCaptor.capture());

    CreateSectorGroupVersionModel createModel = createModelCaptor.getValue();
    assertThat(createModel.getTrafficPointSloid()).isEqualTo("ch:1:sloid:7000:1:2");
    assertThat(createModel.getValidFrom()).isEqualTo(LocalDate.of(2021, 4, 1));
    assertThat(createModel.getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    assertThat(createModel.getDesignation()).isEqualTo("AB");
    assertThat(createModel.getLength()).isEqualTo(35.0);
    assertThat(createModel.getSectorSloids())
        .containsExactlyInAnyOrder("ch:1:sloid:7000:1:2:1", "ch:1:sloid:7000:1:2:2");
  }

  @Test
  void shouldCreateSectorGroupInNameOfUser() {
    sectorGroupBulkImportService.createSectorGroupByUserName("e123456", container());

    verify(sectorGroupApiClient).createSectorGroupVersion(createModelCaptor.capture());

    assertThat(createModelCaptor.getValue().getTrafficPointSloid()).isEqualTo("ch:1:sloid:7000:1:2");
  }

}
