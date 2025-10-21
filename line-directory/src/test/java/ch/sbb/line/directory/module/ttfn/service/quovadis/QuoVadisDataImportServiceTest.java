package ch.sbb.line.directory.module.ttfn.service.quovadis;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisCsvReader.QuoVadisDataRow;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class QuoVadisDataImportServiceTest {

  @Autowired
  private QuoVadisDataImportService quoVadisDataImportService;

  @Test
  void importDataFromQuoVadis() throws IOException {
    File file = new File("src/test/resources/quovadis_ttfn.csv");
    quoVadisDataImportService.importDataFromQuoVadis(file);
  }

  @Test
  void shouldBastelDescription() {
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