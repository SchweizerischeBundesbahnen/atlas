package ch.sbb.prm.directory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.enumeration.StandardPrmAttributeType;
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
        .alternativeTransport(StandardPrmAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardPrmAttributeType.YES)
        .alternativeCondition("No alternative Bro!")
        .assistanceService(StandardPrmAttributeType.NO)
        .audioTicketMachine(StandardPrmAttributeType.PARTIALLY)
        .additionalInfo("No alternative")
        .dynamicAudioSystem(StandardPrmAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardPrmAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInfo("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardPrmAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardPrmAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(StandardPrmAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardPrmAttributeType.TO_BE_COMPLETED)
        .build();

  }

}
