package ch.sbb.line.directory.module.line.security;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.atlas.user.administration.security.service.UpdateAffectedVersionLocator;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

/**
 * Evaluates which fields of a {@link LineVersionV3} the current user is allowed to write.
 * <p>
 * Fields annotated with {@link FieldPermission} require the permission declared by the annotation, every other field requires
 * the regular LIDI write permission on the business organisation of the line. This results in the following matrix:
 * <ul>
 *   <li>supervisor: all fields</li>
 *   <li>user with LIDI write permission: all fields except the annotated ones</li>
 *   <li>user holding only a dedicated field permission: only the fields annotated with that permission</li>
 * </ul>
 * The third case additionally requires {@link #mayWriteRestrictedFieldsOnly(LineVersionV3, List)} as an alternative entry
 * condition on the update method of the service, because such a user has no write permission on the business organisation.
 * Granting a dedicated field permission to non-supervisors is not implemented yet; it only requires extending
 * {@link #mayWrite(LineV3FieldPermission)}, analogous to
 * {@link ch.sbb.atlas.user.administration.security.service.ServicePointTerminationBasedUserAdministrationService}.
 */
@Service
@RequiredArgsConstructor
public class LineV3FieldPermissionService {

  private static final Set<String> TECHNICAL_FIELDS = Set.of(Fields.id);

  private static final Map<String, LineV3FieldPermission> RESTRICTED_FIELDS = resolveRestrictedFields();
  private static final List<String> COMPARED_FIELDS = resolveComparedFields();

  private final BusinessOrganisationBasedUserAdministrationService userAdministrationService;

  public boolean mayWrite(LineV3FieldPermission permission) {
    return switch (permission) {
      case CABLEWAY, FINANCING -> userAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI);
    };
  }

  public Set<String> forbiddenFieldsOnCreate(LineVersionV3 newVersion) {
    BeanWrapper newValues = new BeanWrapperImpl(newVersion);
    return forbiddenFields(field -> newValues.getPropertyValue(field) != null);
  }

  public Set<String> forbiddenFieldsOnUpdate(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    BeanWrapper currentValues = new BeanWrapperImpl(currentVersion);
    BeanWrapper editedValues = new BeanWrapperImpl(editedVersion);
    return forbiddenFields(field -> !Objects.equals(currentValues.getPropertyValue(field), editedValues.getPropertyValue(field)));
  }

  /**
   * Alternative entry condition for an update: it grants access to users without write permission on the business organisation
   * as long as they change nothing but fields covered by a {@link FieldPermission} they hold. The detailed check of the
   * individual fields stays with the validator, so that a rejected request still reports the offending fields.
   */
  public boolean mayWriteRestrictedFieldsOnly(LineVersionV3 editedVersion, List<LineVersionV3> currentVersions) {
    List<LineVersionV3> affectedVersions = UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(editedVersion,
        currentVersions);
    if (affectedVersions.isEmpty()) {
      return false;
    }
    Set<String> changedFields = affectedVersions.stream()
        .map(currentVersion -> changedFields(currentVersion, editedVersion))
        .flatMap(Set::stream)
        .collect(Collectors.toCollection(TreeSet::new));
    if (changedFields.isEmpty() || !RESTRICTED_FIELDS.keySet().containsAll(changedFields)) {
      return false;
    }
    return changedFields.stream()
        .map(RESTRICTED_FIELDS::get)
        .distinct()
        .allMatch(this::mayWrite);
  }

  private Set<String> forbiddenFields(Predicate<String> affected) {
    return RESTRICTED_FIELDS.entrySet().stream()
        .filter(entry -> affected.test(entry.getKey()))
        .filter(entry -> !mayWrite(entry.getValue()))
        .map(Map.Entry::getKey)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  private static Set<String> changedFields(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    BeanWrapper currentValues = new BeanWrapperImpl(currentVersion);
    BeanWrapper editedValues = new BeanWrapperImpl(editedVersion);
    return COMPARED_FIELDS.stream()
        .filter(field -> !Objects.equals(currentValues.getPropertyValue(field), editedValues.getPropertyValue(field)))
        .collect(Collectors.toCollection(TreeSet::new));
  }

  private static Map<String, LineV3FieldPermission> resolveRestrictedFields() {
    Map<String, LineV3FieldPermission> restrictedFields = new LinkedHashMap<>();
    businessFields().forEach(field -> {
      FieldPermission fieldPermission = field.getAnnotation(FieldPermission.class);
      if (fieldPermission != null) {
        restrictedFields.put(field.getName(), fieldPermission.value());
      }
    });
    return Map.copyOf(restrictedFields);
  }

  private static List<String> resolveComparedFields() {
    return businessFields().map(Field::getName).toList();
  }

  /**
   * All fields a client may write. Auditing fields are inherited from the base entity and therefore not declared here, the
   * technical primary key is excluded explicitly. Any newly added field is compared by default, so that a missing
   * {@link FieldPermission} never silently widens the permissions of a restricted user.
   */
  private static Stream<Field> businessFields() {
    return Arrays.stream(LineVersionV3.class.getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .filter(field -> !TECHNICAL_FIELDS.contains(field.getName()));
  }

}
