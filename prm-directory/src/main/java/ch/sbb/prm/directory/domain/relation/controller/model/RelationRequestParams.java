package ch.sbb.prm.directory.domain.relation.controller.model;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.search.model.PrmObjectRequestParams;
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
public class RelationRequestParams extends PrmObjectRequestParams {

  @Parameter(description = "ReferencePointElementType")
  @Singular(ignoreNullCollections = true)
  private List<ReferencePointElementType> referencePointElementTypes = new ArrayList<>();

  @Parameter(description = "ReferencePoint Sloid")
  @Singular(ignoreNullCollections = true)
  private List<String> referencePointSloids = new ArrayList<>();
}
