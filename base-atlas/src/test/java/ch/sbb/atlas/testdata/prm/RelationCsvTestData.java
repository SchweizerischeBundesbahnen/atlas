package ch.sbb.atlas.testdata.prm;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class RelationCsvTestData {
    public static RelationCsvModelContainer getContainer() {
        return RelationCsvModelContainer.builder()
                .sloid(getCsvModel().getSloid())
                .csvModels(List.of(getCsvModel()))
                .build();
    }


    public static RelationCsvModel getCsvModel() {
        return RelationCsvModel.builder()
                .sloid("ch:1:sloid:294:787306")
                .dsSloid("ch:1:sloid:294")
                .didokCode(85002949)
                .status(1)
                .elType("platform")
                .rpSloid("ch:1:sloid:294:1")
                .contrastingAreas(1)
                .stepFreeAccess(1)
                .tactVisualMarks(1)
                .validFrom(LocalDate.of(2020, 8, 25))
                .validTo(LocalDate.of(2025, 12, 31))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
