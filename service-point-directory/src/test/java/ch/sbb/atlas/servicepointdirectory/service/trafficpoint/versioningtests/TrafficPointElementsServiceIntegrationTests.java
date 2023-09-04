package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.versioningtests;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TrafficPointElementsServiceIntegrationTests extends BaseTrafficPointElementsServiceIntegrationTest{

    @Autowired
    public TrafficPointElementsServiceIntegrationTests(TrafficPointElementVersionRepository trafficPointElementVersionRepository, TrafficPointElementService trafficPointElementService) {
        super(trafficPointElementVersionRepository, trafficPointElementService);
    }

    @Test
    public void scenario3UpdateVersion2ByAddingTrafficPointElementGeolocation() {
        // given
        version1 = trafficPointElementVersionRepository.save(version1);
        version2 = trafficPointElementVersionRepository.save(version2);
        version3 = trafficPointElementVersionRepository.save(version3);
        TrafficPointElementVersion editedVersion = version2Builder().build();
        editedVersion.setTrafficPointElementGeolocation(TrafficPointTestData.getTrafficPointGeolocationBernMittelland());
        editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
        // when
        trafficPointElementService.updateTrafficPointElementVersion(version2, editedVersion);
        List<TrafficPointElementVersion> result = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(SLOID);

        // then
        assertThat(result).isNotNull().hasSize(5);
        result.sort(Comparator.comparing(TrafficPointElementVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();
        assertThat(result.get(3)).isNotNull();
        assertThat(result.get(4)).isNotNull();

        // not touched
        TrafficPointElementVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(firstTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(firstTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(firstTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(firstTemporalVersion.getCompassDirection()).isEqualTo(271.0);

        // updated
        TrafficPointElementVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 5, 31));
        assertThat(secondTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(secondTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(secondTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(secondTemporalVersion.getCompassDirection()).isEqualTo(272.0);

        // new
        TrafficPointElementVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(thirdTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(thirdTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(thirdTemporalVersion.getCompassDirection()).isEqualTo(272.0);
        assertThat(thirdTemporalVersion.getTrafficPointElementGeolocation().getSpatialReference()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getSpatialReference());
        assertThat(thirdTemporalVersion.getTrafficPointElementGeolocation().getEast()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getEast());
        assertThat(thirdTemporalVersion.getTrafficPointElementGeolocation().getNorth()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getNorth());
        assertThat(thirdTemporalVersion.getTrafficPointElementGeolocation().getHeight()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getHeight());

        // new
        TrafficPointElementVersion fourthTemporalVersion = result.get(3);
        assertThat(fourthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(fourthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(fourthTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(fourthTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(fourthTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(fourthTemporalVersion.getCompassDirection()).isEqualTo(273.0);
        assertThat(fourthTemporalVersion.getTrafficPointElementGeolocation().getSpatialReference()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getSpatialReference());
        assertThat(fourthTemporalVersion.getTrafficPointElementGeolocation().getEast()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getEast());
        assertThat(fourthTemporalVersion.getTrafficPointElementGeolocation().getNorth()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getNorth());
        assertThat(fourthTemporalVersion.getTrafficPointElementGeolocation().getHeight()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getHeight());

        // updated
        TrafficPointElementVersion fifthTemporalVersion = result.get(4);
        assertThat(fifthTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 6, 2));
        assertThat(fifthTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(fifthTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(fifthTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(fifthTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(fifthTemporalVersion.getCompassDirection()).isEqualTo(273.0);
    }

    @Test
    public void scenario3UpdateVersion3ByChangingTrafficPointElementGeolocationCreatorAndEditor() {
        // given
        version1 = trafficPointElementVersionRepository.save(version1);
        version2 = trafficPointElementVersionRepository.save(version2);
        TrafficPointElementGeolocation tpeg = TrafficPointTestData.getTrafficPointGeolocationBernMittelland();
        String initialCreator = "initialCreator";
        String initialEditor = "initialEditor";
        tpeg.setCreator(initialCreator);
        tpeg.setEditor(initialEditor);
        version3.setTrafficPointElementGeolocation(tpeg);
        version3 = trafficPointElementVersionRepository.save(version3);
        TrafficPointElementVersion editedVersion = version3Builder().build();
        TrafficPointElementGeolocation updatedTpeg = TrafficPointTestData.getTrafficPointGeolocationBernMittelland();
        String updatedCreator = "updatedCreator";
        String updatedEditor = "updatedEditor";
        updatedTpeg.setCreator(updatedCreator);
        updatedTpeg.setEditor(updatedEditor);
        editedVersion.setTrafficPointElementGeolocation(updatedTpeg);
        editedVersion.setValidFrom(LocalDate.of(2023, 6, 1));
        editedVersion.setValidTo(LocalDate.of(2024, 6, 1));
        // when
        trafficPointElementService.updateTrafficPointElementVersion(version3, editedVersion);
        List<TrafficPointElementVersion> result = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(SLOID);

        // then
        assertThat(result).isNotNull().hasSize(3);
        result.sort(Comparator.comparing(TrafficPointElementVersion::getValidFrom));
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(1)).isNotNull();
        assertThat(result.get(2)).isNotNull();

        // not touched
        TrafficPointElementVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
        assertThat(firstTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(firstTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(firstTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(firstTemporalVersion.getCompassDirection()).isEqualTo(271.0);

        // updated
        TrafficPointElementVersion secondTemporalVersion = result.get(1);
        assertThat(secondTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(secondTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(secondTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(secondTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(secondTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(secondTemporalVersion.getCompassDirection()).isEqualTo(272.0);

        // updated
        TrafficPointElementVersion thirdTemporalVersion = result.get(2);
        assertThat(thirdTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(thirdTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(thirdTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(thirdTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(thirdTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(thirdTemporalVersion.getCompassDirection()).isEqualTo(273.0);
        assertThat(thirdTemporalVersion.getTrafficPointElementGeolocation().getSpatialReference()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getSpatialReference());
        assertThat(thirdTemporalVersion.getTrafficPointElementGeolocation().getEast()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getEast());
        assertThat(thirdTemporalVersion.getTrafficPointElementGeolocation().getNorth()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getNorth());
        assertThat(thirdTemporalVersion.getTrafficPointElementGeolocation().getHeight()).isEqualTo(TrafficPointTestData.getTrafficPointGeolocationBernMittelland().getHeight());
    }

    @Test
    public void updateTrafficPointElementGeolocationCreatorOnExistingVersion() {
        // given
        version1 = trafficPointElementVersionRepository.save(version1);
        TrafficPointElementGeolocation tpeg = TrafficPointTestData.getTrafficPointGeolocationBernMittelland();
        String initialCreator = "initialCreator";
        String initialEditor = "initialEditor";
        tpeg.setCreator(initialCreator);
        tpeg.setEditor(initialEditor);
        version1.setTrafficPointElementGeolocation(tpeg);

        TrafficPointElementVersion editedVersion = version1Builder().build();
        TrafficPointElementGeolocation updatedTpeg = TrafficPointTestData.getTrafficPointGeolocationBernMittelland();
        String updatedCreator = "updatedCreator";
        updatedTpeg.setCreator(updatedCreator);
        editedVersion.setTrafficPointElementGeolocation(updatedTpeg);
        editedVersion.setValidFrom(LocalDate.of(2022, 1, 1));
        editedVersion.setValidTo(LocalDate.of(2022, 1, 2));
        // when
        trafficPointElementService.updateTrafficPointElementVersion(version1, editedVersion);
        List<TrafficPointElementVersion> result = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(SLOID);

        // then
        assertThat(result).isNotNull().hasSize(1);

        // not touched
        TrafficPointElementVersion firstTemporalVersion = result.get(0);
        assertThat(firstTemporalVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(firstTemporalVersion.getValidTo()).isEqualTo(LocalDate.of(2022, 1, 2));
        assertThat(firstTemporalVersion.getSloid()).isEqualTo(SLOID);
        assertThat(firstTemporalVersion.getDesignation()).isEqualTo("Bezeichnung");
        assertThat(firstTemporalVersion.getDesignationOperational()).isEqualTo("Betriebliche Bezeich");
        assertThat(firstTemporalVersion.getCompassDirection()).isEqualTo(271.0);
    }

}
