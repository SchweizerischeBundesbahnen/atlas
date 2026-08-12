package ch.sbb.timetable.hearing.entity;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "tth_dossier_year")
@FieldNameConstants
public class TthDossierYear {

  @Id
  @Min(TimetableHearingConstants.MIN_YEAR)
  @Max(TimetableHearingConstants.MAX_YEAR)
  private Long timetableYear;

  @NotNull
  @Enumerated(EnumType.STRING)
  private HearingStatus hearingStatus;

}
