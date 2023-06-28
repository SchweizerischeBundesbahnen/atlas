package ch.sbb.atlas.imports.servicepoint.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TrafficPointCsvModelContainerTest {

  @Test
  void shouldMergeWhenDatesAreSequentialAndModelsAreEqual() {
    // given
    TrafficPointCsvModelContainer container = TrafficPointCsvModelContainer.builder()
        .sloid("ch:1:sloid:123")
        .trafficPointCsvModelList(new ArrayList<>(
            List.of(
                TrafficPointElementCsvModel.builder()
                    .validFrom(LocalDate.of(2020, 1, 1))
                    .validTo(LocalDate.of(2020, 12, 31))
                    .height(100.12)
                    .build(),
                TrafficPointElementCsvModel.builder()
                    .validFrom(LocalDate.of(2021, 1, 1))
                    .validTo(LocalDate.of(2021, 12, 31))
                    .height(500.88)
                    .build(),
                TrafficPointElementCsvModel.builder()
                    .validFrom(LocalDate.of(2022, 1, 1))
                    .validTo(LocalDate.of(2022, 12, 31))
                    .height(500.88)
                    .build()
            ))
        )
        .build();

    // when
    container.mergeWhenDatesAreSequentialAndModelsAreEqual();

    // then
    assertThat(container.getTrafficPointCsvModelList()).hasSize(2);
    assertThat(container.getTrafficPointCsvModelList().get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(container.getTrafficPointCsvModelList().get(0).getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(container.getTrafficPointCsvModelList().get(0).getHeight()).isEqualTo(100.12);

    assertThat(container.getTrafficPointCsvModelList().get(1).getValidFrom()).isEqualTo(LocalDate.of(2021, 1, 1));
    assertThat(container.getTrafficPointCsvModelList().get(1).getValidTo()).isEqualTo(LocalDate.of(2022, 12, 31));
    assertThat(container.getTrafficPointCsvModelList().get(1).getHeight()).isEqualTo(500.88);
  }
}
