package ch.sbb.atlas.api.timetable.hearing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Schema(name = "TimetableHearingStatementAlternating")
public class TimetableHearingStatementAlternatingModel {

  @NotNull
  private TimetableHearingStatementModelV2 timetableHearingStatement;

  @NotNull
  private Pageable pageable;

}
