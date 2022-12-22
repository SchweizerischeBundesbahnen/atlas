package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LoadingPointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_LADESTELLEN_20221222011259.csv";

  @Autowired
  private LoadingPointImportService loadingPointImportService;

  @Autowired
  private LoadingPointRepository loadingPointRepository;

  @Test
  void shouldParseLoadingPointCsvAndSaveInDbSuccessfully() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<LoadingPointCsvModel> loadingPointCsvModels = LoadingPointImportService.parseLoadingPoints(
        csvStream);

    assertThat(loadingPointCsvModels).hasSize(3019);

    // delete all
    loadingPointRepository.deleteAll();

    // import
    loadingPointImportService.importLoadingPoints(loadingPointCsvModels);

    // get all
    assertThat(loadingPointRepository.count()).isEqualTo(3019);
  }
}
