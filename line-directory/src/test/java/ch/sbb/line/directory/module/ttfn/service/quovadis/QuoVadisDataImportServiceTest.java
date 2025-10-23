package ch.sbb.line.directory.module.ttfn.service.quovadis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisCsvReader.QuoVadisDataRow;
import ch.sbb.line.directory.shared.businessorganisation.entity.SharedBusinessOrganisationVersion;
import ch.sbb.line.directory.shared.businessorganisation.repository.SharedBusinessOrganisationVersionRepository;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class QuoVadisDataImportServiceTest {

  @Autowired
  private QuoVadisDataImportService quoVadisDataImportService;

  @MockitoBean
  private SharedBusinessOrganisationVersionRepository sharedBusinessOrganisationVersionRepository;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Test
  void shouldImportDataFromQuoVadisFile() {
    List<SharedBusinessOrganisationVersion> businessOrganisation = List.of(SharedBusinessOrganisationVersion.builder()
        .sboid("ch:1:sboid:123123")
        .build());
    when(sharedBusinessOrganisationVersionRepository.findByOrganisationNumber(any())).thenReturn(businessOrganisation);
    when(sharedBusinessOrganisationService.existsBySboid(any())).thenReturn(true);

    File file = new File("src/test/resources/quovadis_ttfn.csv");
    assertThatNoException().isThrownBy(() -> quoVadisDataImportService.importDataFromQuoVadis(file));
  }

  @Test
  void shouldExtractDescriptionBySplittingOnPipes() {
    QuoVadisDataRow row = new QuoVadisDataRow();
    row.setNumber("203");
    row.setDescription("Lausanne - Palézieux - Romont - Fribourg/Freiburg  | (RER Fribourg | Freiburg, Lignes S40, S41)");
    row.setRowCount("0 | 1");

    List<String> description = QuoVadisDataMapper.getDescription(row);
    assertThat(description).hasSize(2);
    assertThat(description.getFirst()).isEqualTo("Lausanne - Palézieux - Romont - Fribourg/Freiburg  ");
    assertThat(description.get(1)).isEqualTo(" (RER Fribourg | Freiburg, Lignes S40, S41)");
  }
}