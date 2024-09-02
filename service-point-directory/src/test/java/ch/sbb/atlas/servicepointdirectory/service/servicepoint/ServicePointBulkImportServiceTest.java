package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointFotCommentRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
class ServicePointBulkImportServiceTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  private ServicePointFotCommentRepository servicePointFotCommentRepository;

  @Autowired
  private ServicePointBulkImportService servicePointBulkImportService;

  private ServicePointVersion bernWyleregg;

  @BeforeEach
  void setUp() {
    bernWyleregg = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
  }

  @AfterEach
  void tearDown() {
    servicePointFotCommentRepository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldUpdateBulkAddingProperty() {
    assertThat(bernWyleregg.getDesignationLong()).isNull();

    servicePointBulkImportService.updateServicePoint(BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
        .object(ServicePointUpdateCsvModel.builder()
            .sloid(bernWyleregg.getSloid())
            .validFrom(bernWyleregg.getValidFrom())
            .validTo(bernWyleregg.getValidTo())
            .designationLong("Bern, am Wyleregg")
            .build())
        .build());

    ServicePointVersion bulkUpdateResult = servicePointVersionRepository.findById(bernWyleregg.getId()).orElseThrow();
    assertThat(bulkUpdateResult.getDesignationLong()).isEqualTo("Bern, am Wyleregg");
  }

  @Test
  void shouldUpdateBulkRemovingProperty() {
    assertThat(bernWyleregg.getServicePointGeolocation().getHeight()).isEqualTo(555D);

    servicePointBulkImportService.updateServicePoint(BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
        .object(ServicePointUpdateCsvModel.builder()
            .sloid(bernWyleregg.getSloid())
            .validFrom(bernWyleregg.getValidFrom())
            .validTo(bernWyleregg.getValidTo())
            .build())
        .attributesToNull(List.of(Fields.height))
        .build());

    ServicePointVersion bulkUpdateResult = servicePointVersionRepository.findById(bernWyleregg.getId()).orElseThrow();
    assertThat(bulkUpdateResult.getServicePointGeolocation().getHeight()).isNull();
  }

  @Test
  void shouldUpdateBulkUpdatingSet() {
    assertThat(bernWyleregg.getMeansOfTransport()).hasSize(1);

    servicePointBulkImportService.updateServicePoint(BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
        .object(ServicePointUpdateCsvModel.builder()
            .sloid(bernWyleregg.getSloid())
            .validFrom(bernWyleregg.getValidFrom())
            .validTo(bernWyleregg.getValidTo())
            .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAM))
            .build())
        .build());

    ServicePointVersion bulkUpdateResult = servicePointVersionRepository.findById(bernWyleregg.getId()).orElseThrow();
    assertThat(bulkUpdateResult.getMeansOfTransport()).hasSize(2);
  }

  @Test
  void shouldUpdateBulkRemovingSet() {
    assertThat(bernWyleregg.getMeansOfTransport()).hasSize(1);

    servicePointBulkImportService.updateServicePoint(BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
        .object(ServicePointUpdateCsvModel.builder()
            .sloid(bernWyleregg.getSloid())
            .validFrom(bernWyleregg.getValidFrom())
            .validTo(bernWyleregg.getValidTo())
            .build())
        .attributesToNull(List.of(Fields.meansOfTransport))
        .build());

    ServicePointVersion bulkUpdateResult = servicePointVersionRepository.findById(bernWyleregg.getId()).orElseThrow();
    assertThat(bulkUpdateResult.getMeansOfTransport()).isEmpty();
  }

  @Test
  void shouldUpdateAndGetMoreVersions() {
    assertThat(servicePointVersionRepository.findBySloidOrderByValidFrom(bernWyleregg.getSloid())).hasSize(1);

    servicePointBulkImportService.updateServicePoint(BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
        .object(ServicePointUpdateCsvModel.builder()
            .sloid(bernWyleregg.getSloid())
            .validFrom(LocalDate.of(2015, 12, 14))
            .validTo(LocalDate.of(2020, 12, 14))
            .designationOfficial("BERN - WYLEREGG")
            .build())
        .build());

    List<ServicePointVersion> versions = servicePointVersionRepository.findBySloidOrderByValidFrom(bernWyleregg.getSloid());
    assertThat(versions).hasSize(3);

    ServicePointVersion firstVersion = versions.getFirst();
    assertThat(firstVersion.getValidFrom()).isEqualTo(LocalDate.of(2014, 12, 14));
    assertThat(firstVersion.getValidTo()).isEqualTo(LocalDate.of(2015, 12, 13));
    assertThat(firstVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");

    ServicePointVersion secondVersion = versions.get(1);
    assertThat(secondVersion.getValidFrom()).isEqualTo(LocalDate.of(2015, 12, 14));
    assertThat(secondVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 12, 14));
    assertThat(secondVersion.getDesignationOfficial()).isEqualTo("BERN - WYLEREGG");

    ServicePointVersion thirdVersion = versions.getLast();
    assertThat(thirdVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 12, 15));
    assertThat(thirdVersion.getValidTo()).isEqualTo(LocalDate.of(2021, 3, 31));
    assertThat(thirdVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldThrowSloidNotFoundException() {
    ThrowingCallable update = () -> servicePointBulkImportService.updateServicePoint(
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("unknown:sloid")
                .validFrom(LocalDate.of(2015, 12, 14))
                .validTo(LocalDate.of(2020, 12, 14))
                .designationOfficial("BERN - WYLEREGG")
                .build())
            .build());
    assertThatExceptionOfType(SloidNotFoundException.class).isThrownBy(update);
  }

  @Test
  void shouldUpdateBulkAddingPropertyViaNumber() {
    assertThat(bernWyleregg.getDesignationLong()).isNull();

    servicePointBulkImportService.updateServicePoint(BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
        .object(ServicePointUpdateCsvModel.builder()
            .number(bernWyleregg.getNumber().getNumber())
            .validFrom(bernWyleregg.getValidFrom())
            .validTo(bernWyleregg.getValidTo())
            .designationLong("Bern, am Wyleregg")
            .build())
        .build());

    ServicePointVersion bulkUpdateResult = servicePointVersionRepository.findById(bernWyleregg.getId()).orElseThrow();
    assertThat(bulkUpdateResult.getDesignationLong()).isEqualTo("Bern, am Wyleregg");
  }

  @Test
  void shouldThrowNumberNotFoundException() {
    ThrowingCallable update = () -> servicePointBulkImportService.updateServicePoint(
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .number(1234567)
                .validFrom(LocalDate.of(2015, 12, 14))
                .validTo(LocalDate.of(2020, 12, 14))
                .designationOfficial("BERN - WYLEREGG")
                .build())
            .build());
    assertThatExceptionOfType(ServicePointNumberNotFoundException.class).isThrownBy(update);
  }
}