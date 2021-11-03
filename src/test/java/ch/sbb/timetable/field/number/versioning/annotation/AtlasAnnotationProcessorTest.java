package ch.sbb.timetable.field.number.versioning.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AtlasAnnotationProcessorTest {

  private final AtlasAnnotationProcessor atlasAnnotationProcessor = new AtlasAnnotationProcessor();

  @Test
  public void shouldThrowAtlasVersionableExceptionWhenTheObjectIsNull(){

    //when
    assertThatThrownBy(() -> {
      atlasAnnotationProcessor.getVersionableProperties(null);
      //then
    }).isInstanceOf(AtlasVersionableException.class)
      .hasMessageContaining(
          "Can't versioning a null object.");

  }

  @Test
  public void shouldThrowAtlasVersionableExceptionWhenObjectToVersioningDoesNotImplementVersioningInterface() {
    //given
    @AtlasVersionable
    class NonVersionable {

      @AtlasVersionableProperty
      private String property;

      public String getProperty() {
        return property;
      }

      public void setProperty(String property) {
        this.property = property;
      }
    }

    NonVersionable nonVersionable = new NonVersionable();
    nonVersionable.setProperty("Ciao");

    //when
    assertThatThrownBy(() -> {
      atlasAnnotationProcessor.getVersionableProperties(nonVersionable);
      //then
    }).isInstanceOf(AtlasVersionableException.class)
      .hasMessageContaining(
          "The class NonVersionable must implement the interface Versionable. Please check the documentation.");

  }

  @Test
  public void shouldThrowAtlasVersionableExceptionWhenObjectIsNotAnnotatedWithAtlasVersionable() {
    //given
    class NonVersionable implements Versionable {

      @AtlasVersionableProperty
      private String property;

      public String getProperty() {
        return property;
      }

      public void setProperty(String property) {
        this.property = property;
      }

      @Override
      public LocalDate getValidFrom() {
        return null;
      }

      @Override
      public LocalDate getValidTo() {
        return null;
      }

      @Override
      public Long getId() {
        return null;
      }
    }

    NonVersionable nonVersionable = new NonVersionable();
    nonVersionable.setProperty("Ciao");

    //when
    assertThatThrownBy(() -> {
      atlasAnnotationProcessor.getVersionableProperties(nonVersionable);
      //then
    }).isInstanceOf(AtlasVersionableException.class)
      .hasMessageContaining(
          "The class NonVersionable is not annotated with @AtlasVersionable.  Please check the documentation.");

  }

  @Test
  public void shouldThrowAtlasVersionableExceptionWhenObjectHasNoOnePropertyAnnotatedWithAtlasVersionableProperty() {
    //given
    @AtlasVersionable
    class NonVersionable implements Versionable {

      private String property;

      public String getProperty() {
        return property;
      }

      public void setProperty(String property) {
        this.property = property;
      }

      @Override
      public LocalDate getValidFrom() {
        return null;
      }

      @Override
      public LocalDate getValidTo() {
        return null;
      }

      @Override
      public Long getId() {
        return null;
      }
    }

    NonVersionable nonVersionable = new NonVersionable();
    nonVersionable.setProperty("Ciao");

    //when
    assertThatThrownBy(() -> {
      atlasAnnotationProcessor.getVersionableProperties(nonVersionable);
      //then
    }).isInstanceOf(AtlasVersionableException.class)
      .hasMessageContaining(
          "To versioning an Object you have to mark some properties with @AtlasVersionableProperty. Please check the documentation.");

  }

  @Test
  public void shouldReturnVersionableProperties() {
    //given
    @AtlasVersionable
    class NonVersionable implements Versionable {

      @AtlasVersionableProperty
      private String property;

      public String getProperty() {
        return property;
      }

      public void setProperty(String property) {
        this.property = property;
      }

      @Override
      public LocalDate getValidFrom() {
        return null;
      }

      @Override
      public LocalDate getValidTo() {
        return null;
      }

      @Override
      public Long getId() {
        return null;
      }
    }

    NonVersionable nonVersionable = new NonVersionable();
    nonVersionable.setProperty("Ciao");

    //when
    List<VersionableProperty> versionableProperties = atlasAnnotationProcessor.getVersionableProperties(
        nonVersionable);
    //then
    assertThat(versionableProperties).isNotEmpty();
    assertThat(versionableProperties.size()).isEqualTo(1);
    VersionableProperty versionableProperty = versionableProperties.get(0);
    assertThat(versionableProperty).isNotNull();
    assertThat(versionableProperty.getFieldName()).isEqualTo("property");
    assertThat(versionableProperty.getRelationType()).isEqualTo(RelationType.NONE);
    assertThat(versionableProperty.getRelationsFields()).isEmpty();

  }


}