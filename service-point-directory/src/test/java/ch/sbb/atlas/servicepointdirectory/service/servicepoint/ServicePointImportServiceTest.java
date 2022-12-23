package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static ch.sbb.atlas.servicepointdirectory.enumeration.Country.SWITZERLAND;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointGeolocationRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@Transactional
public class ServicePointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";

  @Autowired
  private ServicePointImportService servicePointImportService;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  private ServicePointGeolocationRepository servicePointGeolocationRepository;

  @Test
  void shouldParseServicePointCsvAndSaveInDbSuccessfully() throws IOException {
    System.out.println("delete all items");
    servicePointVersionRepository.deleteAllInBatch();
    servicePointVersionRepository.flush();
    servicePointGeolocationRepository.deleteAllInBatch();
    servicePointGeolocationRepository.flush();

    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);

    // parse csv all
    System.out.println("parse all");
    long start = System.currentTimeMillis();
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(
        csvStream);
    long end = System.currentTimeMillis();
    System.out.println("Elapsed Time in milli seconds: " + (end - start));

    assertThat(servicePointCsvModels).hasSize(359616);
    ServicePointCsvModel csvModel = servicePointCsvModels.get(0);
    assertThat(csvModel.getNummer()).isNotNull();
    assertThat(csvModel.getLaendercode()).isNotNull();
    assertThat(csvModel.getDidokCode()).isNotNull();
    assertThat(csvModel.getCreatedAt()).isNotNull();
    assertThat(csvModel.getCreatedBy()).isNotNull();

    System.out.println("save all items %d".formatted(servicePointCsvModels.size()));
    start = System.currentTimeMillis();

    List<List<ServicePointCsvModel>> subSets = Lists.partition(servicePointCsvModels, 5000);

    int i = 0;
    for (List<ServicePointCsvModel> subSet : subSets) {
      System.out.println("  ...save subSet of %d items".formatted(subSet.size()));
      servicePointImportService.importServicePoints(subSet);
      i++;
      if (i >= 20) {
        break;
      }
    }
    end = System.currentTimeMillis();
    System.out.println("Elapsed Time in milli seconds: " + (end - start));

    // get
    //assertThat(servicePointVersionRepository.count()).isEqualTo(servicePointCsvModels.size());
    System.out.println("get by number 85070003");
    final List<ServicePointVersion> savedServicePoints =
        servicePointVersionRepository.findAllByNumber(
            85070003);
    System.out.println("got records for 85070003");
    // assertThat(savedServicePoints).hasSize(6);
    ServicePointVersion savedServicePointVersion = savedServicePoints.get(0);
    assertThat(savedServicePointVersion.getId()).isNotNull();
    assertThat(savedServicePointVersion.getCountry()).isEqualTo(SWITZERLAND);
    assertThat(savedServicePointVersion.getServicePointGeolocation().getId()).isNotNull();
  }
}
