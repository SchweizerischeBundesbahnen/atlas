package ch.sbb.prm.directory;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ContactPointVersion.ContactPointVersionBuilder;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContactPointTestData {

  public static ContactPointVersion getContactPointVersion() {
    return ContactPointVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .type(ContactPointType.INFORMATION_DESK)
        .build();
  }

  public static ContactPointVersionBuilder<?, ?> builderVersion1() {
    return ContactPointVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .type(ContactPointType.INFORMATION_DESK);
  }

  public static ContactPointVersionBuilder<?, ?> builderVersion2() {
    return ContactPointVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation wrong")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .type(ContactPointType.INFORMATION_DESK);
  }

  public static ContactPointVersionBuilder<?, ?> builderVersion3() {
    return ContactPointVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation ok")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .type(ContactPointType.INFORMATION_DESK);
  }

  public static ContactPointVersionModel getContactPointVersionModel() {
    return ContactPointVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .type(ContactPointType.INFORMATION_DESK)
        .build();
  }
}
