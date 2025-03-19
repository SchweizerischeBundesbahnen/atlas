package ch.sbb.exportservice.job.stoppoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.job.prm.stoppoint.StopPointVersion;
import ch.sbb.exportservice.job.prm.stoppoint.StopPointVersionCsvProcessor;
import ch.sbb.exportservice.util.MapperUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StopPointVersionCsvProcessorTest {

  private final StopPointVersionCsvProcessor processor = new StopPointVersionCsvProcessor();

  @Test
  void shouldMapToCsvModel() {
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
        .build();

    StopPointVersionCsvModel expected = StopPointVersionCsvModel.builder()
        .sloid("ch:1:sloid:112:23")
        .number(8500112)
        .checkDigit(3)
        .meansOfTransport("BUS|TRAM")
        .address("address")
        .zipCode("3014")
        .city("city")
        .alternativeTransport("TO_BE_COMPLETED")
        .alternativeTransportCondition("alt tra")
        .assistanceAvailability("YES")
        .assistanceCondition("ass con")
        .assistanceService("NO")
        .audioTicketMachine("TO_BE_COMPLETED")
        .freeText("desig")
        .additionalInformation("add")
        .dynamicAudioSystem("YES")
        .dynamicOpticSystem("NOT_APPLICABLE")
        .infoTicketMachine("info tick")
        .interoperable("0")
        .url("www.a.bc")
        .visualInfo("YES")
        .wheelchairTicketMachine("PARTIALLY")
        .assistanceRequestFulfilled("YES")
        .ticketMachine("YES")
        .validFrom(MapperUtil.DATE_FORMATTER.format(LocalDate.of(2020, 1, 1)))
        .validTo(MapperUtil.DATE_FORMATTER.format(LocalDate.of(2020, 12, 31)))
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(LocalDateTime.of(2024, 2, 7, 20, 0)))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(LocalDateTime.of(2024, 2, 7, 20, 0)))
        .status(Status.VALIDATED)
        .build();

    StopPointVersionCsvModel result = processor.process(entity);

    assertThat(result).isEqualTo(expected);
  }

}