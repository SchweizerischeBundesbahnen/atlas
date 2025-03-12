package ch.sbb.line.directory.model;

import ch.sbb.line.directory.entity.SublineVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SublineVersionRange {

  private SublineVersion oldestVersion;
  private SublineVersion latestVersion;
}
