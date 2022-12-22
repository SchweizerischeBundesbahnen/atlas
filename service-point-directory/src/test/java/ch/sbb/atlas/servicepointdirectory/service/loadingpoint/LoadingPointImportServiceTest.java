package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointCsvModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
    System.out.println("parse all");
    List<LoadingPointCsvModel> loadingPointCsvModels = LoadingPointImportService.parseLoadingPoints(
        csvStream);

    assertThat(loadingPointCsvModels).hasSize(3019);
    LoadingPointCsvModel csvModel = loadingPointCsvModels.get(0);
    assertThat(csvModel.getServicePointNumber()).isNotNull();
    assertThat(csvModel.getDesignation()).isNotNull();
    assertThat(csvModel.getCreatedAt()).isNotNull();
    assertThat(csvModel.getCreatedBy()).isNotNull();

    // import
    System.out.println("save all");
    long start = System.currentTimeMillis();
    loadingPointImportService.importLoadingPoints(loadingPointCsvModels);
    long end = System.currentTimeMillis();
    System.out.println("Elapsed Time in milli seconds: " + (end - start));

    // get all
    assertThat(loadingPointRepository.count()).isEqualTo(3019);
    final List<LoadingPointVersion> versions = loadingPointRepository
        .findAll(Pageable.ofSize(1000)).getContent();
    assertThat(versions.size()).isEqualTo(1000);
  }
}
