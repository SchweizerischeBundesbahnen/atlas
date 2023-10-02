package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.enumeration.BasicPrmAttributeStatus;
import java.time.LocalDate;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPlaceTestData {

  public static StopPlaceVersion getStopPlaceVersion(){
    return StopPlaceVersion.builder()
        .sloid("ch:1.sloid:12345")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.BOAT))
        .freeText("I am a free text!!!")
        .address("Wylerstrasse 123")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(BasicPrmAttributeStatus.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(BasicPrmAttributeStatus.YES)
        .alternativeCondition("No alternative Bro!")
        .assistanceService(BasicPrmAttributeStatus.NO)
        .audioTicketMachine(BasicPrmAttributeStatus.PARTIALLY)
        .additionalInfo("No alternative")
        .dynamicAudioSystem(BasicPrmAttributeStatus.TO_BE_COMPLETED)
        .dynamicOpticSystem(BasicPrmAttributeStatus.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInfo("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(BasicPrmAttributeStatus.TO_BE_COMPLETED)
        .wheelchairTicketMachine(BasicPrmAttributeStatus.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BasicPrmAttributeStatus.TO_BE_COMPLETED)
        .ticketMachine(BasicPrmAttributeStatus.TO_BE_COMPLETED)
        .build();

  }

}
