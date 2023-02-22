package ch.sbb.atlas.versioning.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class VersionableProperty {

  private String fieldName;

  private boolean ignoreDiff;

  private boolean doNotOverride;

  private RelationType relationType;

  private List<String> relationsFields;

  public enum RelationType {
    ONE_TO_MANY,
    ONE_TO_ONE,
    NONE
  }
}
