package ch.sbb.exportservice.job.prm.stoppoint.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StopPointVersionJsonProcessorTest {

  @Test
  void shouldMapToReadModel() {
    StopPointVersion entity = StopPointVersion.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAM))
        .meansOfTransportPipeList("BUS|TRAM")
        .address("address")
        .zipCode("3014")
        .city("city")
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("alt tra")
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition("ass con")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .freeText("desig")
        .additionalInformation("add")
        .dynamicAudioSystem(StandardAttributeType.YES)
        .dynamicOpticSystem(StandardAttributeType.NOT_APPLICABLE)
        .infoTicketMachine("info tick")
        .interoperable("0")
        .url("www.a.bc")
        .visualInfo(StandardAttributeType.YES)
        .wheelchairTicketMachine(StandardAttributeType.PARTIALLY)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.YES)
        .ticketMachine(BooleanOptionalAttributeType.YES)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .status(Status.VALIDATED)
        .recordingObligation(true)
        .build();

    StopPointVersionJsonProcessor processor = new StopPointVersionJsonProcessor();

    ReadStopPointVersionModel expected = ReadStopPointVersionModel.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .meansOfTransport(List.of(MeanOfTransport.BUS, MeanOfTransport.TRAM))
        .address("address")
        .zipCode("3014")
        .city("city")
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .alternativeTransportCondition("alt tra")
        .assistanceAvailability(StandardAttributeType.YES)
        .assistanceCondition("ass con")
        .assistanceService(StandardAttributeType.NO)
        .audioTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .freeText("desig")
        .additionalInformation("add")
        .dynamicAudioSystem(StandardAttributeType.YES)
        .dynamicOpticSystem(StandardAttributeType.NOT_APPLICABLE)
        .infoTicketMachine("info tick")
        .interoperable(false)
        .url("www.a.bc")
        .visualInfo(StandardAttributeType.YES)
        .wheelchairTicketMachine(StandardAttributeType.PARTIALLY)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.YES)
        .ticketMachine(BooleanOptionalAttributeType.YES)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .status(Status.VALIDATED)
        .recordingObligation(true)
        .build();

    ReadStopPointVersionModel result = processor.process(entity);

    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }

}