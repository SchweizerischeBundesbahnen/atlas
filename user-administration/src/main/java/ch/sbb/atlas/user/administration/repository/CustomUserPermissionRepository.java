package ch.sbb.atlas.user.administration.repository;

import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserPermissionRepository {

  Page<String> getFilteredUsers(Pageable pageable, Set<ApplicationType> applicationTypes, Set<String> permissionRestrictions, PermissionRestrictionType type);

}
