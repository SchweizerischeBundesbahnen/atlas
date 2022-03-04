package ch.sbb.atlas.versioning.version;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningData;
import java.util.List;

public interface Versioning {

  List<VersionedObject> applyVersioning(VersioningData vd);
}
