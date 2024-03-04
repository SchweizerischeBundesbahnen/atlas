package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ServicePointCsvServiceTest {

  private ServicePointCsvService servicePointCsvService;

  @Mock
  private FileHelperService fileHelperService;

  @Mock
  private JobHelperService jobHelperService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    servicePointCsvService = new ServicePointCsvService(fileHelperService, jobHelperService);
  }

  @Test
  void shouldMapToServicePointCsvModelContainersWithPreMerge() {
    // given
    List<ServicePointCsvModel> csvModels = List.of(
        ServicePointCsvModel.builder()
            .didokCode(8507000)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .height(500.88)
            .isVirtuell(true)
            .build(),
        ServicePointCsvModel.builder()
            .didokCode(9608000)
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .height(500.88)
            .isVirtuell(true)
            .build(),
        ServicePointCsvModel.builder()
            .didokCode(8507000)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .height(500.88)
            .isVirtuell(true)
            .build()
    );

    // when
    final List<ServicePointCsvModelContainer> servicePointCsvModelContainers =
        servicePointCsvService.mapToServicePointCsvModelContainers(csvModels);

    // then
    assertThat(servicePointCsvModelContainers).hasSize(2);
    assertThat(servicePointCsvModelContainers.get(1).getDidokCode()).isEqualTo(8507000);
    assertThat(servicePointCsvModelContainers.get(1).getServicePointCsvModelList()).hasSize(1);
    assertThat(servicePointCsvModelContainers.get(1).getServicePointCsvModelList().get(0).getDidokCode()).isEqualTo(8507000);
    assertThat(servicePointCsvModelContainers.get(1).getServicePointCsvModelList().get(0).getHeight()).isEqualTo(500.88);
    assertThat(servicePointCsvModelContainers.get(1).getServicePointCsvModelList().get(0).getValidFrom()).isEqualTo(
        LocalDate.of(2020, 1, 1));
    assertThat(servicePointCsvModelContainers.get(1).getServicePointCsvModelList().get(0).getValidTo()).isEqualTo(
        LocalDate.of(2021, 12, 31));

    assertThat(servicePointCsvModelContainers.get(0).getDidokCode()).isEqualTo(9608000);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList()).hasSize(1);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getDidokCode()).isEqualTo(9608000);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getHeight()).isEqualTo(500.88);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getValidFrom()).isEqualTo(
        LocalDate.of(2022, 1, 1));
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getValidTo()).isEqualTo(
        LocalDate.of(2022, 12, 31));
  }

  @Test
  void shouldSetReplaceHighDateDataForServicePoints() {
    // given
    List<ServicePointCsvModel> csvModels = List.of(
        ServicePointCsvModel.builder()
            .didokCode(8507000)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .editedAt(LocalDateTime.of(2020, 1, 1, 15, 15))
            .height(500.88)
            .isVirtuell(false)
            .build(),
        ServicePointCsvModel.builder()
            .didokCode(8507000)
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(ImportUtils.DIDOK_HIGEST_DATE)
            .editedAt(LocalDateTime.of(2020, 1, 1, 15, 15))
            .height(500.88)
            .isVirtuell(false)
            .build()
    );
    LocalDate now = LocalDate.now();

    // when
    final List<ServicePointCsvModelContainer> servicePointCsvModelContainers =
        servicePointCsvService.mapToServicePointCsvModelContainers(csvModels);

    // then
    assertThat(servicePointCsvModelContainers).hasSize(1);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList()).hasSize(1);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getValidTo()).isEqualTo(ImportUtils.ATLAS_HIGHEST_DATE);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getEditedAt().toLocalDate()).isEqualTo(now);
  }

  @Test
  void shouldSetReplaceNewLineForServicePoints() {
    // given
    List<ServicePointCsvModel> csvModels = List.of(
        ServicePointCsvModel.builder()
            .didokCode(8507000)
            .oeffnungsBedingung("Desc $newline$ of $newline$ this model.")
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .editedAt(LocalDateTime.of(2020, 1, 1, 15, 15))
            .height(500.88)
            .isVirtuell(false)
            .build(),
        ServicePointCsvModel.builder()
            .didokCode(8507000)
            .oeffnungsBedingung("Desc $newline$ of $newline$ this model.")
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .editedAt(LocalDateTime.of(2020, 1, 1, 15, 15))
            .height(500.88)
            .isVirtuell(false)
            .build()
    );
    LocalDate now = LocalDate.now();

    // when
    final List<ServicePointCsvModelContainer> servicePointCsvModelContainers =
        servicePointCsvService.mapToServicePointCsvModelContainers(csvModels);

    // then
    assertThat(servicePointCsvModelContainers).hasSize(1);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList()).hasSize(1);
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getOeffnungsBedingung()).isEqualTo("Desc \r\n of \r\n this model.");
    assertThat(servicePointCsvModelContainers.get(0).getServicePointCsvModelList().get(0).getEditedAt().toLocalDate()).isEqualTo(now);
  }

}
