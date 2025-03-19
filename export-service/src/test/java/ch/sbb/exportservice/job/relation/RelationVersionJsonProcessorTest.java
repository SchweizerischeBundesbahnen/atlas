package ch.sbb.exportservice.job.relation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.relation.RelationVersion;
import ch.sbb.exportservice.job.prm.relation.RelationVersionJsonProcessor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class RelationVersionJsonProcessorTest {

  @Test
  public void shouldMapToReadModel() {
    RelationVersion entity = RelationVersion.builder()
        .id(1L)
        .parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .sloid("ch:1:sloid:112:23")
        .parentServicePointSloid("ch:1:sloid:112")
        .tactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED)
        .contrastingAreas(StandardAttributeType.TO_BE_COMPLETED)
        .stepFreeAccess(StepFreeAccessAttributeType.TO_BE_COMPLETED)
        .referencePointElementType(ReferencePointElementType.TOILET)
        .referencePointSloid("ch:1:sloid:112:wc")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .build();

    RelationVersionJsonProcessor processor = new RelationVersionJsonProcessor();

    ReadRelationVersionModel expected = ReadRelationVersionModel.builder()
        .id(1L)
        .elementSloid("ch:1:sloid:112:23")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .parentServicePointSloid("ch:1:sloid:112")
        .tactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED)
        .contrastingAreas(StandardAttributeType.TO_BE_COMPLETED)
        .stepFreeAccess(StepFreeAccessAttributeType.TO_BE_COMPLETED)
        .referencePointElementType(ReferencePointElementType.TOILET)
        .referencePointSloid("ch:1:sloid:112:wc")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .build();

    ReadRelationVersionModel result = processor.process(entity);

    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }
}
