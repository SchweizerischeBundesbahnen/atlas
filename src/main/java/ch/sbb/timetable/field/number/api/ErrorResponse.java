package ch.sbb.timetable.field.number.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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
  @NotNull
  @Builder.Default
  private List<Detail> details = new ArrayList<>();

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  @Schema(name = "ErrorDetail")
  public static class Detail {

    @Schema(description = "Errormessage in english for API purposes", example = "Resource not found")
    @NotNull
    private String message;

    @Schema(description = "Field on which to display the error", example = "validFrom")
    @NotNull
    private String field;

    @Schema(description = "DisplayInfo for messages")
    @NotNull
    private DisplayInfo displayInfo;

    public String getMessage() {
      return MessageFormat.format(message, displayInfo.getParameters().stream().map(Parameter::getValue).toArray());
    }
  }

  @AllArgsConstructor
  @Getter
  public static class DisplayInfo {

    @Schema(description = "Errorcode for UI", example = "LIDI.LINE.CONFLICT")
    @NotNull
    private final String code;

    @Schema(description = "Parameters for messages")
    @NotNull
    private final List<Parameter> parameters;

    public static DisplayInfoBuilder builder() {
      return new DisplayInfoBuilder();
    }

    public static class DisplayInfoBuilder {

      private String code;
      private final List<Parameter> parameters = new ArrayList<>();

      public DisplayInfoBuilder code(String code) {
        this.code = code;
        return this;
      }

      public DisplayInfoBuilder with(String key, String value) {
        this.parameters.add(new Parameter(key, value));
        return this;
      }

      public DisplayInfoBuilder with(String key, LocalDate value) {
        this.parameters.add(new Parameter(key, value.format(DateTimeFormatter.ISO_DATE)));
        return this;
      }

      public DisplayInfo build() {
        return new DisplayInfo(Objects.requireNonNull(code), parameters);
      }
    }
  }

  @RequiredArgsConstructor
  @Getter
  public static class Parameter {

    private final String key;
    private final String value;
  }

}
