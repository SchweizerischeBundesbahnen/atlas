package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.CountryTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.UicCountry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
public class ServicePointVersionRepositoryTest {

    private final ServicePointVersionRepository servicePointVersionRepository;
    private final UicCountryRepository uicCountryRepository;
    private UicCountry switzerland;

    @Autowired
    public ServicePointVersionRepositoryTest(ServicePointVersionRepository servicePointVersionRepository, UicCountryRepository uicCountryRepository) {
        this.servicePointVersionRepository = servicePointVersionRepository;
        this.uicCountryRepository = uicCountryRepository;
    }

    @BeforeEach
    void setUp() {
        switzerland = uicCountryRepository.save(CountryTestData.SWITZERLAND);
    }

    @AfterEach
    void tearDown() {
        servicePointVersionRepository.deleteAll();
        uicCountryRepository.deleteAll();
    }

    @Test
    void shouldSaveServicePointVersionWithUicCountry() {
        // given
        ServicePointVersion servicePoint = ServicePointVersion.builder()
                .number(1)
                .checkDigit(1)
                .numberShort(1)
                .uicCountry(switzerland)
                .designationLong("long designation")
                .designationOfficial("official designation")
                .abbreviation("BE")
                .statusDidok3(1)
                .businessOrganisation("somesboid")
                .hasGeolocation(true)
                .status(Status.VALIDATED)
                .validFrom(LocalDate.of(2020,1,1))
                .validTo(LocalDate.of(2020,12,31))
                .build();

        // when
        ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

        // then
        assertThat(savedVersion.getId()).isNotNull();
        assertThat(savedVersion.getServicePointGeolocation()).isNull();
    }

    @Test
    void shouldSaveServicePointVersionWithGeolocation() {
        // given
        ServicePointGeolocation servicePointGeolocation = ServicePointGeolocation.builder()
                .source_spatial_ref(1)
                .lv03east(600037.945)
                .lv03north(199749.812)
                .lv95east(2600037.945)
                .lv95north(1199749.812)
                .wgs84east(7.43913089)
                .wgs84north(46.94883229)
                .height(540.2)
                .isoCountryCode(switzerland.getIsoCode())
                .swissCantonFsoNumber(5)
                .swissCantonName("Bern")
                .swissCantonNumber(5)
                .swissDistrictName("Bern")
                .swissDistrictNumber(5)
                .swissMunicipalityName("Bern")
                .swissLocalityName("Bern")
                .build();

        ServicePointVersion servicePoint = ServicePointVersion.builder()
                .number(1)
                .checkDigit(1)
                .numberShort(1)
                .uicCountry(switzerland)
                .designationLong("long designation")
                .designationOfficial("official designation")
                .abbreviation("BE")
                .statusDidok3(1)
                .businessOrganisation("somesboid")
                .hasGeolocation(true)
                .status(Status.VALIDATED)
                .validFrom(LocalDate.of(2020,1,1))
                .validTo(LocalDate.of(2020,12,31))
                .servicePointGeolocation(servicePointGeolocation)
                .build();

        servicePointGeolocation.setServicePointVersion(servicePoint);

        // when
        ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

        // then
        assertThat(savedVersion.getId()).isNotNull();
        assertThat(savedVersion.getServicePointGeolocation()).isNotNull();
        assertThat(savedVersion.getServicePointGeolocation().getSwissCantonName()).isEqualTo("Bern");
    }
}