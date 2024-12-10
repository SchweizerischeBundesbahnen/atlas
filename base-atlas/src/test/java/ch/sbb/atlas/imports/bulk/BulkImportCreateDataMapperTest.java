package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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

  private static class DummyBulkImportCreateDataMapper extends BulkImportCreateDataMapper<ServicePointCreateCsvModel,
          CreateServicePointVersionModel> {

  }

}