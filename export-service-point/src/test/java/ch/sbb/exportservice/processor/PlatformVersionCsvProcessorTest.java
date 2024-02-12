package ch.sbb.exportservice.processor;


import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.PlatformVersion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PlatformVersionCsvProcessorTest {

    private final PlatformVersionCsvProcessor processor = new PlatformVersionCsvProcessor();



    @Test
    void shouldMapToCsvModel() {
        PlatformVersion entity = PlatformVersion.builder()
                .id(1L)
                .parentNumberServicePoint(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
                .sloid("ch:1:sloid:112:23")
                .parentServicePointSloid("ch:1:sloid:112")
                .wheelchairAreaWidth(1.5)
                .wheelchairAreaLength(1.5)
                .infoOpportunitiesPipeList("TO_BE_COMPLETED")
                .additionalInformation("String")
                .adviceAccessInfo("Empty")
                .height(1.5)
                .inclination(1.5)
                .inclinationLongitudinal(1.5)
                .inclinationWidth(1.6)
                .validFrom(LocalDate.of(2020, 1, 1))
                .validTo(LocalDate.of(2020, 12, 31))
                .creationDate(LocalDateTime.now())
                .editionDate(LocalDateTime.now())
                .build();

        PlatformVersionCsvModel expected = PlatformVersionCsvModel.builder()
                .sloid("ch:1:sloid:112:23")
                .parentSloidServicePoint("ch:1:sloid:112")
                .parentNumberServicePoint(1234567)
                .wheelChairAreaLength(1.5)
                .wheelChairAreaWidth(1.5)
                .infoOpportunities("TO_BE_COMPLETED")
                .additionalInformation("String")
                .adviceAccessInfo("Empty")
                .height(1.5)
                .inclination(1.5)
                .inclinationLongitudinal(1.5)
                .inclinationWidth(1.6)
                .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 1, 1)))
                .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 12, 31)))
                .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(LocalDateTime.now()))
                .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(LocalDateTime.now()))
                .build();

        PlatformVersionCsvModel result = processor.process(entity);

        assertThat(result).isEqualTo(expected);
    }
}
