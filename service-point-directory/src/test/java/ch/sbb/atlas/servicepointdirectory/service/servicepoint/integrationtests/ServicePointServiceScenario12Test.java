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

public class ServicePointServiceScenario12Test extends BaseServicePointServiceIntegrationTest{

    @Autowired
    public ServicePointServiceScenario12Test(ServicePointVersionRepository versionRepository,
                                             ServicePointService servicePointService) {
        super(versionRepository, servicePointService);
    }

    /**
     * Scenario 12 (Spezialfall 2): Update während und nach letzter Version
     *
     * Änderung                          |_____________________|
     *                 |-----------------|-------------|
     *                     Version 1       Version 2
     *
     * Ergebnis: Version 2 wird verlängert
     */
    @Test
    public void scenario12() {
        // given
        version1 = versionRepository.save(version1);
        version2 = versionRepository.save(version2);
        ServicePointVersion editedVersion = new ServicePointVersion();
        editedVersion.setValidFrom(version2.getValidFrom());
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
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
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(firstTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(firstTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(firstTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(firstTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(firstTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
        assertThat(firstTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.BUS));
        assertThat(firstTemporalVersion.getComment()).isNull();

        // second version no changes
        ServicePointVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(secondTemporalVersion.getNumber()).isEqualTo(SPN);
        assertThat(secondTemporalVersion.getStatus()).isEqualTo(Status.VALIDATED);
        assertThat(secondTemporalVersion.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100626");
        assertThat(secondTemporalVersion.getSloid()).isEqualTo("ch:1:sloid:89008");
        assertThat(secondTemporalVersion.getDesignationOfficial()).isEqualTo("Bern, Thunplatz");
        assertThat(secondTemporalVersion.getMeansOfTransport()).isEqualTo(Set.of(MeanOfTransport.CABLE_CAR));
        assertThat(secondTemporalVersion.getComment()).isNull();
    }

}
