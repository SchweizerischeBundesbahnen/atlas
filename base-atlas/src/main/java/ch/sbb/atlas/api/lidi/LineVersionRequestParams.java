package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.model.VersionedObjectDateRequestParams;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class LineVersionRequestParams extends VersionedObjectDateRequestParams {

  @Parameter(description = "Filter for a swiss line number.")
  private String swissLineNumber;

  @Parameter(description = "Filter on the Status of a Line.")
  @Singular(ignoreNullCollections = true)
  private List<Status> statusRestrictions = new ArrayList<>();

  @Parameter(description = "Filter on the LineType.")
  @Singular(ignoreNullCollections = true)
  private List<LineType> typeRestrictions = new ArrayList<>();

  @Parameter(description = "Filter for a business organisation.")
  private String businessOrganisation;

}
