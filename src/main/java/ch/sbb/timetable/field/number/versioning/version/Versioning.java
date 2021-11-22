package ch.sbb.timetable.field.number.versioning.version;

import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningData;
import java.util.List;

public interface Versioning {

  List<VersionedObject> applyVersioning(VersioningData vd);
}
