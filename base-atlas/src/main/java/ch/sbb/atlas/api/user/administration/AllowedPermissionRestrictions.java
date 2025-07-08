package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;

@UtilityClass
public class AllowedPermissionRestrictions {

  private static final Map<Pair<ApplicationType, ApplicationRole>, Set<PermissionRestrictionType>> ALLOWED_PERMISSION_RESTRICTIONS =
      new HashMap<>();

  static {
    // TTFN
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.TTFN, ApplicationRole.READER), Set.of());
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.TTFN, ApplicationRole.WRITER),
        Set.of(PermissionRestrictionType.BUSINESS_ORGANISATION, PermissionRestrictionType.BULK_IMPORT));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.TTFN, ApplicationRole.SUPER_USER),
        Set.of(PermissionRestrictionType.BULK_IMPORT));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.TTFN, ApplicationRole.SUPERVISOR), Set.of());

    // LIDI
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.LIDI, ApplicationRole.READER), Set.of());
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.LIDI, ApplicationRole.WRITER),
        Set.of(PermissionRestrictionType.BUSINESS_ORGANISATION, PermissionRestrictionType.BULK_IMPORT));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.LIDI, ApplicationRole.SUPER_USER),
        Set.of(PermissionRestrictionType.BULK_IMPORT));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.LIDI, ApplicationRole.SUPERVISOR), Set.of());

    // BODI
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.BODI, ApplicationRole.READER), Set.of());
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.BODI, ApplicationRole.SUPERVISOR), Set.of());

    // TIMETABLE_HEARING
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.TIMETABLE_HEARING, ApplicationRole.READER), Set.of());
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.TIMETABLE_HEARING, ApplicationRole.EXPLICIT_READER),
        Set.of());
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.TIMETABLE_HEARING, ApplicationRole.WRITER),
        Set.of(PermissionRestrictionType.CANTON));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.TIMETABLE_HEARING, ApplicationRole.SUPERVISOR), Set.of());

    // SEPODI
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.SEPODI, ApplicationRole.READER),
        Set.of(PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE, PermissionRestrictionType.NOVA_TERMINATION_VOTE));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.SEPODI, ApplicationRole.WRITER),
        Set.of(PermissionRestrictionType.COUNTRY, PermissionRestrictionType.BUSINESS_ORGANISATION,
            PermissionRestrictionType.BULK_IMPORT, PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE,
            PermissionRestrictionType.NOVA_TERMINATION_VOTE));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.SEPODI, ApplicationRole.SUPER_USER),
        Set.of(PermissionRestrictionType.COUNTRY, PermissionRestrictionType.BULK_IMPORT,
            PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE, PermissionRestrictionType.NOVA_TERMINATION_VOTE));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.SEPODI, ApplicationRole.SUPERVISOR),
        Set.of(PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE, PermissionRestrictionType.NOVA_TERMINATION_VOTE));

    // PRM
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.PRM, ApplicationRole.READER), Set.of());
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.PRM, ApplicationRole.WRITER),
        Set.of(PermissionRestrictionType.BUSINESS_ORGANISATION,
            PermissionRestrictionType.BULK_IMPORT));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.PRM, ApplicationRole.SUPER_USER),
        Set.of(PermissionRestrictionType.BULK_IMPORT));
    ALLOWED_PERMISSION_RESTRICTIONS.put(Pair.of(ApplicationType.PRM, ApplicationRole.SUPERVISOR), Set.of());
  }

  public static Set<PermissionRestrictionType> get(ApplicationType application, ApplicationRole role) {
    return ALLOWED_PERMISSION_RESTRICTIONS.getOrDefault(Pair.of(application, role), Set.of());
  }
}
