package ch.sbb.line.directory.module.line.security;

import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class LineV3RestrictedFieldService {

  public boolean areRestrictedFieldsEmpty(LineVersionV3 newVersion) {
    return restrictedFieldsOf(newVersion).stream().allMatch(Objects::isNull);
  }

  public boolean areRestrictedFieldsUnchanged(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    return restrictedFieldsOf(currentVersion).equals(restrictedFieldsOf(editedVersion));
  }

  private static List<Object> restrictedFieldsOf(LineVersionV3 version) {
    return Arrays.asList(
        version.getFinancing(),
        version.getCablewayNumber(),
        version.getCablewayType(),
        version.getCablewayName());
  }

}
