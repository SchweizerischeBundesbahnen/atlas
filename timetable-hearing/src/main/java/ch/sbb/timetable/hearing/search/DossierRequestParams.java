package ch.sbb.timetable.hearing.search;

import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DossierRequestParams {

  @Parameter(description = "Filter on the timetable hearing year of a dossier.")
  private Long timetableHearingYear;

  private SwissCanton canton;

  private String boContactSbbuid;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias = new ArrayList<>();

  @Parameter(description = "Filter on the Status of a dossier.")
  @Singular(ignoreNullCollections = true)
  private List<DossierStatus> statusRestrictions = new ArrayList<>();

}
