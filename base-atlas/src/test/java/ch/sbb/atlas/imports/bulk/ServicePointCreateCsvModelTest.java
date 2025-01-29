package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ServicePointCreateCsvModelTest {

  @Test
  void shouldBeValidServicePointCreateModelSwitzerland() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(85)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .businessOrganisation("abc")
        .build();
    assertThat(bern.validate()).isEmpty();
  }

  @Test
  void shouldReportErrorsInServicePointCreateModel() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .build();
    assertThat(bern.validate()).hasSize(6);
  }

  @Test
  void shouldReportInvalidServicePointMissingBO() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(85)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .build();
    assertThat(bern.validate()).hasSize(1);
  }

  @Test
  void shouldReportInvalidServicePointMissingNumberShort() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(81)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .businessOrganisation("abc")
        .build();
    assertThat(bern.validate()).hasSize(1);
  }

  @Test
  void shouldBeValidServicePointCreateModel() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(81)
        .numberShort(12345)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .businessOrganisation("abc")
        .build();
    assertThat(bern.validate()).isEmpty();
  }

  @Test
  void shouldValidateNumberShort() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(81)
        .numberShort(44444444)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .businessOrganisation("abc")
        .build();

    assertThat(bern.validate()).hasSize(1);

  }

  @Test
  void shouldValidateUicCountryCode() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(35)
        .numberShort(12345)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .businessOrganisation("abc")
        .build();

    assertThat(bern.validate()).hasSize(1);
  }

  @Test
  void shouldValidateHeight() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(85)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .businessOrganisation("abc")
        .spatialReference(SpatialReference.LV95)
        .north(1207167.52455)
        .east(2601352.9171)
        .height(100000000.0)
        .build();

    assertThat(bern.validate()).hasSize(1);
  }

  @Test
  void shouldValidateGeography() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(85)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .businessOrganisation("abc")
        .spatialReference(null)
        .north(1207167.52455)
        .east(2601352.9171)
        .height(500.0)
        .build();

    assertThat(bern.validate()).hasSize(1);
  }

  @Test
  void shouldValidateStopPointType() {
    ServicePointCreateCsvModel bern = ServicePointCreateCsvModel.builder()
        .uicCountryCode(85)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .businessOrganisation("abc")
        .stopPointType(StopPointType.ORDERLY)
        .meansOfTransport(null)
        .build();

    assertThat(bern.validate()).hasSize(1);
  }

  @Test
  void checkUniqueFields() {
    ServicePointCreateCsvModel model = ServicePointCreateCsvModel.builder().build();
    assertThat(model.uniqueFields()).hasSize(1);
    assertThat(model.uniqueFields().get(0).getField()).isEqualTo("number");
    assertThat(model.uniqueFields().get(0).getFieldValueExtractor()).isNotNull();
  }

  @Test
  void shouldReturnNullOnGetNumberIfCountryNotValid() {
    ServicePointCreateCsvModel model = ServicePointCreateCsvModel.builder().uicCountryCode(150).build();
    assertThat(model.getNumber()).isNull();
  }

  @Test
  void shouldReturnNullOnGetNumberIfNumberShortNotValid() {
    ServicePointCreateCsvModel model = ServicePointCreateCsvModel.builder().uicCountryCode(85).build();
    assertThat(model.getNumber()).isNull();
  }

  @Test
  void shouldReturnNumber() {
    ServicePointCreateCsvModel model = ServicePointCreateCsvModel.builder().uicCountryCode(85).numberShort(1).build();
    assertThat(model.getNumber()).isEqualTo("8500001");
  }

}
