package ch.sbb.atlas.api.lidi;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AffectedSublinesModel {

  private List<String> allowedSublines;
  private List<String> notAllowedSublines;

  private boolean isAffectedSublinesEmpty;
  private boolean hasAllowedSublinesOnly;
  private boolean hasNotAllowedSublinesOnly;

}
