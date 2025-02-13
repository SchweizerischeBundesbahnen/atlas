package ch.sbb.atlas.api.lidi;

import java.util.ArrayList;
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

  private List<String> allowedSublines = new ArrayList<>();
  private List<String> notAllowedSublines = new ArrayList<>();

  private boolean isZeroAffectedSublines;
  private boolean isAllowedToShort;
  private boolean isNotAllowedToShort;

}
