package ch.sbb.atlas.versioning.version;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningWhenValidToAndOrValidFromAreEdited implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {
    log.info("Apply versioning when validFrom and/or validTo are edited.");

    Versioning versioning;
    if (vd.isNoObjectToVersioningFound()) {
      versioning = new VersioningWhenNoEntityFound();
    } else if (vd.isJustOneObjectToVersioningFound()) {
      versioning = new VersioningOnSingleFoundEntity();
    } else {
      versioning = new VersioningOverMultipleFoundEntities();
    }

    List<VersionedObject> versionedObjects = versioning.applyVersioning(vd);
    vd.getVersionedObjects().addAll(versionedObjects);
    return vd.getVersionedObjects();
  }

}
