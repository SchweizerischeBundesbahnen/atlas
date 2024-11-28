package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.ElementType;
import ch.sbb.atlas.api.lidi.enumaration.LidiElementType;
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
public class LineRequestParams extends VersionedObjectDateRequestParams {

  @Parameter(description = "Filter for a swiss line number.")
  private String swissLineNumber;

  @Parameter(description = "Search for a string in Line attributes.")
  @Singular(ignoreNullCollections = true, value = "searchCriteria")
  private List<String> searchCriteria = new ArrayList<>();

  @Parameter(description = "Filter on the Status of a Line.")
  @Singular(ignoreNullCollections = true)
  private List<Status> statusRestrictions = new ArrayList<>();

  @Parameter(description = "Filter on the ElementType.")
  @Singular(ignoreNullCollections = true)
  private List<LidiElementType> typeRestrictions = new ArrayList<>();

  @Parameter(description = "Filter on the Element.")
  @Singular(ignoreNullCollections = true)
  private List<ElementType> elementRestrictions = new ArrayList<>();

  @Parameter(description = "Filter for a business organisation.")
  private String businessOrganisation;

}
