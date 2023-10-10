package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.controller.model.informationdesk.CreateInformationDeskVersionModel;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InformationDeskTestData {

  public static InformationDeskVersion getInformationDeskVersion(){
    return InformationDeskVersion.builder()
        .sloid("ch:1.sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .designation("Designation")
        .info("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .build();
  }


  public static CreateInformationDeskVersionModel getCreateInformationDeskVersionModel(){
    return CreateInformationDeskVersionModel.builder()
        .sloid("ch:1.sloid:12345:1")
        .numberWithoutCheckDigit(8507000)
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .parentServicePointSloid("ch:1.sloid:12345")
        .designation("Designation")
        .info("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .build();
  }

}
