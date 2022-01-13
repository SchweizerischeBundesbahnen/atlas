package ch.sbb.timetable.field.number.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.text.MessageFormat;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ErrorResponse")
public class ErrorResponse {

  @Schema(description = "HTTP Status Code", example = "400")
  private int httpStatus;

  @Schema(description = "Summary of error", example = "Validation error")
  private String message;

  @Schema(description = "List of error details")
  private List<Detail> details;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  @Schema(name = "ErrorDetail")
  public static class Detail {

    @Schema(description = "Field on which to display the error", example = "validFrom")
    @NotNull
    private String field;

    @Schema(description = "Errorcode for UI", example = "LIDI.LINE.CONFLICT")
    @NotNull
    private String code;

    @Schema(description = "Errormessage in english for API purposes", example = "Resource not found")
    @NotNull
    private String message;

    @Schema(description = "Parameters for messages")
    @NotNull
    private List<String> parameters;

    public String getMessage() {
      return MessageFormat.format(message, parameters.toArray());
    }
  }

}
