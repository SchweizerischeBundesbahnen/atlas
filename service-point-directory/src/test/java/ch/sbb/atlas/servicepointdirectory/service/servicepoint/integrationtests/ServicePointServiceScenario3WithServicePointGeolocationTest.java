package ch.sbb.atlas.servicepointdirectory.service.servicepoint.integrationtests;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.model.TestData;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ServicePointServiceScenario3WithServicePointGeolocationTest extends BaseServicePointServiceIntegrationTest{

    @Autowired
    public ServicePointServiceScenario3WithServicePointGeolocationTest(ServicePointVersionRepository versionRepository,
                                                                       ServicePointService servicePointService) {
        super(versionRepository, servicePointService);
    }

    /**
     * Szenario 3: Update, dass über Versionsgrenze geht
     * NEU:                                   |___________|
     * IST:      |-----------|----------------------|--------------------
     * Version:        1                 2                  3
     *
     * RESULTAT: |-----------|----------------|______|_____|-------------     NEUE VERSION EINGEFÜGT
     * Version:        1                 2        4     5          3
     */
    @Test
    public void scenario3UpdateVersion2ByAddingServicePointGeolocation() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setServicePointGeolocation(TestData.testGeolocationWgs84());
        editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
        // when
        servicePointService.updateServicePointVersion(version2, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

        // then
        assertThat(result).isNotNull().hasSize(5);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();

        // not touched
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.BUS));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // updated
        ServicePointVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 5, 31));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Thunplatz");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAIN));
        assertThat(secondTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Thunplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAIN));
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSpatialReference()).isEqualTo(TestData.testGeolocationWgs84().getSpatialReference());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getEast()).isEqualTo(TestData.testGeolocationWgs84().getEast());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getNorth()).isEqualTo(TestData.testGeolocationWgs84().getNorth());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getHeight()).isEqualTo(TestData.testGeolocationWgs84().getHeight());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getCountry()).isEqualTo(TestData.testGeolocationWgs84().getCountry());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissCanton()).isEqualTo(TestData.testGeolocationWgs84().getSwissCanton());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissDistrictName()).isEqualTo(TestData.testGeolocationWgs84().getSwissDistrictName());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissDistrictNumber()).isEqualTo(TestData.testGeolocationWgs84().getSwissDistrictNumber());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissMunicipalityNumber()).isEqualTo(TestData.testGeolocationWgs84().getSwissMunicipalityNumber());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissMunicipalityName()).isEqualTo(TestData.testGeolocationWgs84().getSwissMunicipalityName());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissLocalityName()).isEqualTo(TestData.testGeolocationWgs84().getSwissLocalityName());
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getSpatialReference()).isEqualTo(TestData.testGeolocationWgs84().getSpatialReference());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getEast()).isEqualTo(TestData.testGeolocationWgs84().getEast());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getNorth()).isEqualTo(TestData.testGeolocationWgs84().getNorth());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getHeight()).isEqualTo(TestData.testGeolocationWgs84().getHeight());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getCountry()).isEqualTo(TestData.testGeolocationWgs84().getCountry());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getSwissCanton()).isEqualTo(TestData.testGeolocationWgs84().getSwissCanton());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getSwissDistrictName()).isEqualTo(TestData.testGeolocationWgs84().getSwissDistrictName());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getSwissDistrictNumber()).isEqualTo(TestData.testGeolocationWgs84().getSwissDistrictNumber());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getSwissMunicipalityNumber()).isEqualTo(TestData.testGeolocationWgs84().getSwissMunicipalityNumber());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getSwissMunicipalityName()).isEqualTo(TestData.testGeolocationWgs84().getSwissMunicipalityName());
        assertThat(fourthTemporalVersion.getServicePointGeolocation().getSwissLocalityName()).isEqualTo(TestData.testGeolocationWgs84().getSwissLocalityName());
        assertThat(fourthTemporalVersion.getComment()).isNull();

        // updated
        ServicePointVersion fifthTemporalVersion = result.get(4);
        assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
        assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(fifthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fifthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fifthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fifthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(fifthTemporalVersion.getComment()).isNull();
    }

    @Test
    public void scenario3UpdateVersion3ByChangingServicePointGeolocationCreatorAndEditor() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        ServicePointGeolocation spg = TestData.testGeolocationWgs84();
        String initialCreator = "initialCreator";
        String initialEditor = "initialEditor";
        spg.setCreator(initialCreator);
        spg.setEditor(initialEditor);
        version3.setServicePointGeolocation(spg);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        ServicePointGeolocation updatedSpg = TestData.testGeolocationWgs84();
        String updatedCreator = "updatedCreator";
        String updatedEditor = "updatedEditor";
        updatedSpg.setCreator(updatedCreator);
        updatedSpg.setEditor(updatedEditor);
        editedVersion.setServicePointGeolocation(updatedSpg);
        editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
        // when
        servicePointService.updateServicePointVersion(version3, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

        // then
        assertThat(result).isNotNull().hasSize(3);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();

        // not touched
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.BUS));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // updated
        ServicePointVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Thunplatz");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAIN));
        assertThat(secondTemporalVersion.getComment()).isNull();

        // updated
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSpatialReference()).isEqualTo(TestData.testGeolocationWgs84().getSpatialReference());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getEast()).isEqualTo(TestData.testGeolocationWgs84().getEast());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getNorth()).isEqualTo(TestData.testGeolocationWgs84().getNorth());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getHeight()).isEqualTo(TestData.testGeolocationWgs84().getHeight());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getCountry()).isEqualTo(TestData.testGeolocationWgs84().getCountry());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissCanton()).isEqualTo(TestData.testGeolocationWgs84().getSwissCanton());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissDistrictName()).isEqualTo(TestData.testGeolocationWgs84().getSwissDistrictName());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissDistrictNumber()).isEqualTo(TestData.testGeolocationWgs84().getSwissDistrictNumber());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissMunicipalityNumber()).isEqualTo(TestData.testGeolocationWgs84().getSwissMunicipalityNumber());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissMunicipalityName()).isEqualTo(TestData.testGeolocationWgs84().getSwissMunicipalityName());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissLocalityName()).isEqualTo(TestData.testGeolocationWgs84().getSwissLocalityName());
        assertThat(thirdTemporalVersion.getServicePointGeolocation().getSwissLocalityName()).isEqualTo(TestData.testGeolocationWgs84().getSwissLocalityName());
        assertThat(thirdTemporalVersion.getComment()).isNull();
    }

    @Test
    public void updateServicePointGeolocationCreatorOnExistingVersion() {
        // given
        version1 = versionRepository.save(version1);
        ServicePointGeolocation spg = TestData.testGeolocationWgs84();
        String initialCreator = "initialCreator";
        String initialEditor = "initialEditor";
        spg.setCreator(initialCreator);
        spg.setEditor(initialEditor);
        version1.setServicePointGeolocation(spg);

        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        ServicePointGeolocation updatedSpg = TestData.testGeolocationWgs84();
        String updatedCreator = "updatedCreator";
        updatedSpg.setCreator(updatedCreator);
        editedVersion.setServicePointGeolocation(updatedSpg);
        editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2022, 1, 2));
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

        // then
        assertThat(result).isNotNull().hasSize(1);
    }

}
