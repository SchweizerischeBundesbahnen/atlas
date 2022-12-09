package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.UicCountry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
public class ServicePointVersionRepositoryTest {

    private final ServicePointVersionRepository servicePointVersionRepository;

    @Autowired
    public ServicePointVersionRepositoryTest(ServicePointVersionRepository servicePointVersionRepository) {
        this.servicePointVersionRepository = servicePointVersionRepository;
    }

    @AfterEach
    void tearDown() {
        servicePointVersionRepository.deleteAll();
    }

    @Test
    void shouldSaveServicePointVersionWithUicCountry() {
        // given
        ServicePointVersion servicePoint = ServicePointVersion.builder()
                .number(1)
                .checkDigit(1)
                .numberShort(1)
                .uicCountry(UicCountry.builder()
                        .isoCode("CH")
                        .uicCode(85)
                        .nameDe("Schweiz")
                        .nameFr("Suisse")
                        .nameEn("Switzerland")
                        .nameIt("Svizzera")
                        .build())
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
    }
}