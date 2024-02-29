package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class TrafficPointCsvServiceTest {

  private TrafficPointCsvService trafficPointCsvService;

  @Mock
  private FileHelperService fileHelperService;

  @Mock
  private JobHelperService jobHelperService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    trafficPointCsvService = new TrafficPointCsvService(fileHelperService, jobHelperService);
  }

  @Test
  void shouldMapToTrafficPointCsvModelContainersWithPreMerge() {
    // given
    List<TrafficPointElementCsvModel> csvModels = List.of(
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:123")
            .servicePointNumber(8507000)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .height(500.88)
            .build(),
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:567")
            .servicePointNumber(8507000)
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .height(500.88)
            .build(),
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:567")
            .servicePointNumber(8507000)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .height(500.88)
            .build()
    );

    // when
    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers =
        trafficPointCsvService.mapToTrafficPointCsvModelContainers(
        csvModels);

    // then
    assertThat(trafficPointCsvModelContainers).hasSize(2);
    assertThat(trafficPointCsvModelContainers.get(0).getSloid()).isEqualTo("ch:1:sloid:123");
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList()).hasSize(1);
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList().get(0).getSloid()).isEqualTo("ch:1:sloid:123");

    assertThat(trafficPointCsvModelContainers.get(1).getSloid()).isEqualTo("ch:1:sloid:567");
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList()).hasSize(1);
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getSloid()).isEqualTo("ch:1:sloid:567");
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getValidFrom()).isEqualTo(
        LocalDate.of(2021, 1, 1));
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getValidTo()).isEqualTo(
        LocalDate.of(2022, 12, 31));
  }

  @Test
  void shouldSetReplaceHighDateDataForTrafficPoints() {
    // given
    List<TrafficPointElementCsvModel> csvModels = List.of(
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:123")
            .servicePointNumber(8507000)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .editedAt(LocalDateTime.of(2020,1,1,15,15))
            .height(800.88)
            .build(),
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:567")
            .servicePointNumber(8507000)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2099, 12, 31))
            .editedAt(LocalDateTime.of(2020,1,1,15,15))
            .height(500.88)
            .build()
    );
    LocalDate now = LocalDate.now();

    // when
    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers =
        trafficPointCsvService.mapToTrafficPointCsvModelContainers(
            csvModels);

    // then
    assertThat(trafficPointCsvModelContainers).hasSize(2);
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList()).hasSize(1);
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList().get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList().get(0).getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList().get(0).getEditedAt())
        .isEqualTo(LocalDateTime.of(2020,1,1,15,15));
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList()).hasSize(1);
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getValidFrom()).isEqualTo(LocalDate.of(2021, 1, 1));
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getEditedAt().toLocalDate())
        .isEqualTo(now);
  }

  @Test
  void shouldSetReplaceNewLineForTrafficPoints() {
    // given
    List<TrafficPointElementCsvModel> csvModels = List.of(
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:123")
            .designationOperational("Desc $newline$ of $newline$ this model.")
            .servicePointNumber(8507000)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .editedAt(LocalDateTime.of(2020,1,1,15,15))
            .height(800.88)
            .build(),
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:567")
            .designationOperational("Desc $newline$ of $newline$ this model.")
            .servicePointNumber(8507000)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .editedAt(LocalDateTime.of(2020,1,1,15,15))
            .height(500.88)
            .build()
    );
    LocalDate now = LocalDate.now();

    // when
    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers =
        trafficPointCsvService.mapToTrafficPointCsvModelContainers(
            csvModels);

    // then
    assertThat(trafficPointCsvModelContainers).hasSize(2);
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList()).hasSize(1);
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList().get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList().get(0).getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList().get(0).getDesignationOperational()).isEqualTo("Desc \r\n of \r\n this model.");
    assertThat(trafficPointCsvModelContainers.get(0).getCsvModelList().get(0).getEditedAt().toLocalDate()).isEqualTo(now);
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList()).hasSize(1);
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getValidFrom()).isEqualTo(LocalDate.of(2021, 1, 1));
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getDesignationOperational()).isEqualTo("Desc \r\n"
        + " of \r\n this model.");
    assertThat(trafficPointCsvModelContainers.get(1).getCsvModelList().get(0).getEditedAt().toLocalDate()).isEqualTo(now);
  }

}
