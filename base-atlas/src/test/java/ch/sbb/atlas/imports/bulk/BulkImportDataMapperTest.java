package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BulkImportDataMapperTest {

  private final DummyBulkImportDataMapper mapper = new DummyBulkImportDataMapper();

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
    UpdateServicePointVersionModel currentEntity = new UpdateServicePointVersionModel();
    currentEntity.setCategories(Collections.emptyList());
    currentEntity.setDesignationOfficial("Bern, Wyleregg");

    UpdateServicePointVersionModel result = mapper.applyUpdate(container, currentEntity, new UpdateServicePointVersionModel());
    assertThat(result.getDesignationLong()).isEqualTo("Bern, am Wyleregg");
    assertThat(result.getMeansOfTransport()).containsExactly(MeanOfTransport.BUS);
    assertThat(result.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldApplyAdditionalDefaultMapping() {
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
    UpdateServicePointVersionModel currentEntity = new UpdateServicePointVersionModel();
    currentEntity.setId(4L);

    UpdateServicePointVersionModel result = mapper.applyUpdate(container, currentEntity, new UpdateServicePointVersionModel());
    assertThat(result.getId()).isEqualTo(4L);
  }

  @Test
  void shouldApplyNullingAfterDefaultMapping() {
    BulkImportUpdateContainer<ServicePointUpdateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("sloid")
                .validFrom(LocalDate.of(2014, 12, 14))
                .validTo(LocalDate.of(2021, 3, 31))
                .meansOfTransport(Set.of(MeanOfTransport.BUS))
                .build())
            .attributesToNull(List.of(Fields.height, Fields.categories, Fields.designationLong))
            .build();
    UpdateServicePointVersionModel currentEntity =
        UpdateServicePointVersionModel.builder()
            .categories(List.of(Category.HOSTNAME))
            .designationLong("Bern, am Wyleregg")
            .servicePointGeolocation(GeolocationBaseCreateModel.builder()
                .height(15.0)
                .build())
            .build();

    UpdateServicePointVersionModel result = mapper.applyUpdate(container, currentEntity, new UpdateServicePointVersionModel());
    assertThat(result.getMeansOfTransport()).containsExactly(MeanOfTransport.BUS);

    assertThat(result.getServicePointGeolocation().getHeight()).isNull();
    assertThat(result.getCategories()).isEmpty();
    assertThat(result.getDesignationLong()).isNull();
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
    UpdateServicePointVersionModel currentEntity = UpdateServicePointVersionModel.builder().build();

    assertThatExceptionOfType(AttributeNullingNotSupportedException.class).isThrownBy(() -> mapper.applyUpdate(container,
        currentEntity, new UpdateServicePointVersionModel()));
  }

  private static class DummyBulkImportDataMapper extends BulkImportDataMapper<ServicePointUpdateCsvModel,
      UpdateServicePointVersionModel, UpdateServicePointVersionModel> {

  }

}