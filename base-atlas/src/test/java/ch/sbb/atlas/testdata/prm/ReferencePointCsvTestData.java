package ch.sbb.atlas.testdata.prm;

import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;

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

    public static ReferencePointCsvModel getCsvModel1() {
        return ReferencePointCsvModel.builder()
                .sloid("ch:1:sloid:5467:270449")
                .dsSloid("ch:1:sloid:5467")
                .didokCode(85054676)
                .status(1)
                .description("")
                .export(1)
                .infos("")
                .rpType(0)
                .validFrom(LocalDate.of(2023, 8, 4))
                .validTo(LocalDate.of(2023, 9, 14))
                .createdAt(LocalDateTime.of(2023, 8, 4, 9, 27))
                .modifiedAt(LocalDateTime.of(2023, 9, 18, 12, 48))
                .build();
    }

    public static ReferencePointCsvModel getCsvModel2() {
        return ReferencePointCsvModel.builder()
                .sloid("ch:1:sloid:5468:270449")
                .dsSloid("ch:1:sloid:5468")
                .didokCode(85054686)
                .status(1)
                .description("")
                .export(1)
                .infos("")
                .rpType(0)
                .validFrom(LocalDate.of(2023, 8, 4))
                .validTo(ImportUtils.DIDOK_HIGEST_DATE)
                .createdAt(LocalDateTime.of(2023, 8, 4, 9, 27))
                .modifiedAt(LocalDateTime.of(2023, 9, 18, 12, 48))
                .build();
    }

    public static ReferencePointCsvModel getCsvModel3() {
        return ReferencePointCsvModel.builder()
                .sloid("ch:1:sloid:5469:270449")
                .dsSloid("ch:1:sloid:5469")
                .didokCode(85054696)
                .status(1)
                .description("")
                .export(1)
                .infos("")
                .rpType(0)
                .validFrom(LocalDate.of(2023, 8, 4))
                .validTo(ImportUtils.DIDOK_HIGEST_DATE)
                .createdAt(LocalDateTime.of(2023, 8, 4, 9, 27))
                .modifiedAt(LocalDateTime.of(2023, 9, 18, 12, 48))
                .build();
    }

}
