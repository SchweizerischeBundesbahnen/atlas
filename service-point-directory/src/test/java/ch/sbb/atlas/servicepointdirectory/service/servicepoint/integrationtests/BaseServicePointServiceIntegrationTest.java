package ch.sbb.atlas.servicepointdirectory.service.servicepoint.integrationtests;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@IntegrationTest
@Transactional
public abstract class BaseServicePointServiceIntegrationTest {

    @MockBean
    private SharedBusinessOrganisationService sharedBusinessOrganisationService;

    protected static final ServicePointNumber SPN = ServicePointNumber.of(85890087);
    protected ServicePointVersionRepository versionRepository;
    protected ServicePointService servicePointService;

    protected ServicePointVersion version1;
    protected ServicePointVersion version2;
    protected ServicePointVersion version3;
    protected ServicePointVersion version4;
    protected ServicePointVersion version5;

    @Autowired
    public BaseServicePointServiceIntegrationTest(ServicePointVersionRepository versionRepository,
        ServicePointService servicePointService) {
        this.versionRepository = versionRepository;
        this.servicePointService = servicePointService;
    }

    @BeforeEach
    void init() {
        version1 = ServicePointVersion
            .builder()
            .number(SPN)
            .sloid("ch:1:sloid:89008")
            .numberShort(89008)
            .country(Country.SWITZERLAND)
            .designationOfficial("Bern, Wyleregg")
            .statusDidok3(ServicePointStatus.IN_OPERATION)
            .businessOrganisation("ch:1:sboid:100626")
            .status(Status.VALIDATED)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .categories(new HashSet<>())
            .meansOfTransport(Set.of(MeanOfTransport.BUS))
            .operatingPoint(true)
            .operatingPointWithTimetable(true)
            .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
            .editor("fs45117")
            .build();
        version2 = ServicePointVersion
            .builder()
            .number(SPN)
            .sloid("ch:1:sloid:89008")
            .numberShort(89008)
            .country(Country.SWITZERLAND)
            .designationOfficial("Bern, Thunplatz")
            .statusDidok3(ServicePointStatus.IN_OPERATION)
            .businessOrganisation("ch:1:sboid:100626")
            .status(Status.VALIDATED)
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2023, 12, 31))
            .categories(new HashSet<>())
            .meansOfTransport(Set.of(MeanOfTransport.TRAIN))
            .operatingPoint(true)
            .operatingPointWithTimetable(true)
            .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
            .editor("fs45117")
            .build();
        version3 = ServicePointVersion
            .builder()
            .number(SPN)
            .sloid("ch:1:sloid:89008")
            .numberShort(89008)
            .country(Country.SWITZERLAND)
            .designationOfficial("Bern, Eigerplatz")
            .statusDidok3(ServicePointStatus.IN_OPERATION)
            .businessOrganisation("ch:1:sboid:100626")
            .status(Status.VALIDATED)
            .validFrom(LocalDate.of(2024, 1, 1))
            .validTo(LocalDate.of(2024, 12, 31))
            .categories(new HashSet<>())
            .meansOfTransport(Set.of(MeanOfTransport.TRAM))
            .operatingPoint(true)
            .operatingPointWithTimetable(true)
            .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
            .editor("fs45117")
            .build();
        version4 = ServicePointVersion
            .builder()
            .number(SPN)
            .sloid("ch:1:sloid:89008")
            .numberShort(89008)
            .country(Country.SWITZERLAND)
            .designationOfficial("Köniz, Liebefeld")
            .statusDidok3(ServicePointStatus.IN_OPERATION)
            .businessOrganisation("ch:1:sboid:100626")
            .status(Status.VALIDATED)
            .validFrom(LocalDate.of(2025, 1, 1))
            .validTo(LocalDate.of(2025, 12, 31))
            .categories(new HashSet<>())
            .meansOfTransport(Set.of(MeanOfTransport.METRO))
            .operatingPoint(true)
            .operatingPointWithTimetable(true)
            .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
            .editor("fs45117")
            .build();
        version5 = ServicePointVersion
            .builder()
            .number(SPN)
            .sloid("ch:1:sloid:89008")
            .numberShort(89008)
            .country(Country.SWITZERLAND)
            .designationOfficial("Bern, Wankdorf")
            .statusDidok3(ServicePointStatus.IN_OPERATION)
            .businessOrganisation("ch:1:sboid:100626")
            .status(Status.VALIDATED)
            .validFrom(LocalDate.of(2026, 1, 1))
            .validTo(LocalDate.of(2026, 12, 31))
            .categories(new HashSet<>())
            .meansOfTransport(Set.of(MeanOfTransport.BOAT))
            .operatingPoint(true)
            .operatingPointWithTimetable(true)
            .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
            .editor("fs45117")
            .build();
    }

    @AfterEach
    void cleanUp() {
        List<ServicePointVersion> versionsVersioned = versionRepository.findAllByNumberOrderByValidFrom(SPN);
        versionRepository.deleteAll(versionsVersioned);
    }

}
