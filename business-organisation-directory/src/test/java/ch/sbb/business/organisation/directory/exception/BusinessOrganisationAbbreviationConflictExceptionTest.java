package ch.sbb.business.organisation.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.api.ErrorResponse;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion.Fields;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class BusinessOrganisationAbbreviationConflictExceptionTest {

  private final BusinessOrganisationVersion version = BusinessOrganisationData.businessOrganisationVersion();

  @Test
  void shouldConvertToErrorMessageCorrectly() {
    // Given
    BusinessOrganisationAbbreviationConflictException conflictException = new BusinessOrganisationAbbreviationConflictException(
        version, List.of(version));
    // When
    ErrorResponse errorResponse = conflictException.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(errorResponse.getDetails()).hasSize(4);

    assertThat(errorResponse.getDetails().get(0).getMessage()).isEqualTo(
        "abbreviationDe de already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");
    assertThat(errorResponse.getDetails().get(0).getDisplayInfo().getParameters().get(0).getKey()).isEqualTo(
        BusinessOrganisationAbbreviationConflictException.FIELD);
    assertThat(errorResponse.getDetails().get(0).getDisplayInfo().getParameters().get(0).getValue()).isEqualTo(
        Fields.abbreviationDe);
    assertThat(errorResponse.getDetails().get(0).getDisplayInfo().getParameters().get(1).getKey()).isEqualTo(
        Fields.abbreviationDe);
    assertThat(errorResponse.getDetails().get(0).getDisplayInfo().getParameters().get(1).getValue()).isEqualTo("de");

    assertThat(errorResponse.getDetails().get(1).getMessage()).isEqualTo(
        "abbreviationFr fr already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");

    assertThat(errorResponse.getDetails().get(2).getMessage()).isEqualTo(
        "abbreviationIt it already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");

    assertThat(errorResponse.getDetails().get(3).getMessage()).isEqualTo(
        "abbreviationEn en already taken from 01.01.2000 to 31.12.2000 by ch:1:sboid:1000000");


  }
}