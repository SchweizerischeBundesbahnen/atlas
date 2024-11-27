package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class BulkImportCreateDataMapperTest {

  private final DummyBulkImportCreateDataMapper mapper = new DummyBulkImportCreateDataMapper();

  @Test
  void shouldApplyDefaultMapping() {
    BulkImportUpdateContainer<ServicePointCreateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointCreateCsvModel>builder()
            .object(ServicePointCreateCsvModel.builder()
                .numberShort(6000)
                .businessOrganisation("ch:1:sboid:100001")
                .validFrom(LocalDate.of(2014, 12, 14))
                .validTo(LocalDate.of(2021, 3, 31))
                .designationOfficial("BulkImportCreate")
                .meansOfTransport(Set.of(MeanOfTransport.BUS))
                .build())
            .build();

    CreateServicePointVersionModel result = mapper.applyCreate(container, new CreateServicePointVersionModel());
    assertThat(result.getDesignationOfficial()).isEqualTo("BulkImportCreate");
    assertThat(result.getMeansOfTransport()).containsExactly(MeanOfTransport.BUS);
  }

  @Test
  void shouldApplyNullingAfterDefaultMapping() {
    BulkImportUpdateContainer<ServicePointCreateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointCreateCsvModel>builder()
            .object(ServicePointCreateCsvModel.builder()
                    .numberShort(6000)
                    .businessOrganisation("ch:1:sboid:100001")
                    .validFrom(LocalDate.of(2014, 12, 14))
                    .validTo(LocalDate.of(2021, 3, 31))
                    .designationOfficial("BulkImportCreate")
                    .meansOfTransport(Set.of(MeanOfTransport.BUS))
                    .build())
            .attributesToNull(List.of(Fields.height, Fields.categories, Fields.designationLong))
            .build();

    CreateServicePointVersionModel result = mapper.applyCreate(container, new CreateServicePointVersionModel());
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

  private static class DummyBulkImportCreateDataMapper extends BulkImportCreateDataMapper<ServicePointCreateCsvModel,
          CreateServicePointVersionModel> {

  }

}