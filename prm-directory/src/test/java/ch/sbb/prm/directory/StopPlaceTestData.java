package ch.sbb.prm.directory;

import static java.util.List.of;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.api.prm.model.stopplace.CreateStopPlaceVersionModel;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion.StopPlaceVersionBuilder;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
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
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .alternativeCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .additionalInfo("No alternative")
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInfo("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(StandardAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .build();

  }

  public static CreateStopPlaceVersionModel getStopPlaceCreateVersionModel(){
    return CreateStopPlaceVersionModel.builder()
        .sloid("ch:1.sloid:7000")
        .numberWithoutCheckDigit(8507000)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .meansOfTransport(of(MeanOfTransport.BUS, MeanOfTransport.BOAT))
        .freeText("I am a free text!!!")
        .address("Wylerstrasse 123")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .alternativeCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .additionalInfo("No alternative")
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInfo("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(StandardAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .build();

  }


  public StopPlaceVersionBuilder<?, ?> builderVersion1() {
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
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .alternativeCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .additionalInfo("No alternative")
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInfo("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(StandardAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED);
  }

  public StopPlaceVersionBuilder<?, ?> builderVersion2() {
    return StopPlaceVersion.builder()
        .sloid("ch:1.sloid:12345")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.BOAT))
        .freeText("I am a free text!!!")
        .address("Wylerstrasse 312")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(StandardAttributeType.YES)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .alternativeCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .additionalInfo("No alternative")
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInfo("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(StandardAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED);
  }

  public StopPlaceVersionBuilder<?, ?> builderVersion3() {
    return StopPlaceVersion.builder()
        .sloid("ch:1.sloid:12345")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2003, 1, 1))
        .validTo(LocalDate.of(2003, 12, 31))
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.BOAT))
        .freeText("I am a free man!!!")
        .address("Wylerstrasse 666")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(StandardAttributeType.YES)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .alternativeCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .additionalInfo("No alternative")
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInfo("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(StandardAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED);
  }


}
