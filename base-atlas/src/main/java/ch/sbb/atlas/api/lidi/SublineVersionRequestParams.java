package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.api.model.VersionedObjectDateRequestParams;
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
public class SublineVersionRequestParams extends VersionedObjectDateRequestParams {

  @Parameter(description = "Filter for a swiss subline number.")
  private String swissSublineNumber;

  @Parameter(description = "Filter for a main line slnid.")
  private String mainlineSlnid;

  @Parameter(description = "Filter on the SublineType.")
  @Singular(ignoreNullCollections = true)
  private List<SublineType> typeRestrictions = new ArrayList<>();

  @Parameter(description = "Filter for a business organisation.")
  private String businessOrganisation;

}
