package ch.sbb.atlas.versioning.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty;
import ch.sbb.atlas.versioning.model.VersionableProperty.RelationType;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class AtlasAnnotationProcessorTest {

  private final AtlasAnnotationProcessor atlasAnnotationProcessor = new AtlasAnnotationProcessor();

  @Test
  void shouldThrowAtlasVersionableExceptionWhenTheObjectIsNull() {

    //when
    assertThatThrownBy(() -> {
      atlasAnnotationProcessor.getVersionableProperties(null);
      //then
    }).isInstanceOf(AtlasVersionableException.class)
        .hasMessageContaining(
            "Can't versioning a null object.");

  }

  @Test
  void shouldThrowAtlasVersionableExceptionWhenObjectToVersioningDoesNotImplementVersioningInterface() {
    //given
    @AtlasVersionable
    class NonVersionable {

      @AtlasVersionableProperty
      private String property;

      void setProperty(String property) {
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
  void shouldThrowAtlasVersionableExceptionWhenObjectIsNotAnnotatedWithAtlasVersionable() {
    //given
    class NonVersionable implements Versionable {

      @AtlasVersionableProperty
      private String property;

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

      void setProperty(String property) {
        this.property = property;
      }

      @Override
      public void setValidFrom(LocalDate validFrom) {

      }

      @Override
      public void setValidTo(LocalDate validTo) {

      }

      @Override
      public void setId(Long id) {

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
            "The class NonVersionable is not annotated with @AtlasVersionable. Please check the documentation.");

  }

  @Test
  void shouldThrowAtlasVersionableExceptionWhenObjectHasNoOnePropertyAnnotatedWithAtlasVersionableProperty() {
    //given
    @AtlasVersionable
    class NonVersionable implements Versionable {

      private String property;

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

      public void setProperty(String property) {
        this.property = property;
      }

      @Override
      public void setValidFrom(LocalDate validFrom) {

      }

      @Override
      public void setValidTo(LocalDate validTo) {

      }

      @Override
      public void setId(Long id) {

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
            "To versioning an Object you have to mark some properties with @AtlasVersionableProperty. Please check the "
                + "documentation.");

  }

  @Test
  void shouldReturnVersionableProperties() {
    //given
    @AtlasVersionable
    class NonVersionable implements Versionable {

      @AtlasVersionableProperty
      private String property;

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

      void setProperty(String property) {
        this.property = property;
      }

      @Override
      public void setValidFrom(LocalDate validFrom) {

      }

      @Override
      public void setValidTo(LocalDate validTo) {

      }

      @Override
      public void setId(Long id) {

      }
    }

    NonVersionable nonVersionable = new NonVersionable();
    nonVersionable.setProperty("Ciao");

    //when
    List<VersionableProperty> versionableProperties = atlasAnnotationProcessor.getVersionableProperties(
        nonVersionable);
    //then
    assertThat(versionableProperties).hasSize(1);
    VersionableProperty versionableProperty = versionableProperties.get(0);
    assertThat(versionableProperty).isNotNull();
    assertThat(versionableProperty.getFieldName()).isEqualTo("property");
    assertThat(versionableProperty.getRelationType()).isEqualTo(RelationType.NONE);
    assertThat(versionableProperty.getRelationsFields()).isEmpty();

  }

  @Test
  void shouldReturnVersionablePropertiesWithSuperclass() {
    //given
    class ParentVersionable {

      @AtlasVersionableProperty
      private String additionalProperty;

      void setAdditionalProperty(String additionalProperty) {
        this.additionalProperty = additionalProperty;
      }
    }

    @AtlasVersionable
    class ChildVersionable extends ParentVersionable implements Versionable {

      @AtlasVersionableProperty
      private String property;

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

      public void setProperty(String property) {
        this.property = property;
      }

      @Override
      public void setValidFrom(LocalDate validFrom) {

      }

      @Override
      public void setValidTo(LocalDate validTo) {

      }

      @Override
      public void setId(Long id) {

      }
    }

    ChildVersionable childVersionable = new ChildVersionable();
    childVersionable.setProperty("Ciao");
    childVersionable.setAdditionalProperty("Bella");

    //when
    List<VersionableProperty> versionableProperties = atlasAnnotationProcessor.getVersionableProperties(
        childVersionable);
    //then
    assertThat(versionableProperties).hasSize(2);

    VersionableProperty versionableProperty = versionableProperties.get(0);
    assertThat(versionableProperty).isNotNull();
    assertThat(versionableProperty.getFieldName()).isEqualTo("property");

    VersionableProperty additionalProperty = versionableProperties.get(1);
    assertThat(additionalProperty).isNotNull();
    assertThat(additionalProperty.getFieldName()).isEqualTo("additionalProperty");
  }

}