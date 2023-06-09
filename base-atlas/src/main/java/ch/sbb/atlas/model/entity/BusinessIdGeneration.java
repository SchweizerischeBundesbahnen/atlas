package ch.sbb.atlas.model.entity;

import ch.sbb.atlas.model.entity.BusinessIdGeneration.BusinessIdValueGeneration;
import java.io.Serial;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Member;
import java.util.EnumSet;
import org.hibernate.annotations.ValueGenerationType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.AnnotationBasedGenerator;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.GeneratorCreationContext;

@ValueGenerationType(generatedBy = BusinessIdValueGeneration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessIdGeneration {

  Class<? extends BeforeExecutionGenerator> valueGenerator();

  class BusinessIdValueGeneration implements AnnotationBasedGenerator<BusinessIdGeneration>, BeforeExecutionGenerator {

    @Serial
    private static final long serialVersionUID = 1;

    protected BeforeExecutionGenerator valueGenerator;

    @Override
    public void initialize(BusinessIdGeneration annotation, Member member, GeneratorCreationContext context) {
      try {
        valueGenerator = annotation.valueGenerator().getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
      return valueGenerator.generate(session, owner, currentValue, eventType);
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
      return valueGenerator.getEventTypes();
    }
  }

}