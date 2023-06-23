package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class TrafficPointElementServiceTest {

  private final TrafficPointElementService trafficPointElementService;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  TrafficPointElementServiceTest(TrafficPointElementService trafficPointElementService,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.trafficPointElementService = trafficPointElementService;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @AfterEach
  void cleanup() {
    trafficPointElementVersionRepository.deleteAll();
  }

  @Test
  void shouldMergeTrafficPoint() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    TrafficPointElementVersion edited = TrafficPointTestData.getBasicTrafficPoint();
    edited.setValidFrom(LocalDate.of(2024, 1, 2));
    edited.setValidTo(LocalDate.of(2024, 12, 31));
    edited.getTrafficPointElementGeolocation().setCreationDate(null);
    edited.getTrafficPointElementGeolocation().setCreator(null);
    edited.getTrafficPointElementGeolocation().setEditionDate(null);
    edited.getTrafficPointElementGeolocation().setEditor(null);
    // when
    trafficPointElementService.updateTrafficPointElementVersion(edited);

    // then
    assertThat(trafficPointElementService.findBySloidOrderByValidFrom("ch:1:sloid:123")).hasSize(1);
  }

  @Test
  void shouldUpdateTrafficPointElementVersionImport_withImportVersioning_andSetParentPropertiesOnGeolocation() {
    // given
    TrafficPointElementVersion savedVersion = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementService.save(savedVersion);

    TrafficPointElementVersion editedVersion = TrafficPointTestData.getBasicTrafficPoint();
    editedVersion.setValidFrom(LocalDate.of(2024, 1, 2));
    editedVersion.setValidTo(LocalDate.of(2024, 12, 31));
    editedVersion.getTrafficPointElementGeolocation().setHeight(100D);

    editedVersion.getTrafficPointElementGeolocation().setCreator("test");
    editedVersion.getTrafficPointElementGeolocation().setCreationDate(
        LocalDateTime.of(LocalDate.of(1900, 1, 1), LocalTime.of(1, 1, 1))
    );
    editedVersion.getTrafficPointElementGeolocation().setEditor("test2");
    editedVersion.getTrafficPointElementGeolocation().setEditionDate(
        LocalDateTime.of(LocalDate.of(1900, 1, 1), LocalTime.of(1, 1, 1))
    );

    // when
    trafficPointElementService.updateTrafficPointElementVersionImport(editedVersion);

    // then
    List<TrafficPointElementVersion> trafficPointElements = trafficPointElementService.findBySloidOrderByValidFrom(
        "ch:1:sloid:123");
    assertThat(trafficPointElements).hasSize(2);
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getCreator()).isEqualTo("fs45117");
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getCreationDate())
        .isEqualTo(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)));
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getEditor()).isEqualTo("fs45117");
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getEditionDate())
        .isEqualTo(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)));
  }

}
