package ch.sbb.atlas.testdata.prm;

import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ReferencePointCsvTestData {

    public static ReferencePointCsvModelContainer getContainer() {
        return ReferencePointCsvModelContainer.builder()
                .sloid(getCsvModel().getSloid())
                .csvModels(List.of(getCsvModel()))
                .build();
    }

    public static ReferencePointCsvModel getCsvModel() {
        return ReferencePointCsvModel.builder()
                .sloid("ch:1:sloid:294:787306")
                .dsSloid("ch:1:sloid:294")
                .didokCode(85002949)
                .status(1)
                .description("Perron BTH")
                .export(1)
                .infos("Additional Info Example")
                .rpType(4)
                .validFrom(LocalDate.of(2020, 8, 25))
                .validTo(LocalDate.of(2025, 12, 31))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

}
