package ch.sbb.atlas.user.administration.module.useradministration.repository;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserPermissionRepository {

  Page<String> getFilteredUsers(Pageable pageable, Set<ApplicationType> applicationTypes, Set<String> permissionRestrictions,
      PermissionRestrictionType type);

}
