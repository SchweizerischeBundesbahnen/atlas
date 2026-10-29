package ch.sbb.line.directory.module.line.validation;

import ch.sbb.atlas.annotation.SupervisorOnly;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3SupervisorOnlyFieldException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

/**
 * Fields of {@link LineVersionV3} annotated with {@link SupervisorOnly} are reserved for users with at least the SUPERVISOR
 * role. On create the fields must not be populated at all, on update they must not be changed; sending the unchanged current
 * value stays allowed.
 */
@Service
@RequiredArgsConstructor
public class LineV3SupervisorFieldValidator {

  private static final List<String> SUPERVISOR_ONLY_FIELDS =
      FieldUtils.getFieldsListWithAnnotation(LineVersionV3.class, SupervisorOnly.class).stream()
          .map(Field::getName)
          .toList();

  private final BusinessOrganisationBasedUserAdministrationService userAdministrationService;

  public void validateCreate(LineVersionV3 newVersion) {
    BeanWrapper newValues = new BeanWrapperImpl(newVersion);
    validate(field -> newValues.getPropertyValue(field) != null);
  }

  public void validateUpdate(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    BeanWrapper currentValues = new BeanWrapperImpl(currentVersion);
    BeanWrapper editedValues = new BeanWrapperImpl(editedVersion);
    validate(field -> !Objects.equals(currentValues.getPropertyValue(field), editedValues.getPropertyValue(field)));
  }

  private void validate(Predicate<String> affected) {
    Set<String> affectedFields = SUPERVISOR_ONLY_FIELDS.stream()
        .filter(affected)
        .collect(Collectors.toCollection(TreeSet::new));
    if (affectedFields.isEmpty() || userAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)) {
      return;
    }
    throw new LineV3SupervisorOnlyFieldException(affectedFields);
  }

}
