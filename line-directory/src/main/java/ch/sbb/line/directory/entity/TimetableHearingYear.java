package ch.sbb.line.directory.entity;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants;
import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
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
@Entity(name = "timetable_hearing_year")
@FieldNameConstants
public class TimetableHearingYear extends BaseEntity {

  @Id
  @Min(TimetableHearingConstants.MIN_YEAR)
  @Max(TimetableHearingConstants.MAX_YEAR)
  private Long timetableYear;

  @NotNull
  @Enumerated(EnumType.STRING)
  private HearingStatus hearingStatus;

  @Column(columnDefinition = "DATE")
  @NotNull
  private LocalDate hearingFrom;

  @Column(columnDefinition = "DATE")
  @NotNull
  private LocalDate hearingTo;

  private boolean statementCreatableExternal;

  private boolean statementCreatableInternal;

  private boolean statementEditable;

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
