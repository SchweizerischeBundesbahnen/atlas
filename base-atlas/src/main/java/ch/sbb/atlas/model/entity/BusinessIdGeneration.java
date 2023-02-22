package ch.sbb.atlas.model.entity;

import ch.sbb.atlas.model.entity.BusinessIdGeneration.BusinessIdValueGeneration;
import java.io.Serial;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.hibernate.annotations.ValueGenerationType;
import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

@ValueGenerationType(generatedBy = BusinessIdValueGeneration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessIdGeneration {

  Class<? extends ValueGenerator<?>> valueGenerator();

  class BusinessIdValueGeneration implements AnnotationValueGeneration<BusinessIdGeneration> {

    @Serial
    private static final long serialVersionUID = 1;

    protected ValueGenerator<?> valueGenerator;

    @Override
    public void initialize(BusinessIdGeneration annotation, Class<?> propertyType) {
      try {
        valueGenerator = annotation.valueGenerator().getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }

    public GenerationTiming getGenerationTiming() {
      return GenerationTiming.INSERT;
    }

    public ValueGenerator<?> getValueGenerator() {
      return valueGenerator;
    }

    @Override
    public boolean referenceColumnInSql() {
      return false;
    }

    @Override
    public String getDatabaseGeneratedReferencedColumnValue() {
      return null;
    }
  }

}