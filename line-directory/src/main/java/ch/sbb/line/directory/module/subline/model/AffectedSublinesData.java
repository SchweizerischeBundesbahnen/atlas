package ch.sbb.line.directory.module.subline.model;

import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.subline.entity.SublineVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AffectedSublinesData {

  private LineVersion lineVersion;
  private LineVersion editedVersion;
  private LineVersionRange lineVersionRange;
  private Map<String, List<SublineVersion>> sublineVersions;
  private List<String> allowedSublines = new ArrayList<>();
  private List<String> notAllowedSublines = new ArrayList<>();

}