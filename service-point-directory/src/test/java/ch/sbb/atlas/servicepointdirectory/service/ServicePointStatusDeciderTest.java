package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointStatusDecider3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@IntegrationTest
class ServicePointStatusDeciderTest {

    @MockBean
    private GeoReferenceService geoReferenceService;

    private final ServicePointStatusDecider3 servicePointStatusDecider = new ServicePointStatusDecider3(geoReferenceService);
//    private final ServicePointStatusDecider servicePointStatusDecider = new ServicePointStatusDecider(geoReferenceService);

    @BeforeEach
    void setUp() {
        GeoReference geoReference = GeoReference.builder().country(Country.SWITZERLAND).build();
        when(geoReferenceService.getGeoReference(any())).thenReturn(geoReference);
    }

    @Test
    void testCheck() {
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
    void testCheck1() {
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
    void testCheck2() {
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
        newOne.setValidTo(LocalDate.of(2019, 8, 1));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void testCheck3() {
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
        newOne.setValidFrom(LocalDate.of(2014, 1, 1));
        newOne.setValidTo(LocalDate.of(2019, 8, 1));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void testCheck4() {
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
        newOne.setValidFrom(LocalDate.of(2020, 1, 1));
        newOne.setValidTo(LocalDate.of(2021, 8, 1));

        assertThat(servicePointStatusDecider.checkIfVersionIsIsolated(newOne, servicePointVersionList)).isTrue();
    }



}
