package ch.sbb.atlas.user.administration.security;

import java.util.List;

public interface UserAdministrationAwareService<T extends BusinessOrganisationAssociated> {

  T create(T businessObject);
  T update(T editedBusinessObject, List<T> currentBusinessObjects);
}
