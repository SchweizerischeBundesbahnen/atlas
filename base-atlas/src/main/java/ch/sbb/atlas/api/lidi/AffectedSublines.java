package ch.sbb.atlas.api.lidi;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AffectedSublines {

  List<String> allowedSublines = new ArrayList<>();
  List<String> notAllowedSublines = new ArrayList<>();

}
