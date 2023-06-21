package ch.sbb.atlas.servicepointdirectory.service.servicepoint.integrationtests;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.versioning.exception.DateValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ServicePointServiceScenario7Test extends BaseServicePointServiceIntegrationTest{

    @Autowired
    public ServicePointServiceScenario7Test(ServicePointVersionRepository versionRepository,
                                            ServicePointService servicePointService) {
        super(versionRepository, servicePointService);
    }

    /**
     * Szenario 7a: Neue Version in der Zukunft, die letzte Version nur berührt
     *
     * NEU:                       |________________________________
     * IST:      |----------------|
     * Version:           1
     *
     * RESULTAT: |----------------|________________________________     NEUE VERSION EINGEFÜGT
     * Version:          1                         2
     */
    @Test
    public void scenario7a() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(LocalDate.of(2025, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2025, 12, 31));
        // when
        servicePointService.updateServicePointVersion(version3, editedVersion);
        List<ServicePointVersion> result = versionRepository.getAllVersionsVersioned(SPN);

        // then
        assertThat(result).isNotNull().hasSize(4);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();
        assertThat(result.get(3)).isNotNull();

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

        // not touched
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

        // not touched
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // fourth new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(fourthTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 7b: Neue Version in der Vergangenheit, die nächste Version nur berührt
     *
     * NEU:      |________________________________|
     * IST:                                       |----------------|
     * Version:                                           1
     *
     * RESULTAT: |________________________________|----------------|     NEUE VERSION EINGEFÜGT
     * Version:                 2                         1
     */
    @Test
    public void scenario7b() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2019, 12, 31));
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.getAllVersionsVersioned(SPN);

        // then
        assertThat(result).isNotNull().hasSize(4);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();
        assertThat(result.get(3)).isNotNull();

        // new
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 12, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // not touched
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

        // not touched
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

        // not touched
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(fourthTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 7c: Neue Version in der Zukunft, die letzte Version nicht überschneidet
     *
     * NEU:                             |________________________________
     * IST:      |----------------|
     * Version:           1
     *
     * RESULTAT: |----------------|     |________________________________     NEUE VERSION EINGEFÜGT
     * Version:          1                               2
     */
    @Test
    public void scenario7c() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(LocalDate.of(2025, 6, 1));
        editedVersion.setValidTo(LocalDate.of(2025, 12, 31));
        // when
        servicePointService.updateServicePointVersion(version3, editedVersion);
        List<ServicePointVersion> result = versionRepository.getAllVersionsVersioned(SPN);

        // then
        assertThat(result).isNotNull().hasSize(4);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();
        assertThat(result.get(3)).isNotNull();

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

        // not touched
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

        // not touched
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(fourthTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 7d: Neue Version in der Vergangenheit, die nächste Version nicht überschneidet
     *
     * NEU:      |________________________________|
     * IST:                                            |----------------|
     * Version:                                                1
     *
     * RESULTAT: |________________________________|    |----------------|     NEUE VERSION EINGEFÜGT
     * Version:                 2                              1
     */
    @Test
    public void scenario7d() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(LocalDate.of(2019, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2019, 5, 31));
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.getAllVersionsVersioned(SPN);

        // then
        assertThat(result).isNotNull().hasSize(4);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();
        assertThat(result.get(3)).isNotNull();

        // new
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2019, 5, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // not touched
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

        // not touched
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

        // not touched
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(fourthTemporalVersion.getComment()).isNull();
    }

    /**
     * ValidFrom is bigger than validTo: expected exception
     */
    @Test
    public void scenarioValidFromBiggerThenValidTo() {
        // given
        version1.setValidFrom(LocalDate.of(2020, 12, 12));
        version1.setValidTo(LocalDate.of(2029, 12, 8));
        version1 = versionRepository.save(version1);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(LocalDate.of(2029, 12, 9));
        editedVersion.setValidTo(version1.getValidTo());
        //when
        assertThatThrownBy(() -> {
            servicePointService.updateServicePointVersion(version1, editedVersion);
            //then
        }).isInstanceOf(DateValidationException.class)
                .hasMessageContaining("Edited ValidFrom 2029-12-09 is bigger than edited ValidTo 2029-12-08");
    }

}
