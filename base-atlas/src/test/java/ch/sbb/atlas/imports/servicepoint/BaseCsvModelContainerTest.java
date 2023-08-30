package ch.sbb.atlas.imports.servicepoint;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BaseCsvModelContainerTest {

  @Test
  void shouldMergeWhenDatesAreSequentialAndModelsAreEqual() {
    // given
    TrafficPointCsvModelContainer container = TrafficPointCsvModelContainer.builder()
        .sloid("ch:1:sloid:123")
        .csvModelList(new ArrayList<>(
            List.of(
                TrafficPointElementCsvModel.builder()
                    .validFrom(LocalDate.of(2020, 1, 1))
                    .validTo(LocalDate.of(2020, 12, 31))
                    .servicePointNumber(1234567)
                    .height(100.12)
                    .build(),
                TrafficPointElementCsvModel.builder()
                    .validFrom(LocalDate.of(2021, 1, 1))
                    .validTo(LocalDate.of(2021, 12, 31))
                    .height(500.88)
                    .servicePointNumber(1234567)
                    .build(),
                TrafficPointElementCsvModel.builder()
                    .validFrom(LocalDate.of(2022, 1, 1))
                    .validTo(LocalDate.of(2022, 12, 31))
                    .servicePointNumber(1234567)
                    .height(500.88)
                    .build(),
                TrafficPointElementCsvModel.builder()
                    .validFrom(LocalDate.of(2023, 1, 1))
                    .validTo(LocalDate.of(2023, 12, 31))
                    .height(500.88)
                    .servicePointNumber(1234567)
                    .build()
            ))
        )
        .build();

    // when
    container.mergeWhenDatesAreSequentialAndModelsAreEqual();

    // then
    assertThat(container.getCsvModelList()).hasSize(2);
    assertThat(container.getCsvModelList().get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(container.getCsvModelList().get(0).getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(container.getCsvModelList().get(0).getHeight()).isEqualTo(100.12);

    assertThat(container.getCsvModelList().get(1).getValidFrom()).isEqualTo(LocalDate.of(2021, 1, 1));
    assertThat(container.getCsvModelList().get(1).getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    assertThat(container.getCsvModelList().get(1).getHeight()).isEqualTo(500.88);
  }
}
