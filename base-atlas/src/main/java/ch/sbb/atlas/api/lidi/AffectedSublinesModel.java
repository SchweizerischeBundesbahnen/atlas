package ch.sbb.atlas.api.lidi;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AffectedSublinesModel {

  private List<String> allowedSublines = new ArrayList<>();
  private List<String> notAllowedSublines = new ArrayList<>();

}
