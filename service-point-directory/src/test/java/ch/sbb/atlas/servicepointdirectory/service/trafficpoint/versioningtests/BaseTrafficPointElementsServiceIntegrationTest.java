package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.versioningtests;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@IntegrationTest
@Transactional
public class BaseTrafficPointElementsServiceIntegrationTest {

    protected static final String SLOID = "ch:1:sloid:123";
    protected TrafficPointElementVersionRepository trafficPointElementVersionRepository;
    protected TrafficPointElementService trafficPointElementService;

    TrafficPointElementVersion version1;
    TrafficPointElementVersion version2;
    TrafficPointElementVersion version3;
    TrafficPointElementVersion version4;
    TrafficPointElementVersion version5;

    @Autowired
    public BaseTrafficPointElementsServiceIntegrationTest(TrafficPointElementVersionRepository trafficPointElementVersionRepository,
                                                  TrafficPointElementService trafficPointElementService) {
        this.trafficPointElementService = trafficPointElementService;
        this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
    }

    @BeforeEach
    void init() {
        version1 = TrafficPointElementVersion.builder()
                .servicePointNumber(ServicePointNumber.of(85891087))
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
                .editor("fs45117")
                .build();

        version2 = TrafficPointElementVersion.builder()
                .servicePointNumber(ServicePointNumber.of(85891088))
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
                .editor("fs45117")
                .build();

        version3 = TrafficPointElementVersion.builder()
                .servicePointNumber(ServicePointNumber.of(85891089))
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
                .editor("fs45117")
                .build();

        version4 = TrafficPointElementVersion.builder()
                .servicePointNumber(ServicePointNumber.of(85891090))
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
                .servicePointNumber(ServicePointNumber.of(85891091))
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

    @AfterEach
    void cleanUp() {
        List<TrafficPointElementVersion> versions = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(SLOID);
        trafficPointElementVersionRepository.deleteAll(versions);
    }

}
