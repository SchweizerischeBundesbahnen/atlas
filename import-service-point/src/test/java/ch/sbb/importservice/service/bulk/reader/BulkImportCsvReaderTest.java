package ch.sbb.importservice.service.bulk.reader;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

@IntegrationTest
class BulkImportCsvReaderTest {

  @Test
  void shouldReadServicePointUpdateCsvCorrectlyWithNullingAndPipedSet() {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.csv");

    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdates =
        BulkImportCsvReader.readLinesFromFileWithNullingValue(
        file, ServicePointUpdateCsvModel.class);

    assertThat(servicePointUpdates).hasSize(1);
    assertThat(servicePointUpdates.getFirst().getAttributesToNull()).containsExactly("height");

    ServicePointUpdateCsvModel expected = ImportFiles.getExpectedServicePointUpdateCsvModel();
    ServicePointUpdateCsvModel actual = servicePointUpdates.getFirst().getObject();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }
}