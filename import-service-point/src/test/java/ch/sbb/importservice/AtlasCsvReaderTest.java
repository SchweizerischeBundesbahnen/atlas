package ch.sbb.importservice;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.AtlasCsvReader;
import ch.sbb.atlas.imports.BulkImportUpdateContainer;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.sepodi.service.point.update.ServicePointUpdateCsvModel;
import java.io.File;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

@IntegrationTest
class AtlasCsvReaderTest {

  @Test
  void shouldAcceptGenericBulkImportWithFile() {
    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("service-point-update.csv")).getFile());
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdates = AtlasCsvReader.readLinesFromFileWithNullingValue(file, ServicePointUpdateCsvModel.class);

    assertThat(servicePointUpdates).hasSize(1);
    assertThat(servicePointUpdates.getFirst().getAttributesToNull()).containsExactly("height");
  }
}