package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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
    void shouldReportErrorsInServicePointUpdateModel() {
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
}
