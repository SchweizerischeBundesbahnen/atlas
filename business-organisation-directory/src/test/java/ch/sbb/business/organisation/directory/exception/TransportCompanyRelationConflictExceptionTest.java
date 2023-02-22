package ch.sbb.business.organisation.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TransportCompanyRelationConflictExceptionTest {

  private static final TransportCompanyRelation RELATION = TransportCompanyRelation.builder()
      .transportCompany(TransportCompany.builder().id(5L).number("#0005").build())
      .sboid("ch:1:sboid:100500").validFrom(LocalDate.of(2020, 1, 1))
      .validTo(LocalDate.of(2021, 1, 1))
      .build();

  private static final TransportCompanyRelation RELATION_2 = TransportCompanyRelation.builder()
      .transportCompany(TransportCompany.builder().id(5L).number("#0005").build())
      .sboid("ch:1:sboid:100500")
      .validFrom(LocalDate.of(2019, 1, 1))
      .validTo(LocalDate.of(2021, 1, 1))
      .build();

  @Test
  void shouldConvertToErrorMessageCorrectly() {
    // Given
    TransportCompanyRelationConflictException conflictException = new TransportCompanyRelationConflictException(
        RELATION, List.of(RELATION, RELATION_2));
    // When
    ErrorResponse errorResponse = conflictException.getErrorResponse();

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(errorResponse.getDetails()).hasSize(2);

    assertThat(errorResponse.getDetails().first().getMessage()).isEqualTo(
        "TransportCompany #0005 already relates to ch:1:sboid:100500 from 01.01.2019 to 01.01.2021");

    assertThat(errorResponse.getDetails().stream().toList().get(1).getMessage()).isEqualTo(
        "TransportCompany #0005 already relates to ch:1:sboid:100500 from 01.01.2020 to 01.01.2021");
  }
}
