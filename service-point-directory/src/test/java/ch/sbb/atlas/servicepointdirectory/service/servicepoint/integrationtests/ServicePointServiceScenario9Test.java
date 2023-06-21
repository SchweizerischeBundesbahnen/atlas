package ch.sbb.atlas.servicepointdirectory.service.servicepoint.integrationtests;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ServicePointServiceScenario9Test extends BaseServicePointServiceIntegrationTest{

    @Autowired
    public ServicePointServiceScenario9Test(ServicePointVersionRepository versionRepository,
                                            ServicePointService servicePointService) {
        super(versionRepository, servicePointService);
    }

    /**
     * Szenario 9a (Fall 5): Update ausserhalb der existierenden version
     *
     * Änderung 1     |_____|
     * Version                 |--------------------|
     *
     * Ergebnis                |--------------------|
     * Version ist vom update nicht betroffen
     */
    @Test
    public void scenario9a() {
        // given
        version1 = versionRepository.save(version1);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2019, 6, 1));
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.getAllVersionsVersioned(SPN);

        // then
        assertThat(result).isNotNull().hasSize(2);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();

        // new version
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 6, 1));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // second version no changes
        ServicePointVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.BUS));
        assertThat(secondTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 9b (Spezialfall 5): Update vor erster existierender Version
     *
     * Änderung  |___|
     *                 |-----------------|----------------|-----------|         |-------------|
     *                     Version 1          Version 2     Version 3               Version 4
     *
     * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 1)
     */
    @Test
    public void scenario9b() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2019, 6, 1));
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.getAllVersionsVersioned(SPN);

        // then
        assertThat(result).isNotNull().hasSize(3);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();

        // new version
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 6, 1));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // second version no changes
        ServicePointVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.BUS));
        assertThat(secondTemporalVersion.getComment()).isNull();

        // third version no changes
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Thunplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAIN));
        assertThat(thirdTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 9c (Spezialfall 1): Update vor und während erster Version
     * Änderung    |_____________________|
     *                 |-----------------|----------------|-----------|         |-------------|
     *                     Version 1          Version 2     Version 3               Version 4
     *
     * Ergebnis: Version 1 wird verlängert
     */
    @Test
    public void scenario9c() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
        editedVersion.setValidTo(version1.getValidTo());
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.getAllVersionsVersioned(SPN);

        // then
        assertThat(result).isNotNull().hasSize(2);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();

        // version update
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // second version no changes
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
    }

}
