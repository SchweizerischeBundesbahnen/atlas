package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class ServicePointStatusDeciderTest {

    @Autowired
    private ServicePointStatusDecider servicePointStatusDecider;

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

        assertThat(servicePointStatusDecider.isIsolatedOrTouchingServicePointVersion(newOne, servicePointVersionList)).isFalse();
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

        assertThat(servicePointStatusDecider.isIsolatedOrTouchingServicePointVersion(newOne, servicePointVersionList)).isFalse();
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

        assertThat(servicePointStatusDecider.isIsolatedOrTouchingServicePointVersion(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointOnTheSameStartingDayThenTouchingFalse() {
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

        assertThat(servicePointStatusDecider.isIsolatedOrTouchingServicePointVersion(newOne, servicePointVersionList)).isFalse();
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

        assertThat(servicePointStatusDecider.isIsolatedOrTouchingServicePointVersion(newOne, servicePointVersionList)).isTrue();
    }

    @Test
    void whenNewServicePointOnTheSameEndingDayThenTouchingFalse() {
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

        assertThat(servicePointStatusDecider.isIsolatedOrTouchingServicePointVersion(newOne, servicePointVersionList)).isFalse();
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

        assertThat(servicePointStatusDecider.isIsolatedOrTouchingServicePointVersion(newOne, servicePointVersionList)).isTrue();
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

        assertThat(servicePointStatusDecider.isIsolatedOrTouchingServicePointVersion(newOne, servicePointVersionList)).isTrue();
    }

    @Test
    void whenNewServicePointInsideOfServicePointsThenOverlappingVersionWithTheSameNameTrue() {
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

        assertThat(servicePointStatusDecider.isThereOverlappingVersionWithTheSameName(newOne, servicePointVersionList)).isTrue();
    }

    @Test
    void whenNewServicePointInsideOfSecondOfServicePointsThenOverlappingVersionWithTheSameNameTrue() {
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

        assertThat(servicePointStatusDecider.isThereOverlappingVersionWithTheSameName(newOne, servicePointVersionList)).isTrue();
    }

    @Test
    void whenNewServicePointInsideOfFirstServicePointsThenOverlappingVersionWithTheSameNameTrue() {
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

        assertThat(servicePointStatusDecider.isThereOverlappingVersionWithTheSameName(newOne, servicePointVersionList)).isTrue();
    }

    @Test
    void whenNewServicePointOnTheSameStartingDayThenOverlappingVersionWithTheSameNameFalse() {
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

        assertThat(servicePointStatusDecider.isThereOverlappingVersionWithTheSameName(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointOneDayBeforeServicePointsThenOverlappingVersionWithTheSameNameFalse() {
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

        assertThat(servicePointStatusDecider.isThereOverlappingVersionWithTheSameName(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointOnTheSameEndingDayThenOverlappingVersionWithTheSameNameFalse() {
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

        assertThat(servicePointStatusDecider.isThereOverlappingVersionWithTheSameName(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointOneDayAfterServicePointsThenOverlappingVersionWithTheSameNameFalse() {
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

        assertThat(servicePointStatusDecider.isThereOverlappingVersionWithTheSameName(newOne, servicePointVersionList)).isFalse();
    }

    @Test
    void whenNewServicePointBeforeServicePointsThenOverlappingVersionWithTheSameNameFalse() {
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

        assertThat(servicePointStatusDecider.isThereOverlappingVersionWithTheSameName(newOne, servicePointVersionList)).isFalse();
    }

}
