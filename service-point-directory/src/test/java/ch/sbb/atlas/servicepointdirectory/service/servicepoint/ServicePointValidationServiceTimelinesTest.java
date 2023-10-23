package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.ServicePointVersionsTimelineTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ForbiddenDueToChosenServicePointVersionValidationPeriodException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ServicePointValidationServiceTimelinesTest {

    private ServicePointValidationService servicePointValidationService;

    @Mock
    private ServicePointVersionRepository servicePointVersionRepositoryMock;

    @Mock
    private SharedBusinessOrganisationService sharedBusinessOrganisationServiceMock;

    private ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8034510);

    @BeforeEach
    void initMocksAndService() {
        MockitoAnnotations.openMocks(this);
        servicePointValidationService = new ServicePointValidationService(sharedBusinessOrganisationServiceMock, servicePointVersionRepositoryMock);
        when(servicePointVersionRepositoryMock.findAllByNumberAndOperatingPointRouteNetworkTrueOrderByValidFrom(servicePointNumber)).thenReturn(getServicePointVersions());
    }

    private static List<ServicePointVersion> getServicePointVersions() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel0());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel2());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel5());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel6());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel7());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel8());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel9());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel10());
        return servicePointVersionList;
    }

    @Test
    void testScenarion1SePoTimelineIsEquallyLongAsOneOfKilMasterTimelines() {
        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();

        assertDoesNotThrow(() -> servicePointValidationService.checkIfKilometerMasterNumberCanBeAssigned(servicePointNumber, servicePointVersion));
    }


    @Test
    void testScenarion2SePoTimelineValidToIsLongerThanOneOfKilMasterTimelineValidTo() {
        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidTo(LocalDate.of(2014, 8, 18));

        assertThrows(ForbiddenDueToChosenServicePointVersionValidationPeriodException.class, () ->
                servicePointValidationService.checkIfKilometerMasterNumberCanBeAssigned(servicePointNumber, servicePointVersion));
    }

    @Test
    void testScenarion3SePoTimelineValidFromIsBeforeThanBpsKilMasterTimelineValidFrom() {
        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidFrom(LocalDate.of(2013, 8, 12));

        assertThrows(ForbiddenDueToChosenServicePointVersionValidationPeriodException.class, () ->
                servicePointValidationService.checkIfKilometerMasterNumberCanBeAssigned(servicePointNumber, servicePointVersion));
    }

    @Test
    void testScenarion4SePoTimelineIsInsideOfKilMasterTimeline() {
        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidFrom(LocalDate.of(2013, 8, 20));
        servicePointVersion.setValidTo(LocalDate.of(2014, 8, 10));

        assertDoesNotThrow(() -> servicePointValidationService.checkIfKilometerMasterNumberCanBeAssigned(servicePointNumber, servicePointVersion));
    }

    @Test
    void testScenarion5SePoTimelineIsInsideOfKilMasterTimelines() {
        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel2();
        servicePointVersion.setValidFrom(LocalDate.of(2011, 1, 1));
        servicePointVersion.setValidTo(LocalDate.of(2013, 1, 1));

        assertDoesNotThrow(() -> servicePointValidationService.checkIfKilometerMasterNumberCanBeAssigned(servicePointNumber, servicePointVersion));
    }

    @Test
    void testScenarion6SePoTimelineIncludesKilMasterTimelineWithGap() {
        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidFrom(LocalDate.of(2013, 8, 1));
        servicePointVersion.setValidTo(LocalDate.of(2014, 8, 1));

        assertThrows(ForbiddenDueToChosenServicePointVersionValidationPeriodException.class, () ->
                servicePointValidationService.checkIfKilometerMasterNumberCanBeAssigned(servicePointNumber, servicePointVersion));
    }

    @Test
    void testScenarion7SepoTimelineGoesOverKilMasterTimelineWithGap() {
        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidFrom(LocalDate.of(2013, 8, 15));
        servicePointVersion.setValidTo(LocalDate.of(2014, 8, 20));

        assertThrows(ForbiddenDueToChosenServicePointVersionValidationPeriodException.class, () ->
                servicePointValidationService.checkIfKilometerMasterNumberCanBeAssigned(servicePointNumber, servicePointVersion));
    }

}
