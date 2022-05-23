package ch.sbb.atlas.model.entity;

import java.lang.reflect.Field;
import java.util.Optional;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

public abstract class BusinessIdGenerator implements ValueGenerator<String> {

  private final String dbSequence;
  private final String businessIdPrefix;
  private final String dbField;

  private BusinessIdGenerator(String dbSequence, String businessIdPrefix, String dbField) {
    this.dbSequence = dbSequence;
    this.businessIdPrefix = businessIdPrefix;
    this.dbField = dbField;
  }

  @Override
  public String generateValue(Session session, Object entity) {
    Optional<String> presetSlnid = getPresetSlnid(entity);
    if (presetSlnid.isPresent()) {
      return presetSlnid.get();
    }

    long result = Long.parseLong(
        session.createNativeQuery("SELECT nextval('" + dbSequence + "') as nextval")
               .setFlushMode(FlushMode.COMMIT)
               .getSingleResult().toString());
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

  public static class SlnidGenerator extends BusinessIdGenerator {

    public SlnidGenerator() {
      super("slnid_seq", "ch:1:slnid:", "slnid");
    }
  }

  public static class TtfnidGenerator extends BusinessIdGenerator {

    public TtfnidGenerator() {
      super("ttfnid_seq", "ch:1:ttfnid:", "ttfnid");
    }
  }

  public static class SboidGenerator extends BusinessIdGenerator {

    public SboidGenerator() {
      super("sboid_seq", "ch:1:sboid:", "sboid");
    }
  }

}