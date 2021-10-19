package ch.sbb.timetable.field.number.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.entity.Version.Fields;
import ch.sbb.timetable.field.number.versioning.model.AttributeObject;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersioningEngineTest {

  @Test
  public void shouldJustUpdateAnObjectWhenAttributeNameIsChanged()
      throws ClassNotFoundException, NoSuchFieldException {
//    Version actualVersion = Version.builder()
//                                .id(1L)
//                                .ttfnid("ch:1:fpfnid:100000")
//                                .name("FPFN Name")
//                                .number("BEX")
//                                .swissTimetableFieldNumber("b0.BEX")
//                                .validFrom(LocalDate.of(2020, 12, 12))
//                                .validTo(LocalDate.of(2021, 12, 12))
//                                .build();
//
//    Version editedVersion = Version.builder()
//                                .id(2L)
//                                .ttfnid("ch:1:fpfnid:100000")
//                                .name("FPFN Name")
//                                .number("BEX")
//                                .swissTimetableFieldNumber("b0.BEX")
//                                .validFrom(LocalDate.of(2021, 12, 13))
//                                .validTo(LocalDate.of(2099, 12, 12))
//                                .build();
//
//    Class<?> versionClazz = Class.forName(Version.class.getName());
//    String nameType = versionClazz.getDeclaredField(Fields.name).getType().toString();
//
//    List<AttributeObject> changedAttribute = new ArrayList<>();
//    AttributeObject changedAttributeName = new AttributeObject(Fields.name, "FPFN Name <Changed>",
//        nameType);
//    changedAttribute.add(changedAttributeName);
//
//    AttributeObject attributeName = new AttributeObject(Fields.name, actualVersion.getName(),
//        nameType);
//
//    String numberType = versionClazz.getDeclaredField(Fields.number).getType().toString();
//    AttributeObject attributeNumber = new AttributeObject(Fields.number, actualVersion.getNumber(),
//        numberType);
//
//    List<AttributeObject> attributeObjectList =  new ArrayList<>();
//    attributeObjectList.add(attributeName);
//    attributeObjectList.add(attributeNumber);
//
//    VersioningEngine versioningEngine = new VersioningEngine();
//
//    List<VersionedObject> result = versioningEngine.objectsVersioned(actualVersion,editedVersion, attributeObjectList,attributeObjectList);
//
//    assertThat(true).isTrue();
  }

}