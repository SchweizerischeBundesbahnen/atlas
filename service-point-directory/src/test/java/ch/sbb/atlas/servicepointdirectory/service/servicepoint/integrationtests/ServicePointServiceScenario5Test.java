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

public class ServicePointServiceScenario5Test extends BaseServicePointServiceIntegrationTest{

    @Autowired
    public ServicePointServiceScenario5Test(ServicePointVersionRepository versionRepository,
                                            ServicePointService servicePointService) {
        super(versionRepository, servicePointService);
    }

    /**
     * Szenario 5: Update, das über mehrere Versionen hinausragt
     *
     * NEU:             |___________________________________|
     * IST:      |-----------|-----------|-----------|-------------------
     * Version:        1           2          3               4
     *
     * RESULTAT: |------|_____|__________|____________|_____|------------     NEUE VERSION EINGEFÜGT
     * Version:      1     5       2           3         6      4
     */
    @Test
    public void scenario5() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        version3 = versionRepository.save(version3);
        version4 = versionRepository.save(version4);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setOperatingPoint(true);
        editedVersion.setOperatingPointWithTimetable(true);
        editedVersion.setMeansOfTransport(Set.of(MeanOfTransport.CABLE_CAR));
        editedVersion.setValidFrom(LocalDate.of(2020, 6, 1));
        editedVersion.setValidTo(LocalDate.of(2025, 6, 1));
        // when
        servicePointService.updateServicePointVersion(version3, editedVersion);
        List<ServicePointVersion> result = versionRepository.findAllByNumberOrderByValidFrom(SPN);

        // then
        assertThat(result).isNotNull().hasSize(6);
        result.sort(Comparator.comparing(ServicePointVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();
        assertThat(result.get(3)).isNotNull();
        assertThat(result.get(4)).isNotNull();
        assertThat(result.get(5)).isNotNull();

        // update
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
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(secondTemporalVersion.getComment()).isNull();

        // update
        ServicePointVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(thirdTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(thirdTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(thirdTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(thirdTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Thunplatz");
        assertThat(thirdTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(thirdTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(fourthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fourthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fourthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fourthTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Eigerplatz");
        assertThat(fourthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(fourthTemporalVersion.getComment()).isNull();

        // new
        ServicePointVersion fifthTemporalVersion = result.get(4);
        assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(fifthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(fifthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(fifthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(fifthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(fifthTemporalVersion.getDesignationOfficial()).isEqualTo("Köniz, Liebefeld");
        assertThat(fifthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(fifthTemporalVersion.getComment()).isNull();

        // update
        ServicePointVersion sixthTemporalVersion = result.get(5);
        assertThat(sixthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 2));
        assertThat(sixthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(sixthTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(sixthTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(sixthTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(sixthTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(sixthTemporalVersion.getDesignationOfficial()).isEqualTo("Köniz, Liebefeld");
        assertThat(sixthTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.METRO));
        assertThat(sixthTemporalVersion.getComment()).isNull();
    }

}
