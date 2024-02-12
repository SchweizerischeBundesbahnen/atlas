package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.PlatformVersion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PlatformVersionJsonProcessorTest {

    @Test
    public void shouldMapToReadModel() throws Exception {
        PlatformVersion entity = PlatformVersion.builder()
                .id(1L)
                .parentNumberServicePoint(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
                .sloid("ch:1:sloid:112:23")
                .parentServicePointSloid("ch:1:sloid:112")
                .wheelchairAreaWidth(1.5)
                .wheelchairAreaLength(1.5)
                .infoOpportunities(Set.of(InfoOpportunityAttributeType.TO_BE_COMPLETED))
                .additionalInformation("String")
                .adviceAccessInfo("Empty")
                .height(1.5)
                .inclination(1.5)
                .inclinationLongitudinal(1.5)
                .inclinationWidth(1.6)
                .validFrom(LocalDate.of(2020, 1, 1))
                .validTo(LocalDate.of(2020, 12, 31))
                .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
                .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
                .build();

        PlatformVersionJsonProcessor processor = new PlatformVersionJsonProcessor();

        ReadPlatformVersionModel expected = ReadPlatformVersionModel.builder()
                .id(1L)
                .sloid("ch:1:sloid:112:23")
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
                .parentServicePointSloid("ch:1:sloid:112")
                .wheelchairAreaLength(1.5)
                .wheelchairAreaWidth(1.5)
                .infoOpportunities(List.of(InfoOpportunityAttributeType.TO_BE_COMPLETED))
                .additionalInformation("String")
                .adviceAccessInfo("Empty")
                .height(1.5)
                .inclination(1.5)
                .inclinationLongitudinal(1.5)
                .inclinationWidth(1.6)
                .validFrom(LocalDate.of(2020, 1, 1))
                .validTo(LocalDate.of(2020, 12, 31))
                .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
                .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
                .build();

        ReadPlatformVersionModel result = processor.process(entity);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

    }
}
