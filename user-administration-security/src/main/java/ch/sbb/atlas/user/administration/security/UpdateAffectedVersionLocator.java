package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import java.time.LocalDate;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UpdateAffectedVersionLocator {

  public static <T extends BusinessOrganisationAssociated> List<T> findUpdateAffectedCurrentVersions(
      T editedBusinessObject,
      List<T> currentBusinessObjects) {
    LocalDate validFrom = editedBusinessObject.getValidFrom();
    LocalDate validTo = editedBusinessObject.getValidTo();

    return currentBusinessObjects.stream()
        .filter(currentVersion ->
            !currentVersion.getValidTo().isBefore(validFrom) &&
                !currentVersion.getValidFrom().isAfter(validTo))
        .toList();
  }
}