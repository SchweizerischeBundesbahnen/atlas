package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.entity.SublineVersion.Fields;
import jakarta.persistence.FlushModeType;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Optional;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

public class SublineSlnidGenerator implements BeforeExecutionGenerator {

  @Override
  public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
    return generateValue(session, owner);
  }

  @Override
  public EnumSet<EventType> getEventTypes() {
    return EnumSet.of(EventType.INSERT);
  }

  public String generateValue(SharedSessionContractImplementor session, Object entity) {
    Optional<String> presetSlnid = getPresetSlnid(entity);
    if (presetSlnid.isPresent()) {
      return presetSlnid.get();
    }

    String mainlineSlnid = getMainlineSlnid(entity);
    Long sublinePartNumber = session
        .createNativeQuery("SELECT count(*)+1 from subline_version where mainline_slnid = ?", Long.class)
        .setFlushMode(FlushModeType.COMMIT)
        .setParameter(1, mainlineSlnid)
        .getSingleResult();
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
