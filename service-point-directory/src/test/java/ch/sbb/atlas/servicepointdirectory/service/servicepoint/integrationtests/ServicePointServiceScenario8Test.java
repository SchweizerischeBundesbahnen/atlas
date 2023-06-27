package ch.sbb.atlas.servicepointdirectory.service.servicepoint.integrationtests;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ServicePointServiceScenario8Test extends BaseServicePointServiceIntegrationTest{

    @Autowired
    public ServicePointServiceScenario8Test(ServicePointVersionRepository versionRepository,
                                            ServicePointService servicePointService) {
        super(versionRepository, servicePointService);
    }

    /**
     * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
     * NEU:      |______________________|
     * IST:      |-------------------------------------------------------
     * Version:                            1
     *
     * RESULTAT: |----------------------| Version wird per xx aufgehoben
     * Version:         1
     */
    @Test
    public void scenario8() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setValidFrom(version3.getValidFrom());
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
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

        // update
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(thirdTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 8b: Letzte Version validTo und props updated
     * NEU:      |______________________|
     * IST:      |-------------------------------------------------------
     * Version:                            1
     *
     * RESULTAT: |----------------------|--------------------------------|
     * Version:         1                             2
     */
    @Test
    public void scenario8b() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(version3.getValidFrom());
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
        // when
        servicePointService.updateServicePointVersion(version3, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

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

        // update
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(fourthTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 8c: Letzte Version nur validTo
     * NEU:      |__________________________|
     * IST:      |----------------------|       |-------------------------|
     * Version:             1                             2
     *
     * RESULTAT: |-------------------------|    |------------------------|
     * Version:             1                             2
     */
    @Test
    public void scenario8c() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        version4.setValidFrom(LocalDate.of(2025, 6, 1));
        version4 = versionRepository.save(version4);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setValidTo(LocalDate.of(2025, 2, 1));
        editedVersion.setValidFrom(version3.getValidFrom());
        // when
        servicePointService.updateServicePointVersion(version3, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

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

        // update
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 2, 1));
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
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Köniz, Liebefeld");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.METRO));
        assertThat(fourthTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 8d: Letzte Version validTo und props updated
     * NEU:      |__________________________|
     * IST:      |----------------------|       |-------------------------|
     * Version:             1                             2
     *
     * RESULTAT: |--------------------------|    |------------------------|
     * Version:             1                             2
     */
    @Test
    public void scenario8d() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        version4.setValidFrom(LocalDate.of(2025, 6, 1));
        version4 = versionRepository.save(version4);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidTo(LocalDate.of(2025, 2, 1));
        editedVersion.setValidFrom(version3.getValidFrom());
        // when
        servicePointService.updateServicePointVersion(version3, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

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

        // update
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 2, 1));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Köniz, Liebefeld");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.METRO));
        assertThat(fourthTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 8e: Letzte Version validTo und props updated
     * NEU:      |________________________________________|
     * IST:      |----------------------|       |-------------------------|
     * Version:             1                                 2
     *
     * RESULTAT: |------------------------------|--------|----------------|
     * Version:             1                        2            3
     */
    @Test
    public void scenario8e() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        version4.setValidFrom(LocalDate.of(2025, 6, 1));
        version4 = versionRepository.save(version4);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidTo(LocalDate.of(2025, 8, 1));
        editedVersion.setValidFrom(version3.getValidFrom());
        // when
        servicePointService.updateServicePointVersion(version3, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

        // then
        assertThat(result).isNotNull().hasSize(5);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();
        assertThat(result.get(3)).isNotNull();
        assertThat(result.get(4)).isNotNull();

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

        // update
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 5, 31));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 8, 1));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Köniz, Liebefeld");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(fourthTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fifthTemporalVersion = result.get(4);
        assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 8, 2));
        assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(fifthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fifthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fifthTemporalVersion.getDesignationOfficial()).isEqualTo("Köniz, Liebefeld");
        assertThat(fifthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.METRO));
        assertThat(fifthTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 8f: Letzte Version validTo und props updated
     * NEU:      |________________________________________________________________|
     * IST:      |----------------------|       |---------|----------------|----------------|
     * Version:             1                        2            3               4
     *
     * RESULTAT: |------------------------------|--------|----------------|------|----------|
     * Version:             1                        2            3           4       5
     */
    @Test
    public void scenario8f() {
        // given
        version1.setValidTo(LocalDate.of(2021,6,1));
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        version4 = versionRepository.save(version4);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidTo(LocalDate.of(2025, 6, 1));
        editedVersion.setValidFrom(version1.getValidFrom());
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

        // then
        assertThat(result).isNotNull().hasSize(5);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();
        assertThat(result.get(3)).isNotNull();
        assertThat(result.get(4)).isNotNull();

        // not touched
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
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
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(secondTemporalVersion.getComment()).isNull();

        // update
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Köniz, Liebefeld");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(fourthTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fifthTemporalVersion = result.get(4);
        assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 2));
        assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(fifthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fifthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fifthTemporalVersion.getDesignationOfficial()).isEqualTo("Köniz, Liebefeld");
        assertThat(fifthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.METRO));
        assertThat(fifthTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 8g: Letzte Version validTo und props updated
     * NEU:      |________________________________________________________________|
     * IST:      |----------------------|       |----------------|         |----------------|
     * Version:             1                           2                          3
     *
     * RESULTAT: |------------------------------|-------------------------|------|----------|
     * Version:             1                           2                    3       4
     */
    @Test
    public void scenario8g() {
        // given
        version1.setValidTo(LocalDate.of(2021,6,1));
        version1 = versionRepository.save(version1);
        version2.setValidTo(LocalDate.of(2022,6,1));
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
        editedVersion.setValidFrom(version1.getValidFrom());
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

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
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
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
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(secondTemporalVersion.getComment()).isNull();

        // update
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(fourthTemporalVersion.getComment()).isNull();
    }

}
