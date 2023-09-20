package ch.sbb.atlas.versioning.service;

import ch.sbb.atlas.versioning.BaseTest;
import ch.sbb.atlas.versioning.model.Property;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;

public abstract class VersionableServiceBaseTest extends BaseTest {

  protected final VersionableService versionableService = new VersionableServiceImpl();

  protected VersionableObject versionableObject1;
  protected VersionableObject versionableObject2;
  protected VersionableObject versionableObject3;
  protected VersionableObject versionableObject4;

  @BeforeEach
   void init() {
    versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .anotherProperty("prop1")
        .build();
    versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .property("Ciao2")
        .anotherProperty("prop2")
        .build();
    versionableObject3 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .property("Ciao3")
        .anotherProperty("prop3")
        .build();
    versionableObject4 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2025, 1, 1))
        .validTo(LocalDate.of(2025, 12, 31))
        .property("Ciao4")
        .anotherProperty("prop4")
        .build();

  }

  protected Property filterProperty(List<Property> properties, String fieldProperty) {
    return properties.stream().filter(property -> fieldProperty.equals(
                         property.getKey()))
                     .findFirst()
                     .orElse(null);
  }

}
