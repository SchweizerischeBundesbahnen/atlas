package ch.sbb.business.organisation.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion.Fields;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class BusinessOrganisationConflictExceptionTest {

  private final BusinessOrganisationVersion version = BusinessOrganisationData.businessOrganisationVersion();
  private final BusinessOrganisationVersion version2 = BusinessOrganisationData.businessOrganisationVersionBuilder()
      .validFrom(LocalDate.of(1980, 1, 1))
      .validTo(LocalDate.of(2020, 1, 1))
      .build();

  @Test
  void shouldConvertToErrorMessageCorrectlyUnsorted() {
    // Given
    BusinessOrganisationConflictException conflictException = new BusinessOrganisationConflictException(
        version, List.of(version, version2));
    // When
    ErrorResponse errorResponse = conflictException.getErrorResponse();
    List<Detail> detailList = errorResponse.getDetails().stream().toList();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(detailList).hasSize(10);

    assertThat(detailList.get(0).getMessage()).isEqualTo(
        "abbreviationDe de already taken from 01.01.1980 to 01.01.2020 by ch:1:sboid:1000000");
    assertThat(detailList
        .get(0)
        .getDisplayInfo()
        .getParameters()
        .get(0)
        .getKey()).isEqualTo(BusinessOrganisationConflictException.FIELD);
    assertThat(detailList
        .get(0)
        .getDisplayInfo()
        .getParameters()
        .get(0)
        .getValue()).isEqualTo(Fields.abbreviationDe);
    assertThat(detailList
        .get(0)
        .getDisplayInfo()
        .getParameters()
        .get(1)
        .getKey()).isEqualTo(Fields.abbreviationDe);
    assertThat(detailList
        .get(0)
        .getDisplayInfo()
        .getParameters()
        .get(1)
        .getValue()).isEqualTo("de");

    assertThat(detailList.get(1).getMessage()).isEqualTo(
        "abbreviationEn en already taken from 01.01.1980 to 01.01.2020 by ch:1:sboid:1000000");

    assertThat(detailList.get(2).getMessage()).isEqualTo(
        "abbreviationFr fr already taken from 01.01.1980 to 01.01.2020 by ch:1:sboid:1000000");

    assertThat(detailList.get(3).getMessage()).isEqualTo(
        "abbreviationIt it already taken from 01.01.1980 to 01.01.2020 by ch:1:sboid:1000000");

    assertThat(detailList.get(4).getMessage()).isEqualTo(
        "organisationNumber 123 already taken from 01.01.1980 to 01.01.2020 by ch:1:sboid:1000000");

    assertThat(detailList.get(5).getMessage()).isEqualTo(
        "abbreviationDe de already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");

    assertThat(detailList.get(6).getMessage()).isEqualTo(
        "abbreviationEn en already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");

    assertThat(detailList.get(7).getMessage()).isEqualTo(
        "abbreviationFr fr already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");

    assertThat(detailList.get(8).getMessage()).isEqualTo(
        "abbreviationIt it already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");

    assertThat(detailList.get(9).getMessage()).isEqualTo(
        "organisationNumber 123 already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");
  }

  @Test
  void shouldConvertToErrorResponseCorrectlySorted() {
    // Given
    BusinessOrganisationConflictException conflictException = new BusinessOrganisationConflictException(
        version, List.of(version2, version));
    // When
    ErrorResponse errorResponse = conflictException.getErrorResponse();
    List<Detail> detailList = errorResponse.getDetails().stream().toList();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(detailList).hasSize(10);

    assertThat(detailList.get(0).getMessage()).isEqualTo(
        "abbreviationDe de already taken from 01.01.1980 to 01.01.2020 by ch:1:sboid:1000000");
  }
}
