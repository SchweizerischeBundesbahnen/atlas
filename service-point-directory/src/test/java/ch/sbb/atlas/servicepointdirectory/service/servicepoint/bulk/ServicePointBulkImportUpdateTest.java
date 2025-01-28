package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicePointBulkImportUpdateTest {

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
            .attributesToNull(List.of(Fields.north, Fields.height, Fields.categories, Fields.designationLong))
            .build();
    ServicePointVersion currentEntity =
        ServicePointVersion.builder()
            .categories(Set.of(Category.HOSTNAME))
            .designationLong("Bern, am Wyleregg")
            .servicePointGeolocation(ServicePointGeolocation.builder()
                .height(15.0)
                .build())
            .build();

    UpdateServicePointVersionModel result = ServicePointBulkImportUpdate.apply(container, currentEntity);

    assertThat(result.getMeansOfTransport()).containsExactly(MeanOfTransport.BUS);
    assertThat(result.getServicePointGeolocation()).isNull();
    assertThat(result.getCategories()).isEmpty();
    assertThat(result.getDesignationLong()).isNull();
  }

}
