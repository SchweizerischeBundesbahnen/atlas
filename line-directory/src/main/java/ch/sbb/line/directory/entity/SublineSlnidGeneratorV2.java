package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.entity.SublineSlnidGeneratorV2.FunctionCreationValueGeneration;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.hibernate.annotations.ValueGenerationType;
import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

@ValueGenerationType(generatedBy = FunctionCreationValueGeneration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface SublineSlnidGeneratorV2 {

  class FunctionCreationValueGeneration
      implements AnnotationValueGeneration<SublineSlnidGeneratorV2> {

    @Override
    public void initialize(SublineSlnidGeneratorV2 annotation, Class<?> propertyType) {
      System.out.println("init");
    }

    public GenerationTiming getGenerationTiming() {
      // its creation...
      return GenerationTiming.INSERT;
    }

    public ValueGenerator<?> getValueGenerator() {
      // no in-memory generation
      return null;
    }

    public boolean referenceColumnInSql() {
      return true;
    }

    public String getDatabaseGeneratedReferencedColumnValue() {
      return "SELECT count(*)+1 from subline_version where mainline_slnid = ?";
    }
  }
}
