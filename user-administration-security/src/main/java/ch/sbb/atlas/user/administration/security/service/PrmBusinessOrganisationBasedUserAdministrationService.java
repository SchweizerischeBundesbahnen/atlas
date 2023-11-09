package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.api.model.SboidsAssociated;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PrmBusinessOrganisationBasedUserAdministrationService extends BusinessOrganisationBasedUserAdministrationService {

    public PrmBusinessOrganisationBasedUserAdministrationService(UserPermissionHolder userPermissionHolder) {
        super(userPermissionHolder);
    }

    public boolean hasUserPermissionsForBusinessOrganisations(SboidsAssociated businessObjects,
                                                              ApplicationType applicationType) {
        if (businessObjects == null || businessObjects.getSboids().isEmpty()) {
            log.error("List of provided SboidsAssociated (SBOIDs) was empty. Cannot perform check permissions. Will deny operation");
            return false;
        }
        log.info("Checking if user has enough rights to perform edit or create of PRM object");
        return businessObjects.getSboids().stream()
                .anyMatch(sboid -> hasUserPermissionsForBusinessOrganisation(sboid, applicationType));
    }

}
