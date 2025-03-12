package ch.sbb.line.directory.model;

import ch.sbb.line.directory.entity.LineVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LineVersionRange {

  private LineVersion oldestVersion;
  private LineVersion latestVersion;
}
