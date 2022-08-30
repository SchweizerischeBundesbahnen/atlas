package ch.sbb.atlas.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ErrorResponse")
public class ErrorResponse {

  public static final int VERSIONING_NO_CHANGES_HTTP_STATUS = 520;
  private static final String VALID_FROM_KEY = "validFrom";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
      AtlasApiConstants.DATE_FORMAT_PATTERN_CH);

  @Schema(description = "HTTP Status Code", example = "400")
  private int status;

  @Schema(description = "Summary of error", example = "Validation error")
  private String message;

  @Schema(description = "Error", example = "Method Not Allowed", nullable = true)
  private String error;

  @Schema(description = "List of error details", nullable = true)
  private List<Detail> details = new ArrayList<>();

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  @Schema(name = "ErrorDetail")
  public static class Detail implements Comparable<Detail> {

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
      return MessageFormat.format(message,
          displayInfo.getParameters().stream().map(Parameter::getValue).toArray());
    }

    @Override
    public int compareTo(@NonNull Detail next) {
      LocalDate currentValidFrom = parseStringToLocalDate(
          displayInfo.getValueFromParameter(VALID_FROM_KEY));
      LocalDate nextValidFrom = parseStringToLocalDate(
          next.getDisplayInfo().getValueFromParameter(VALID_FROM_KEY));
      return currentValidFrom.compareTo(nextValidFrom);
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

    public String getValueFromParameter(@NonNull String key) {
      Optional<Parameter> parameter = parameters.stream()
                                                .filter(param -> key.equals(param.getKey()))
                                                .findFirst();
      return parameter.orElseThrow(
          () -> new RuntimeException("Requested Parameter not found in DisplayInfo")).getValue();
    }

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
        this.parameters.add(
            new Parameter(key, value.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
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

  public List<Detail> sortDetailsByValidFrom() {
    return details.stream().sorted().toList();
  }

  private static LocalDate parseStringToLocalDate(String date) {
    return LocalDate.parse(date, DATE_FORMATTER);
  }

}
