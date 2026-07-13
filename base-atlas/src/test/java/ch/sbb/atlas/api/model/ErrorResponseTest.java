package ch.sbb.atlas.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void shouldFormatTemplatedMessageWithParameters() {
    Detail detail = Detail.builder()
        .field("swissLineNumber")
        .message("Value {0} rejected due to {1}")
        .displayInfo(DisplayInfo.builder()
            .code("ERROR.CONSTRAINT")
            .with("rejectedValue", "abc")
            .with("cause", "invalid")
            .build())
        .build();

    assertThat(detail.getMessage()).isEqualTo("Value abc rejected due to invalid");
  }

  @Test
  void shouldReturnLiteralMessageContainingRegexQuantifierBraces() {
    // Jakarta @Pattern violation messages are literal strings, not MessageFormat templates.
    // "{1,3}" would otherwise be interpreted by MessageFormat as an invalid format element and throw.
    Detail detail = Detail.builder()
        .field("number")
        .message("must match \"\\d{1,3}\"")
        .displayInfo(DisplayInfo.builder()
            .code("ERROR.CONSTRAINT_VIOLATION.PATTERN")
            .with("propertyPath", "number")
            .build())
        .build();

    assertThat(detail.getMessage()).isEqualTo("must match \"\\d{1,3}\"");
  }
}


