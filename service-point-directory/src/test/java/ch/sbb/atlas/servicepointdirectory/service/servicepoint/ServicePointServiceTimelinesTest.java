package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepointdirectory.ServicePointVersionsTimelineTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSearchVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.versioning.service.VersionableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicePointServiceTimelinesTest {

    private ServicePointService servicePointService;

    @Mock
    private ServicePointVersionRepository servicePointVersionRepositoryMock;

    @Mock
    private VersionableService versionableServiceMock;

    @Mock
    private ServicePointValidationService servicePointValidationService;

    @Mock
    private ServicePointSearchVersionRepository servicePointSearchVersionRepository;

    @BeforeEach
    void initMocksAndService() {
        MockitoAnnotations.openMocks(this);
        servicePointService = new ServicePointService(servicePointVersionRepositoryMock, versionableServiceMock,
                servicePointValidationService, servicePointSearchVersionRepository);
    }

    @Test
    void testScenarion1SePoTimelineIsEquallyLongAsOneOfKilMasterTimelines() {
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

        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();

        boolean result = servicePointService.checkIfKilometerMasterNumberCanBeAssigned(servicePointVersionList, servicePointVersion);
        Assert.equals(true, result);
    }

    @Test
    void testScenarion2SePoTimelineValidToIsLongerThanOneOfKilMasterTimelineValidTo() {
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

        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidTo(LocalDate.of(2014, 8, 18));

        boolean result = servicePointService.checkIfKilometerMasterNumberCanBeAssigned(servicePointVersionList, servicePointVersion);
        Assert.equals(false, result);
    }

    @Test
    void testScenarion3SePoTimelineValidFromIsBeforeThanBpsKilMasterTimelineValidFrom() {
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

        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidFrom(LocalDate.of(2013, 8, 12));

        boolean result = servicePointService.checkIfKilometerMasterNumberCanBeAssigned(servicePointVersionList, servicePointVersion);
        Assert.equals(false, result);
    }

    @Test
    void testScenarion4SePoTimelineIsInsideOfKilMasterTimeline() {
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

        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidFrom(LocalDate.of(2013, 8, 20));
        servicePointVersion.setValidTo(LocalDate.of(2014, 8, 10));

        boolean result = servicePointService.checkIfKilometerMasterNumberCanBeAssigned(servicePointVersionList, servicePointVersion);
        Assert.equals(true, result);
    }

    @Test
    void testScenarion5SePoTimelineIsInsideOfKilMasterTimelines() {
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

        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel2();
        servicePointVersion.setValidFrom(LocalDate.of(2011, 1, 1));
        servicePointVersion.setValidTo(LocalDate.of(2013, 1, 1));

        boolean result = servicePointService.checkIfKilometerMasterNumberCanBeAssigned(servicePointVersionList, servicePointVersion);
        Assert.equals(true, result);
    }

    @Test
    void testScenarion6SePoTimelineIncludesKilMasterTimelineWithGap() {
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

        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidFrom(LocalDate.of(2013, 8, 1));
        servicePointVersion.setValidTo(LocalDate.of(2014, 8, 1));

        boolean result = servicePointService.checkIfKilometerMasterNumberCanBeAssigned(servicePointVersionList, servicePointVersion);
        Assert.equals(false, result);
    }

    @Test
    void testScenarion7SepoTimelineGoesOverKilMasterTimelineWithGap() {
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

        ServicePointVersion servicePointVersion = ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4();
        servicePointVersion.setValidFrom(LocalDate.of(2013, 8, 15));
        servicePointVersion.setValidTo(LocalDate.of(2014, 8, 20));

        boolean result = servicePointService.checkIfKilometerMasterNumberCanBeAssigned(servicePointVersionList, servicePointVersion);
        Assert.equals(false, result);
    }

}
