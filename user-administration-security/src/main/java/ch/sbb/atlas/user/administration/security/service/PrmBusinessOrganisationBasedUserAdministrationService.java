package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.api.model.SboidsAssociated;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrmBusinessOrganisationBasedUserAdministrationService extends BaseUserAdministrationService {

    private static final Predicate<UserAdministrationPermissionRestrictionModel> IS_BUSINESS_ORGANISATION_RESTRICTION =
            i -> i.getRestrictionType() == PermissionRestrictionType.BUSINESS_ORGANISATION;

    public PrmBusinessOrganisationBasedUserAdministrationService(UserPermissionHolder userPermissionHolder) {
        super(userPermissionHolder);
    }

    public boolean hasUserPermissionsForBusinessOrganisations(SboidsAssociated businessObjects,
                                                              ApplicationType applicationType) {
        if (businessObjects == null || businessObjects.getSboids().isEmpty()) {
            log.error("List of ServicePointVersions was empty. Cannot perform check permissions. Will deny operation");
            return false;
        }
        log.info("Checking if user may create or edit any of given list");
        return businessObjects.getSboids().stream().anyMatch(sboid -> hasUserPermissionsForBusinessOrganisation(sboid, applicationType));
    }


    private boolean hasUserPermissionsForBusinessOrganisation(String sboid,
                                                             ApplicationType applicationType) {
        log.info("Checking if user {} may create object with sboid {}",
                getCurrentUserSbbUid(),
                sboid);

        boolean permissionsToCreate = hasUserPermissions(applicationType,
                permissions -> permissions.getRestrictions().stream()
                        .filter(IS_BUSINESS_ORGANISATION_RESTRICTION)
                        .map(UserAdministrationPermissionRestrictionModel::getValue).collect(Collectors.toSet())
                        .contains(sboid));

        log.info("User {} has permissions: {}", getCurrentUserSbbUid(), permissionsToCreate);
        return permissionsToCreate;
    }

}
