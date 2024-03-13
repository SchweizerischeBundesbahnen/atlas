package ch.sbb.prm.directory.controller.model;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
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
public class ReferencePointRequestParams extends PrmObjectRequestParams{

  @Parameter(description = "ReferencePointAttributeType")
  @Singular(ignoreNullCollections = true)
  private List<ReferencePointAttributeType> referencePointAttributeTypes = new ArrayList<>();

}
