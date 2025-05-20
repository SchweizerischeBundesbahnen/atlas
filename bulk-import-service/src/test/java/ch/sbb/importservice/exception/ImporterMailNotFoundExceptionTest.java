package ch.sbb.importservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.UserModel;
import org.junit.jupiter.api.Test;

class ImporterMailNotFoundExceptionTest {

  @Test
  void shouldHaveDisplayCode() {
    ImporterMailNotFoundException exception = new ImporterMailNotFoundException(UserModel.builder().sbbUserId("e345234").build());
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.ERROR.IMPORTER_MAIL_NOT_FOUND");
  }
}