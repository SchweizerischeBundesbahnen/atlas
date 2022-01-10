package ch.sbb.timetable.field.number.api;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ErrorResponseModel {

  @Schema(description = "HTTP Status Code", example = "400")
  private int httpStatusCode;

  @Schema(description = "List of messages")
  private List<ErrorMessage> errorMessages;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  @Schema(name = "ErrorMessage")
  public static class ErrorMessage {

    @Schema(description = "Field on which to display the error", example = "validFrom")
    @NotNull
    private String field;

    @Schema(description = "Errorcode for UI", example = "LIDI.LINE.CONFLICT")
    @NotNull
    private String errorCode;

    @Schema(description = "Errormessage in english for API purposes", example = "Resource not found")
    @NotNull
    private String message;

    @Schema(description = "Parameters for messages")
    @NotNull
    private List<String> messageParameters; // Translatable parameters? language headers? backend translation?
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  @Schema(name = "MessageParameter")
  public static class MessageParameter {

    @Schema(description = "Errormessage in english for API purposes", example = "Resource not found")
    private boolean translatable;

    private String value;

  }

}
