package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import org.junit.jupiter.api.Test;

class BulkImportUpdateDataMapperTest {

  private static class MockMapper extends
      BulkImportUpdateDataMapper<ServicePointUpdateCsvModel, BaseEntity, UpdateServicePointVersionModel> {

  }

  @Builder
  private static class MockEntity extends BaseEntity {

    private LocalDate validFrom;
    private LocalDate validTo;
    private String designationOfficial;
    private String designationLong;
    private StopPointType stopPointType;
    private boolean freightServicePoint;
    private OperatingPointType operatingPointType;
    private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;
    private Set<MeanOfTransport> meansOfTransport;
    private Set<Category> categories;
    private OperatingPointTrafficPointType operatingPointTrafficPointType;
    private String sortCodeOfDestinationStation;
    private String businessOrganisation;
    private Long id;
  }

  @Test
  void shouldApplyDefaultMapping() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("sloid")
                .validFrom(LocalDate.of(2014, 12, 14))
                .validTo(LocalDate.of(2021, 3, 31))
                .designationLong("Bern, am Wyleregg")
                .meansOfTransport(Set.of(MeanOfTransport.BUS))
                .build())
            .build();
    MockEntity currentEntity = MockEntity.builder()
        .categories(Collections.emptySet())
        .designationOfficial("Bern, Wyleregg")
        .build();

    UpdateServicePointVersionModel result = new MockMapper().applyUpdate(container, currentEntity,
        new UpdateServicePointVersionModel());
    assertThat(result.getDesignationLong()).isEqualTo("Bern, am Wyleregg");
    assertThat(result.getMeansOfTransport()).containsExactly(MeanOfTransport.BUS);
    assertThat(result.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldApplyCopyFromCurrentVersion() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("sloid")
                .validFrom(LocalDate.of(2014, 12, 14))
                .validTo(LocalDate.of(2021, 3, 31))
                .designationLong("Bern, am Wyleregg")
                .meansOfTransport(Set.of(MeanOfTransport.BUS))
                .build())
            .build();
    MockEntity currentEntity = MockEntity.builder().id(4L).build();

    UpdateServicePointVersionModel result = new MockMapper().applyUpdate(container, currentEntity,
        new UpdateServicePointVersionModel());
    assertThat(result.getId()).isEqualTo(4L);
  }

  @Test
  void shouldApplyNullingAndThrowExceptionOnUnallowedProperty() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("sloid")
                .validFrom(LocalDate.of(2014, 12, 14))
                .validTo(LocalDate.of(2021, 3, 31))
                .meansOfTransport(Set.of(MeanOfTransport.BUS))
                .build())
            .attributesToNull(List.of(Fields.businessOrganisation))
            .build();
    MockEntity currentEntity = MockEntity.builder().build();

    assertThatExceptionOfType(AttributeNullingNotSupportedException.class).isThrownBy(
        () -> new MockMapper().applyUpdate(container, currentEntity, new UpdateServicePointVersionModel()));
  }

  @Test
  void shouldApplyNullingWithProperNestedPathHandling() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("sloid")
                .validFrom(LocalDate.of(2014, 12, 14))
                .validTo(LocalDate.of(2021, 3, 31))
                .meansOfTransport(Set.of(MeanOfTransport.BUS))
                .build())
            .attributesToNull(List.of(Fields.north, Fields.height))
            .build();
    MockEntity currentEntity = MockEntity.builder().build();

    UpdateServicePointVersionModel result = new MockMapper().applyUpdate(container, currentEntity,
        new UpdateServicePointVersionModel());

    assertThat(result.getServicePointGeolocation()).isNull();
  }
}
