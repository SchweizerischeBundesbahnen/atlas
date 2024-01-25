package ch.sbb.prm.directory;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.informationdesk.InformationDeskVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.entity.InformationDeskVersion.InformationDeskVersionBuilder;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InformationDeskTestData {

  public static InformationDeskVersion getInformationDeskVersion(){
    return InformationDeskVersion.builder()
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
        .build();
  }

  public static InformationDeskVersionBuilder<?, ?> builderVersion1(){
    return InformationDeskVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES);
  }

  public static InformationDeskVersionBuilder<?, ?> builderVersion2(){
    return InformationDeskVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation wrong")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES);
  }

  public static InformationDeskVersionBuilder<?, ?> builderVersion3(){
    return InformationDeskVersion.builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2004, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation ok")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES);
  }

  public static InformationDeskVersionModel getInformationDeskVersionModel(){
    return InformationDeskVersionModel.builder()
        .sloid("ch:1:sloid:12345:1")
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .build();
  }

}
