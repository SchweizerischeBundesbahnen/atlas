package ch.sbb.line.directory.model;

import ch.sbb.line.directory.entity.SublineVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SublineVersionRange {

  private SublineVersion oldestVersion;
  private SublineVersion latestVersion;
}
