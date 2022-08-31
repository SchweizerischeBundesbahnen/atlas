package ch.sbb.atlas.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
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
  private SortedSet<Detail> details = new TreeSet<>();

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @SuperBuilder
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
    public int compareTo(@NonNull Detail detailToCompare) {
      return 1;
    }
  }

  @SuperBuilder
  public static class ValidFromDetail extends Detail {

    @Override
    public int compareTo(@NonNull Detail detailToCompare) {
      if (detailToCompare instanceof ValidFromDetail validFromDetailToCompare) {
        return Comparator.comparing(ValidFromDetail::getValidFrom)
                         .thenComparing(ValidFromDetail::getMessage)
                         .compare(this, validFromDetailToCompare);
      }
      throw new IllegalArgumentException("Can only compare ValidFromDetail type");
    }

    private LocalDate getValidFrom() {
      Optional<Parameter> parameter = getDisplayInfo().getParameters().stream()
                                                      .filter(param -> VALID_FROM_KEY.equals(
                                                          param.getKey()))
                                                      .findFirst();
      String validFrom = parameter.orElseThrow(
          () -> new RuntimeException("Not found validFrom parameter in DisplayInfo")).getValue();

      return LocalDate.parse(validFrom, DATE_FORMATTER);
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

}
