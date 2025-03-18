package ch.sbb.exportservice.job.relation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.BaseServicePointProcessor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class RelationVersionCsvProcessorTest {

  private final RelationVersionCsvProcessor processor = new RelationVersionCsvProcessor();

  @Test
  void shouldMapToCsvModel() {
    LocalDateTime creationDate = LocalDateTime.now();
    LocalDateTime editionDate = LocalDateTime.now();
    RelationVersion entity = RelationVersion.builder()
        .id(1L)
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8500112))
        .tactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED)
        .contrastingAreas(StandardAttributeType.TO_BE_COMPLETED)
        .stepFreeAccess(StepFreeAccessAttributeType.TO_BE_COMPLETED)
        .referencePointElementType(ReferencePointElementType.TOILET)
        .referencePointSloid("ch:1:sloid:112:wc")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(creationDate)
        .editionDate(editionDate)
        .build();

    RelationVersionCsvModel expected = RelationVersionCsvModel.builder()
        .elementSloid("ch:1:sloid:112:23")
        .parentSloidServicePoint("ch:1:sloid:112")
        .parentNumberServicePoint(8500112)
        .tactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED)
        .contrastingAreas(StandardAttributeType.TO_BE_COMPLETED)
        .stepFreeAccess(StepFreeAccessAttributeType.TO_BE_COMPLETED)
        .referencePointElementType(ReferencePointElementType.TOILET)
        .referencePointSloid("ch:1:sloid:112:wc")
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 1, 1)))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(LocalDate.of(2020, 12, 31)))
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(creationDate))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(editionDate))
        .build();

    RelationVersionCsvModel result = processor.process(entity);

    assertThat(result).isEqualTo(expected);
  }
}