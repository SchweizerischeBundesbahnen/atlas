package ch.sbb.atlas.model.entity;

import jakarta.persistence.FlushModeType;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Optional;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

public abstract class BusinessIdGenerator implements BeforeExecutionGenerator {

  private final String dbSequence;
  private final String businessIdPrefix;
  private final String dbField;

  protected BusinessIdGenerator(String dbSequence, String businessIdPrefix, String dbField) {
    this.dbSequence = dbSequence;
    this.businessIdPrefix = businessIdPrefix;
    this.dbField = dbField;
  }

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

    long result =
        session.createNativeQuery("SELECT nextval('" + dbSequence + "') as nextval", Long.class)
            .setFlushMode(FlushModeType.COMMIT)
            .getSingleResult();
    return businessIdPrefix + result;
  }

  private Optional<String> getPresetSlnid(Object entity) {
    try {
      Field businessIdField = entity.getClass().getDeclaredField(dbField);
      businessIdField.trySetAccessible();
      String slnidValue = (String) businessIdField.get(entity);
      return Optional.ofNullable(slnidValue);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }
  }

}
