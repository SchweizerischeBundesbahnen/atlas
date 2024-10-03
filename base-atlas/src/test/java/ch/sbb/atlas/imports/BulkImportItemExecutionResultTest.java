package ch.sbb.atlas.imports;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class BulkImportItemExecutionResultTest {

  @Test
  void shouldMapSuccessResultToErrorCorrectly() {
    BulkImportItemExecutionResult result = BulkImportItemExecutionResult.builder().lineNumber(1).build();
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getErrors()).isNotNull().isEmpty();
  }

  @Test
  void shouldMapErrorResultToErrorCorrectly() {
    DisplayInfo displayInfo = DisplayInfo.builder()
        .code("ERROR.COMMON")
        .build();
    Detail errorDetail = Detail.builder()
        .message("Error occurred").displayInfo(displayInfo)
        .build();
    BulkImportItemExecutionResult result = BulkImportItemExecutionResult.builder()
        .lineNumber(1)
        .errorResponse(ErrorResponse.builder().details(new TreeSet<>(Set.of(errorDetail))).build()).build();

    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getErrors()).isNotEmpty();
    assertThat(result.getErrors()).first().extracting(BulkImportError::getDisplayInfo).isEqualTo(displayInfo);
  }

  @Test
  void isInfoShouldBeTrue() {
    BulkImportItemExecutionResult result = BulkImportItemExecutionResult.builder()
        .errorResponse(ErrorResponse.builder().status(520).build())
        .build();
    assertThat(result.isInfo()).isTrue();
  }

  @Test
  void isInfoShouldBeFalse() {
    BulkImportItemExecutionResult result = BulkImportItemExecutionResult.builder()
        .errorResponse(ErrorResponse.builder().status(400).build())
        .build();
    assertThat(result.isInfo()).isFalse();
  }
}
