package ch.sbb.atlas.api.lidi;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AffectedSublines {

  List<String> allowedSublines = new ArrayList<>();
  List<String> notAllowedSublines = new ArrayList<>();

}
