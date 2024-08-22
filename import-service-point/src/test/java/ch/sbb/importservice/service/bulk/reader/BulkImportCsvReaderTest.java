package ch.sbb.importservice.service.bulk.reader;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.io.File;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

@IntegrationTest
class BulkImportCsvReaderTest {

  @Test
  void shouldAcceptGenericBulkImportWithFile() {
    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("service-point-update.csv")).getFile());
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdates = BulkImportCsvReader.readLinesFromFileWithNullingValue(file, ServicePointUpdateCsvModel.class);

    assertThat(servicePointUpdates).hasSize(1);
    assertThat(servicePointUpdates.getFirst().getAttributesToNull()).containsExactly("height");
    assertThat(servicePointUpdates.getFirst().getObject().getMeansOfTransport()).containsExactlyInAnyOrder(MeanOfTransport.TRAIN,
        MeanOfTransport.BUS);
  }
}