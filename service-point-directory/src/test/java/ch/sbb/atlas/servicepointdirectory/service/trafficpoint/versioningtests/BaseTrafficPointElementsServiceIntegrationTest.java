package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.versioningtests;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.TrafficPointElementVersionBuilder;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 abstract class BaseTrafficPointElementsServiceIntegrationTest {

  @MockBean
  private CrossValidationService crossValidationService;

  protected static final String SLOID = "ch:1:sloid:123:123:123";
  protected TrafficPointElementVersionRepository trafficPointElementVersionRepository;
  protected TrafficPointElementService trafficPointElementService;

  TrafficPointElementVersion version1;
  TrafficPointElementVersion version2;
  TrafficPointElementVersion version3;
  TrafficPointElementVersion version4;
  TrafficPointElementVersion version5;

  @Autowired
   BaseTrafficPointElementsServiceIntegrationTest(TrafficPointElementVersionRepository trafficPointElementVersionRepository,
      TrafficPointElementService trafficPointElementService) {
    this.trafficPointElementService = trafficPointElementService;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @BeforeEach
  void init() {
    version1 = version1Builder().build();
    version2 = version2Builder().build();
    version3 = version3Builder().build();

    version4 = TrafficPointElementVersion.builder()
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589109))
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeich")
        .compassDirection(274.0)
        .sloid(SLOID)
        .validFrom(LocalDate.of(2025, 1, 1))
        .validTo(LocalDate.of(2025, 12, 31))
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();


    version5 = TrafficPointElementVersion.builder()
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589109))
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeich")
        .compassDirection(275.0)
        .sloid(SLOID)
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 12, 31))
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();
  }


  static TrafficPointElementVersionBuilder<?, ?> version1Builder() {
    return TrafficPointElementVersion.builder()
            .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589108))
            .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
            .designation("Bezeichnung")
            .designationOperational("Betriebliche Bezeich")
            .compassDirection(271.0)
            .sloid(SLOID)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
            .editor("fs45117");
  }

  static TrafficPointElementVersionBuilder<?, ?> version3Builder() {
    return TrafficPointElementVersion.builder()
            .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589108))
            .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
            .designation("Bezeichnung")
            .designationOperational("Betriebliche Bezeich")
            .compassDirection(273.0)
            .sloid(SLOID)
            .validFrom(LocalDate.of(2024, 1, 1))
            .validTo(LocalDate.of(2024, 12, 31))
            .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
            .editor("fs45117");
  }

  static TrafficPointElementVersionBuilder<?, ?> version2Builder() {
    return TrafficPointElementVersion.builder()
            .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589108))
            .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
            .designation("Bezeichnung")
            .designationOperational("Betriebliche Bezeich")
            .compassDirection(272.0)
            .sloid(SLOID)
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2023, 12, 31))
            .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
            .editor("fs45117");
  }

  @AfterEach
  void cleanUp() {
    List<TrafficPointElementVersion> versions = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(SLOID);
    trafficPointElementVersionRepository.deleteAll(versions);
  }

}
