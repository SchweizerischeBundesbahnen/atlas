package ch.sbb.atlas.base.service.versioning.version;

import static ch.sbb.atlas.base.service.versioning.model.VersionedObject.buildVersionedObjectToUpdate;
import static ch.sbb.atlas.base.service.versioning.version.VersioningHelper.findObjectToVersioning;

import ch.sbb.atlas.base.service.versioning.model.Entity;
import ch.sbb.atlas.base.service.versioning.model.ToVersioning;
import ch.sbb.atlas.base.service.versioning.model.VersionedObject;
import ch.sbb.atlas.base.service.versioning.model.VersioningData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningWhenValidFromAndValidToAreNotEdited implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning when validFrom and validTo are not edited.");
    ToVersioning toVersioning = findObjectToVersioning(vd.getCurrentVersion(),
        vd.getObjectsToVersioning());
    Entity entity = Entity.replaceEditedPropertiesWithCurrentProperties(vd.getEditedEntity(),
        toVersioning.getEntity());

    vd.getVersionedObjects().add(buildVersionedObjectToUpdate(vd.getCurrentVersion().getValidFrom(),
        vd.getCurrentVersion().getValidTo(), entity));
    return vd.getVersionedObjects();
  }
}
