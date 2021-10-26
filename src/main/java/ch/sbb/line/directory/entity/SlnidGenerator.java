package ch.sbb.line.directory.entity;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

public class SlnidGenerator implements ValueGenerator<String> {

  private static final String PREFIX = "ch:1:slnid:";
  private static final String SEQUENCE = "slnid_seq";

  @Override
  public String generateValue(Session session, Object o) {
    long result = Long.parseLong(
        session.createNativeQuery("SELECT nextval('" + SEQUENCE + "') as nextval")
               .setFlushMode(FlushMode.COMMIT)
               .getSingleResult().toString());
    return PREFIX + result;
  }
}