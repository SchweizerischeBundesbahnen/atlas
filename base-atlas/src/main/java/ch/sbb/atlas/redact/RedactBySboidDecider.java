package ch.sbb.atlas.redact;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;

public interface RedactBySboidDecider {

  boolean hasUserPermissionsForBusinessOrganisation(String sboid, ApplicationType applicationType);

}
