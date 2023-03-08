
package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.SwissCanton;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableHearingStatementRequestParams {

  private Long timetableHearingYear;

  private SwissCanton canton;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias = new ArrayList<>();

  @Singular(ignoreNullCollections = true)
  private List<StatementStatus> statusRestrictions = new ArrayList<>();

  private String ttfnid;
}
