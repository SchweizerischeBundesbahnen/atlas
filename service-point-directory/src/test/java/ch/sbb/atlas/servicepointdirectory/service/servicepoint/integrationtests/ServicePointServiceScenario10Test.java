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

public class ServicePointServiceScenario10Test extends BaseServicePointServiceIntegrationTest{

    @Autowired
    public ServicePointServiceScenario10Test(ServicePointVersionRepository versionRepository,
                                             ServicePointService servicePointService) {
        super(versionRepository, servicePointService);
    }

    /**
     * Szenario 10a (Spezialfall 3): Update in der Lücke zwischen zwei Versionen
     *
     * Änderung                     |_________|
     *                 |-----------|           |-------------|
     *                   Version 1                Version 2
     *
     * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 3)
     */
    @Test
    public void scenario10a() {
        // given
        version1 = versionRepository.save(version1);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2023, 12, 31));
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

        // then
        assertThat(result).isNotNull().hasSize(3);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();

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

        // new
        ServicePointVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(secondTemporalVersion.getComment()).isNull();

        // new
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
    }

    /**
     * Szenario 10a (Spezialfall 3): Update in der Lücke zwischen zwei Versionen
     *
     * Änderung                     |_________|
     *                 |-----------|           |-------------|
     *                   Version 1                Version 2
     *
     * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 3)
     */
    @Test
    public void scenario10aMergeWhenNoPropertiesChanged() {
        // given
        version1 = versionRepository.save(version1);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2023, 12, 31));
        // when
        servicePointService.updateServicePointVersion(version1, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

        // then
        assertThat(result).isNotNull().hasSize(2);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();

        // first version updated
        ServicePointVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.BUS));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // second version not touched
        ServicePointVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.TRAM));
        assertThat(secondTemporalVersion.getComment()).isNull();
    }

    /**
     * Szenario 10b (Spezialfall 6): Update, welches über die Lücke zwischen zwei Versionen hinaus geht
     *
     * Änderung                 |_________________|
     *                 |------------|         |-------------|
     *                   Version 1               Version 2
     *
     * Ergebnis: In der Lücke wird eine neue Version erstellt (mit Inhalt von Änderung und Version 3)
     */
    @Test
    public void scenario10b() {
        // given
        version1 = versionRepository.save(version1);
        version3 = versionRepository.save(version3);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
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
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.BUS));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(secondTemporalVersion.getComment()).isNull();

        // new
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
