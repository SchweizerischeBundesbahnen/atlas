package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PrmBusinessOrganisationBasedUserAdministrationService extends BaseUserAdministrationService {

    public PrmBusinessOrganisationBasedUserAdministrationService(UserPermissionHolder userPermissionHolder) {
        super(userPermissionHolder);
    }

}
