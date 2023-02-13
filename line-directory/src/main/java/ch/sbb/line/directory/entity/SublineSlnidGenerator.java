package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.lang.reflect.Field;
import java.util.Optional;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

public class SublineSlnidGenerator implements ValueGenerator<String> {

  @Override
  public String generateValue(Session session, Object entity) {
    // For tests only
    Optional<String> presetSlnid = getPresetSlnid(entity);
    if (presetSlnid.isPresent()) {
      return presetSlnid.get();
    }

    String mainlineSlnid = getMainlineSlnid(entity);
    String sublinePartNumber = session
        .createNativeQuery("SELECT count(*)+1 from subline_version where mainline_slnid = ?")
        .setFlushMode(FlushMode.COMMIT)
        .setParameter(1, mainlineSlnid)
        .getSingleResult().toString();
    return mainlineSlnid + ":" + sublinePartNumber;
  }

  private Optional<String> getPresetSlnid(Object entity) {
    return getPresetField(entity, Fields.slnid);
  }

  private String getMainlineSlnid(Object entity) {
    return getPresetField(entity, Fields.mainlineSlnid).orElseThrow();
  }

  private Optional<String> getPresetField(Object entity, String field) {
    try {
      Field businessIdField = entity.getClass().getDeclaredField(field);
      businessIdField.trySetAccessible();
      String value = (String) businessIdField.get(entity);
      return Optional.ofNullable(value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }
  }
}
