package ch.sbb.line.directory.module.line.validation;

import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3FieldPermissionException;
import ch.sbb.line.directory.module.line.security.LineV3FieldPermissionService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Rejects writes on fields the current user is not permitted to write. On create such a field must not be populated at all, on
 * update it must not be changed; sending the unchanged current value stays allowed.
 */
@Service
@RequiredArgsConstructor
public class LineV3FieldPermissionValidator {

  private final LineV3FieldPermissionService fieldPermissionService;

  public void validateCreate(LineVersionV3 newVersion) {
    reject(fieldPermissionService.forbiddenFieldsOnCreate(newVersion));
  }

  public void validateUpdate(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    reject(fieldPermissionService.forbiddenFieldsOnUpdate(currentVersion, editedVersion));
  }

  private static void reject(Set<String> forbiddenFields) {
    if (!forbiddenFields.isEmpty()) {
      throw new LineV3FieldPermissionException(forbiddenFields);
    }
  }

}
