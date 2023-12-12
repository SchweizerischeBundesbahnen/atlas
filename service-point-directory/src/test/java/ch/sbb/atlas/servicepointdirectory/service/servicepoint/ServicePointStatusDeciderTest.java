package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServicePointStatusDeciderTest {

    @MockBean
    private GeoReferenceService geoReferenceService;

    private ServicePointStatusDecider servicePointStatusDecider;

    @BeforeAll
    void beforeAll() {
        servicePointStatusDecider = new ServicePointStatusDecider(geoReferenceService);
    }

    @BeforeEach
    void setUp() {
        GeoReference geoReference = GeoReference.builder().country(Country.SWITZERLAND).build();
        when(geoReferenceService.getGeoReference(any())).thenReturn(geoReference);
    }

    @Test
    void whenNewServicePointInsideOfServicePointsThenIsolatedFalse() {
        ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
        servicePointVersion.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePointVersion.setValidTo(LocalDate.of(2015, 12, 31));
        ServicePointVersion servicePointVersion1 = ServicePointTestData.getBern();
        servicePointVersion1.setValidFrom(LocalDate.of(2016, 1, 1));
        servicePointVersion1.setValidTo(LocalDate.of(2019, 8, 10));
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(servicePointVersion);
        servicePointVersionList.add(servicePointVersion1);

        ServicePointVersion newOne = ServicePointTestData.getBern();
        newOne.setValidFrom(LocalDate.of(2015, 1, 1));
        newOne.setValidTo(LocalDate.of(2019, 8, 10));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointInsideOfSecondOfServicePointsThenIsolatedFalse() {
        ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
        servicePointVersion.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePointVersion.setValidTo(LocalDate.of(2015, 12, 31));
        ServicePointVersion servicePointVersion1 = ServicePointTestData.getBern();
        servicePointVersion1.setValidFrom(LocalDate.of(2016, 1, 1));
        servicePointVersion1.setValidTo(LocalDate.of(2019, 8, 10));
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(servicePointVersion);
        servicePointVersionList.add(servicePointVersion1);

        ServicePointVersion newOne = ServicePointTestData.getBern();
        newOne.setValidFrom(LocalDate.of(2017, 1, 1));
        newOne.setValidTo(LocalDate.of(2019, 8, 1));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointInsideOfFirstServicePointsThenIsolatedFalse() {
        ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
        servicePointVersion.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePointVersion.setValidTo(LocalDate.of(2015, 12, 31));
        ServicePointVersion servicePointVersion1 = ServicePointTestData.getBern();
        servicePointVersion1.setValidFrom(LocalDate.of(2016, 1, 1));
        servicePointVersion1.setValidTo(LocalDate.of(2019, 8, 10));
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(servicePointVersion);
        servicePointVersionList.add(servicePointVersion1);

        ServicePointVersion newOne = ServicePointTestData.getBern();
        newOne.setValidFrom(LocalDate.of(2015, 1, 1));
        newOne.setValidTo(LocalDate.of(2015, 8, 1));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointOnTheSameStartingDayThenIsolatedTrue() {
        ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
        servicePointVersion.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePointVersion.setValidTo(LocalDate.of(2015, 12, 31));
        ServicePointVersion servicePointVersion1 = ServicePointTestData.getBern();
        servicePointVersion1.setValidFrom(LocalDate.of(2016, 1, 1));
        servicePointVersion1.setValidTo(LocalDate.of(2019, 8, 10));
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(servicePointVersion);
        servicePointVersionList.add(servicePointVersion1);

        ServicePointVersion newOne = ServicePointTestData.getBern();
        newOne.setValidFrom(LocalDate.of(2009, 8, 10));
        newOne.setValidTo(LocalDate.of(2010, 12, 11));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointOneDayBeforeServicePointsThenIsolatedTrue() {
        ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
        servicePointVersion.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePointVersion.setValidTo(LocalDate.of(2015, 12, 31));
        ServicePointVersion servicePointVersion1 = ServicePointTestData.getBern();
        servicePointVersion1.setValidFrom(LocalDate.of(2016, 1, 1));
        servicePointVersion1.setValidTo(LocalDate.of(2019, 8, 10));
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(servicePointVersion);
        servicePointVersionList.add(servicePointVersion1);

        ServicePointVersion newOne = ServicePointTestData.getBern();
        newOne.setValidFrom(LocalDate.of(2009, 8, 10));
        newOne.setValidTo(LocalDate.of(2010, 12, 10));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isTrue();
    }

    @Test
    void whenNewServicePointOnTheSameEndingDayThenIsolatedTrue() {
        ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
        servicePointVersion.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePointVersion.setValidTo(LocalDate.of(2015, 12, 31));
        ServicePointVersion servicePointVersion1 = ServicePointTestData.getBern();
        servicePointVersion1.setValidFrom(LocalDate.of(2016, 1, 1));
        servicePointVersion1.setValidTo(LocalDate.of(2019, 8, 10));
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(servicePointVersion);
        servicePointVersionList.add(servicePointVersion1);

        ServicePointVersion newOne = ServicePointTestData.getBern();
        newOne.setValidFrom(LocalDate.of(2019, 8, 10));
        newOne.setValidTo(LocalDate.of(2019, 12, 1));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointOneDayAfterServicePointsThenIsolatedTrue() {
        ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
        servicePointVersion.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePointVersion.setValidTo(LocalDate.of(2015, 12, 31));
        ServicePointVersion servicePointVersion1 = ServicePointTestData.getBern();
        servicePointVersion1.setValidFrom(LocalDate.of(2016, 1, 1));
        servicePointVersion1.setValidTo(LocalDate.of(2019, 8, 10));
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(servicePointVersion);
        servicePointVersionList.add(servicePointVersion1);

        ServicePointVersion newOne = ServicePointTestData.getBern();
        newOne.setValidFrom(LocalDate.of(2019, 8, 11));
        newOne.setValidTo(LocalDate.of(2019, 12, 1));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isTrue();
    }

    @Test
    void whenNewServicePointBeforeServicePointsThenIsolatedTrue() {
        ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
        servicePointVersion.setValidFrom(LocalDate.of(2010, 12, 11));
        servicePointVersion.setValidTo(LocalDate.of(2015, 12, 31));
        ServicePointVersion servicePointVersion1 = ServicePointTestData.getBern();
        servicePointVersion1.setValidFrom(LocalDate.of(2016, 1, 1));
        servicePointVersion1.setValidTo(LocalDate.of(2019, 8, 10));
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(servicePointVersion);
        servicePointVersionList.add(servicePointVersion1);

        ServicePointVersion newOne = ServicePointTestData.getBern();
        newOne.setValidFrom(LocalDate.of(2009, 1, 1));
        newOne.setValidTo(LocalDate.of(2009, 8, 1));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isTrue();
    }



}
