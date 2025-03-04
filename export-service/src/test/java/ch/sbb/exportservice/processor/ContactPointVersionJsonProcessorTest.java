package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.entity.prm.ContactPointVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ContactPointVersionJsonProcessorTest {

  @Test
  void shouldMapToReadModel() {
    ContactPointVersion entity = ContactPointVersion.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .designation("desig")
        .type(ContactPointType.INFORMATION_DESK)
        .additionalInformation("add")
        .inductionLoop(StandardAttributeType.NO)
        .openingHours("10:00-10:01")
        .wheelchairAccess(StandardAttributeType.YES)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .status(Status.VALIDATED)
        .build();

    ContactPointVersionJsonProcessor processor = new ContactPointVersionJsonProcessor();

    ReadContactPointVersionModel expected = ReadContactPointVersionModel.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .designation("desig")
        .type(ContactPointType.INFORMATION_DESK)
        .additionalInformation("add")
        .inductionLoop(StandardAttributeType.NO)
        .openingHours("10:00-10:01")
        .wheelchairAccess(StandardAttributeType.YES)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .status(Status.VALIDATED)
        .build();

    ReadContactPointVersionModel result = processor.process(entity);

    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }

}