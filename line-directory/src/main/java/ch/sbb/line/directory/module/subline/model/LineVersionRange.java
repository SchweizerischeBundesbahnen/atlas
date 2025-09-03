package ch.sbb.line.directory.module.subline.model;

import ch.sbb.line.directory.module.line.entity.LineVersion;
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
