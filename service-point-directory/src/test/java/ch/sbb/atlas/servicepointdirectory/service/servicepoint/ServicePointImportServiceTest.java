package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
@Slf4j
public class ServicePointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";
  private static final String SEPARATOR = "/";

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private final ServicePointImportService servicePointImportService;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  public ServicePointImportServiceTest(ServicePointImportService servicePointImportService,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.servicePointImportService = servicePointImportService;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @Test
  public void shouldImportServicePoints() {
    //given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = getServicePointCsvModelContainers();
    Integer didokCode = servicePointCsvModelContainers.get(0).getDidokCode();
    ServicePointNumber servicePointNumber = ServicePointNumber.of(didokCode);
    //when
    List<ServicePointItemImportResult> servicePointItemImportResults = servicePointImportService.importServicePoints(
        servicePointCsvModelContainers);

    //then
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
    assertThat(result).isNotNull();
    assertThat(servicePointItemImportResults).hasSize(5);
    assertThat(result).hasSize(3);
    for (ServicePointVersion servicePointVerion : result) {
      assertThat(servicePointVerion.getNumber()).isNotNull();
      assertThat(servicePointVerion.getNumber()).isEqualTo(servicePointNumber);
    }
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

  private List<ServicePointCsvModelContainer> getServicePointCsvModelContainers() {
    ServicePointTestData.getBernWyleregg();
    int didokCode = 80187710;
    ServicePointCsvModel withGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .spatialReference(SpatialReference.LV95)
        .bezeichnungLang("Bern, Wyleregg")
        .bezeichnungOffiziell("Bern, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(1)
        .abkuerzung("a")
        .didokCode(didokCode)
        .build();
    ServicePointCsvModel notVirtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .abkuerzung("b")
        .bezeichnungLang("Bern, Wyleregg")
        .bezeichnungOffiziell("Bern, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(1)
        .didokCode(didokCode)
        .build();
    ServicePointCsvModel virtualWithoutGeolocation = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .abkuerzung("c")
        .bezeichnungLang("Bern, Wyleregg")
        .bezeichnungOffiziell("Bern, Wyleregg")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(1)
        .didokCode(didokCode)
        .build();
    ServicePointCsvModel virtualWithoutGeolocation2 = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .abkuerzung("c")
        .bezeichnungLang("Bern, Wankdorf")
        .bezeichnungOffiziell("Bern, Wankdorf")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(1)
        .didokCode(didokCode)
        .build();
    ServicePointCsvModel virtualWithoutGeolocation3 = ServicePointCsvModel.builder()
        .validFrom(LocalDate.of(2002, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .abkuerzung("c")
        .bezeichnungLang("Bern, Wankdorf")
        .bezeichnungOffiziell("Bern, Wankdorf")
        .isBedienpunkt(true)
        .isBetriebspunkt(true)
        .isFahrplan(true)
        .nummer(didokCode)
        .laendercode(80)
        .status(1)
        .didokCode(didokCode)
        .build();
    List<ServicePointCsvModel> modelList = new ArrayList<>();
    modelList.add(withGeolocation);
    modelList.add(notVirtualWithoutGeolocation);
    modelList.add(virtualWithoutGeolocation);
    modelList.add(virtualWithoutGeolocation2);
    modelList.add(virtualWithoutGeolocation3);
    modelList.sort(Comparator.comparing(ServicePointCsvModel::getValidFrom));
    ServicePointCsvModelContainer container = new ServicePointCsvModelContainer();
    container.setServicePointCsvModelList(modelList);
    container.setDidokCode(didokCode);
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    servicePointCsvModelContainers.add(container);
    return servicePointCsvModelContainers;
  }
}
