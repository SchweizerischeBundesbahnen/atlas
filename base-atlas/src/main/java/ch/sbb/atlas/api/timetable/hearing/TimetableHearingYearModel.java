package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Schema(name = "TimetableHearingYear")
public class TimetableHearingYearModel extends BaseVersionModel {

  @Min(2010)
  @Max(2099)
  @NotNull
  @Schema(description = "TimetableYear", example = "2024")
  private Long timetableYear;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private HearingStatus hearingStatus;

  @NotNull
  @Schema(description = "Hearing held from", example = "2023-05-1")
  private LocalDate hearingFrom;

  @NotNull
  @Schema(description = "Hearing held to", example = "2023-05-31")
  private LocalDate hearingTo;

  @Schema(description = "New statements accepted from SKI")
  private boolean statementCreatableExternal;

  @Schema(description = "New statements accepted from Atlas")
  private boolean statementCreatableInternal;

  @Schema(description = "Updates on statements accepted")
  private boolean statementEditable;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "hearingTo must not be before hearingFrom")
  boolean isHearingToEqualOrGreaterThenHearingFrom() {
    if (getHearingTo() == null || getHearingFrom() == null) {
      return false;
    }
    return !getHearingTo().isBefore(getHearingFrom());
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "HearingTo must be the year before")
  boolean isHearingToValid() {
    if (getHearingTo() == null) {
      return false;
    }
    return getHearingTo().getYear() == getTimetableYear() - 1;
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "HearingFrom must be the year before")
  boolean isHearingFromValid() {
    if (getHearingFrom() == null) {
      return false;
    }
    return getHearingFrom().getYear() == getTimetableYear() - 1;
  }
}
