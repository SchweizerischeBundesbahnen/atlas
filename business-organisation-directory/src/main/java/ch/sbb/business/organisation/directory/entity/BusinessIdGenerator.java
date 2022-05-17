package ch.sbb.business.organisation.directory.entity;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion.Fields;
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
    Optional<String> presetSboid = getPresetSboid(entity);
    if (presetSboid.isPresent()) {
      return presetSboid.get();
    }

    long result = Long.parseLong(
        session.createNativeQuery("SELECT nextval('" + dbSequence + "') as nextval")
               .setFlushMode(FlushMode.COMMIT)
               .getSingleResult().toString());
    return businessIdPrefix + result;
  }

  private Optional<String> getPresetSboid(Object entity) {
    try {
      Field businessIdField = entity.getClass().getDeclaredField(dbField);
      businessIdField.trySetAccessible();
      String sboidValue = (String) businessIdField.get(entity);
      return Optional.ofNullable(sboidValue);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }
  }

  static class SboidGenerator extends BusinessIdGenerator {

    public SboidGenerator() {
      super("sboid_seq", "ch:1:sboid:", Fields.sboid);
    }
  }


}