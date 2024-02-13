package ch.sbb.exportservice.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.export.model.prm.ContactPointVersionCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ContactPointVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ContactPointVersionCsvProcessorTest {

    private final ContactPointVersionCsvProcessor processor = new ContactPointVersionCsvProcessor();

    @Test
    void shouldMapToCsvModel() {
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDateTime editionDate = LocalDateTime.now();
        ContactPointVersion entity = ContactPointVersion.builder()
                .id(1L)
                .sloid("ch:1:sloid:112:23")
                .parentServicePointSloid("ch:1:sloid:112")
                .parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
                .designation("Haupteingang")
                .additionalInformation("Langer Text")
                .inductionLoop(StandardAttributeType.TO_BE_COMPLETED)
                .openingHours("Während der Fahrplanzeiten der Linie 2830")
                .wheelchairAccess(StandardAttributeType.TO_BE_COMPLETED)
                .type(ContactPointType.TICKET_COUNTER)
                .validFrom(LocalDate.of(2020, 1, 1))
                .validTo(LocalDate.of(2020, 12, 31))
                .creationDate(creationDate)
                .editionDate(editionDate)
                .build();

        ContactPointVersionCsvModel expected = ContactPointVersionCsvModel.builder()
                .sloid("ch:1:sloid:112:23")
                .parentSloidServicePoint("ch:1:sloid:112")
                .parentNumberServicePoint(8500112)
                .designation("Haupteingang")
                .additionalInformation("Langer Text")
                .inductionLoop(StandardAttributeType.TO_BE_COMPLETED.toString())
                .openingHours("Während der Fahrplanzeiten der Linie 2830")
                .wheelchairAccess(StandardAttributeType.TO_BE_COMPLETED.toString())
                .type(ContactPointType.TICKET_COUNTER.toString())
                .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 1, 1)))
                .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 12, 31)))
                .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(creationDate))
                .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(editionDate))
                .build();

        ContactPointVersionCsvModel result = processor.process(entity);

        assertThat(result).isEqualTo(expected);
    }

}
