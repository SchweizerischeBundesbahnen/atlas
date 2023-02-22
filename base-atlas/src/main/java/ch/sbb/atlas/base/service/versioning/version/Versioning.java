package ch.sbb.atlas.base.service.versioning.version;

import ch.sbb.atlas.base.service.versioning.model.VersionedObject;
import ch.sbb.atlas.base.service.versioning.model.VersioningData;
import java.util.List;

public interface Versioning {

  List<VersionedObject> applyVersioning(VersioningData vd);
}
