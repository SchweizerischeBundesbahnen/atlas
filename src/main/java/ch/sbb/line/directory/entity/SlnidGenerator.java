package ch.sbb.line.directory.entity;

import java.lang.reflect.Field;
import java.util.Optional;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

public class SlnidGenerator implements ValueGenerator<String> {

  private static final String PREFIX = "ch:1:slnid:";
  private static final String SEQUENCE = "slnid_seq";

  @Override
  public String generateValue(Session session, Object entity) {
    Optional<String> presetSlnid = getPresetSlnid(entity);
    if (presetSlnid.isPresent()) {
      return presetSlnid.get();
    }

    long result = Long.parseLong(
        session.createNativeQuery("SELECT nextval('" + SEQUENCE + "') as nextval")
               .setFlushMode(FlushMode.COMMIT)
               .getSingleResult().toString());
    return PREFIX + result;
  }

  private static Optional<String> getPresetSlnid(Object entity) {
    try {
      Field slnid = entity.getClass().getDeclaredField("slnid");
      slnid.trySetAccessible();
      String slnidValue = (String) slnid.get(entity);
      return Optional.ofNullable(slnidValue);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }
  }
}