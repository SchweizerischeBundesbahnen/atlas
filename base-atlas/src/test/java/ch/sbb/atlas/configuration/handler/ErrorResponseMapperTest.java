package ch.sbb.atlas.configuration.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

class ErrorResponseMapperTest {

  @Test
  void shouldMapAtlasExceptionToErrorResponse() {
    ErrorResponse errorResponse = ErrorResponseMapper.mapToErrorResponse(new SloidNotFoundException("ch:1:sloid:12333"));
    assertThat(errorResponse.getMessage()).isEqualTo("Entity not found");
  }

  @Test
  void shouldMapVersioningNoChangesExceptionToErrorResponse() {
    ErrorResponse errorResponse = ErrorResponseMapper.mapToErrorResponse(new VersioningNoChangesException());
    assertThat(errorResponse.getMessage()).isEqualTo("No entities were modified after versioning execution.");
  }

  @Test
  void shouldMapConstraintViolationExceptionToErrorResponse() {
    ErrorResponse errorResponse = ErrorResponseMapper.mapToErrorResponse(getExampleConstraintViolation());

    assertThat(errorResponse.getDetails()).size().isEqualTo(2);
  }

  private ConstraintViolationException getExampleConstraintViolation() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("BernZuLangBernZuLangBernZuLangBernZuLangBernZuLang")
        .businessOrganisation("ch:1:sboid:5846489645")
        .stopPointType(StopPointType.ORDERLY)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    return new ConstraintViolationException(validator.validate(servicePointVersionModel));
  }

  @Test
  void shouldMapAccessDeniedExceptionToErrorResponse() {
    ErrorResponse errorResponse = ErrorResponseMapper.mapToErrorResponse(new AccessDeniedException("no"));
    assertThat(errorResponse.getMessage()).isEqualTo("You are not allowed to perform this operation on the ATLAS platform.");
    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo("ERROR.NOTALLOWED");
  }

}