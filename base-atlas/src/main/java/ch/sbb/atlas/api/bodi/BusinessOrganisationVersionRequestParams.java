package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.model.VersionedObjectDateRequestParams;
import ch.sbb.atlas.model.Status;
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
@Builder
@ToString
public class BusinessOrganisationVersionRequestParams extends VersionedObjectDateRequestParams {

  @Parameter(description = "Search criteria strings will be looked up in specific columns")
  @Singular(value = "searchCriteria", ignoreNullCollections = true)
  private List<String> searchCriteria = new ArrayList<>();

  @Parameter(description = "Sboid based restriction")
  @Singular(ignoreNullCollections = true)
  private List<String> inSboids = new ArrayList<>();

  @Parameter(description = "Status based restriction", example = "85")
  @Singular(ignoreNullCollections = true)
  private List<Status> statusChoices = new ArrayList<>();

}
