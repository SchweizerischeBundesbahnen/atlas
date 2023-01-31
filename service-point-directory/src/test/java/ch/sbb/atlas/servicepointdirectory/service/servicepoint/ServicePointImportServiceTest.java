package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
@Slf4j
public class ServicePointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";
  private static final String SEPARATOR = "/";

  private final ServicePointImportService servicePointImportService;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  public ServicePointImportServiceTest(ServicePointImportService servicePointImportService,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.servicePointImportService = servicePointImportService;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @Test
  void shouldParseCsvCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + CSV_FILE)) {
      List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);

      assertThat(servicePointCsvModels).isNotEmpty();
      ServicePointCsvModel firstServicePointCsvModel = servicePointCsvModels.get(0);
      assertThat(firstServicePointCsvModel.getNummer()).isNotNull();
      assertThat(firstServicePointCsvModel.getLaendercode()).isNotNull();
      assertThat(firstServicePointCsvModel.getDidokCode()).isNotNull();
      assertThat(firstServicePointCsvModel.getCreatedAt()).isNotNull();
      assertThat(firstServicePointCsvModel.getCreatedBy()).isNotNull();
    }
  }

  @Test
  void shouldParseCsvAndAllTheBooleansShouldCorrespond() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + CSV_FILE)) {
      List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);
      ServicePointCsvToEntityMapper servicePointCsvToEntityMapper = new ServicePointCsvToEntityMapper();

      List<Pair<ServicePointCsvModel, ServicePointVersion>> mappingResult = servicePointCsvModels
          .stream()
          .map(i -> Pair.of(i, servicePointCsvToEntityMapper.apply(i)))
          .toList();

      for (Pair<ServicePointCsvModel, ServicePointVersion> mappingPair : mappingResult) {
        ServicePointCsvModel csvModel = mappingPair.getFirst();
        ServicePointVersion atlasModel = mappingPair.getSecond();

        assertThat(csvModel.getIsBetriebspunkt()).isEqualTo(atlasModel.isOperatingPoint());
        assertThat(csvModel.getIsFahrplan()).isEqualTo(atlasModel.isOperatingPointWithTimetable());
        assertThat(csvModel.getIsHaltestelle()).isEqualTo(atlasModel.isStopPoint());
        assertThat(csvModel.getIsBedienpunkt()).isEqualTo(atlasModel.isFreightServicePoint());
        assertThat(csvModel.getIsVerkehrspunkt()).isEqualTo(atlasModel.isTrafficPoint());
        assertThat(csvModel.getIsGrenzpunkt()).isEqualTo(atlasModel.isBorderPoint());
      }
    }
  }
}
