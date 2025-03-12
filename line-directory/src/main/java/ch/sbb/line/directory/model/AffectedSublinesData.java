package ch.sbb.line.directory.model;

import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
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