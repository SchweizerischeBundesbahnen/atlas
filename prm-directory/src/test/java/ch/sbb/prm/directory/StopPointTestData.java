package ch.sbb.prm.directory;

import static java.util.List.of;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.StopPointVersion.StopPointVersionBuilder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class StopPointTestData {

  public static StopPointVersion getStopPointVersion() {
    Set<MeanOfTransport> meanOfTransport = new HashSet<>();
    meanOfTransport.add(MeanOfTransport.TRAIN);
    meanOfTransport.add(MeanOfTransport.METRO);

    return StopPointVersion.builder()
        .sloid("ch:1:sloid:12345")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .meansOfTransport(meanOfTransport)
        .freeText("I am a free text!!!")
        .address("Wylerstrasse 123")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInformation("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .build();

  }

  public static CreateStopPointVersionModel getStopPointCreateVersionModel() {
    return CreateStopPointVersionModel.builder()
        .sloid("ch:1:sloid:7000")
        .numberWithoutCheckDigit(8507000)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .meansOfTransport(of(MeanOfTransport.TRAIN, MeanOfTransport.METRO))
        .freeText("I am a free text!!!")
        .address("Wylerstrasse 123")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInformation("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .build();

  }

  public static CreateStopPointVersionModel getWrongStopPointReducedCreateVersionModel() {
    return CreateStopPointVersionModel.builder()
        .sloid("ch:1:sloid:7000")
        .numberWithoutCheckDigit(8507000)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .meansOfTransport(of(MeanOfTransport.TRAM))
        .freeText("I am a free text!!!")
        .address("Wylerstrasse 123")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInformation("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .build();

  }

  public static CreateStopPointVersionModel getCompleteNotValidatableStopPointReducedCreateVersionModel() {
    return CreateStopPointVersionModel.builder()
        .sloid("ch:1:sloid:7000")
        .numberWithoutCheckDigit(8507000)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .meansOfTransport(of(MeanOfTransport.TRAIN))
        .freeText(RandomStringUtils.random(AtlasFieldLengths.LENGTH_2000 + 1,true,false))
        .address(RandomStringUtils.random(AtlasFieldLengths.LENGTH_2000 + 1,true,false))
        .zipCode(RandomStringUtils.random(AtlasFieldLengths.LENGTH_50 + 1,true,false))
        .city(RandomStringUtils.random(AtlasFieldLengths.LENGTH_75 + 1,true,false))
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition(RandomStringUtils.random(AtlasFieldLengths.LENGTH_2000 + 1,true,false))
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition(RandomStringUtils.random(AtlasFieldLengths.LENGTH_2000 + 1,true,false))
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine(RandomStringUtils.random(AtlasFieldLengths.LENGTH_2000 + 1,true,false))
        .additionalInformation(RandomStringUtils.random(AtlasFieldLengths.LENGTH_2000 + 1,true,false))
        .interoperable(true)
        .url(RandomStringUtils.random(AtlasFieldLengths.LENGTH_500 + 1,true,false))
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .build();
  }

  public StopPointVersionBuilder<?, ?> builderVersion1() {
    return StopPointVersion.builder()
        .sloid("ch:1:sloid:12345")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8512345))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN, MeanOfTransport.METRO))
        .freeText("I am a free text!!!")
        .address("Wylerstrasse 123")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInformation("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED);
  }

  public StopPointVersionBuilder<?, ?> builderVersion2() {
    return StopPointVersion.builder()
        .sloid("ch:1:sloid:12345")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8512345))
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2002, 12, 31))
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN, MeanOfTransport.METRO))
        .freeText("I am a free text!!!")
        .address("Wylerstrasse 312")
        .zipCode("3014")
        .city("Bern")
        .alternativeTransport(StandardAttributeType.YES)
        .alternativeTransportCondition("No way dude!!")
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInformation("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED);
  }

  public StopPointVersionBuilder<?, ?> builderVersion3() {
    return StopPointVersion.builder()
        .sloid("ch:1:sloid:12345")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8512345))
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
        .assistanceCondition("No alternative Bro!")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.PARTIALLY)
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .infoTicketMachine("tick")
        .additionalInformation("additional")
        .interoperable(true)
        .url("https://www.prm.sbb")
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .ticketMachine(StandardAttributeType.TO_BE_COMPLETED);
  }

}
