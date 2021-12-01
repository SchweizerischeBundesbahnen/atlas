package ch.sbb.timetable.field.number.entity;

import java.lang.reflect.Field;
import java.util.Optional;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

public class TtfnidGenerator implements ValueGenerator<String> {

  private static final String PREFIX = "ch:1:ttfnid:";
  private static final String SEQUENCE = "ttfnid_seq";

  @Override
  public String generateValue(Session session, Object entity) {
    if (getPresetTtfnid(entity).isPresent()) {
      return getPresetTtfnid(entity).get();
    }
    long result = Long.parseLong(
        session.createNativeQuery("SELECT nextval('" + SEQUENCE + "') as nextval")
            .setFlushMode(FlushMode.COMMIT)
            .getSingleResult().toString());
    return PREFIX + result;
  }

  private static Optional<String> getPresetTtfnid(Object entity) {
    try {
      Field ttfnid = entity.getClass().getDeclaredField("ttfnid");
      ttfnid.trySetAccessible();
      String ttfnidValue = (String) ttfnid.get(entity);
      return Optional.ofNullable(ttfnidValue);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }
  }
}
