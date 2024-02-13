package ch.sbb.atlas.testdata.prm;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ContactPointCsvTestData {
    public static ContactPointCsvModelContainer getContainer() {
        return ContactPointCsvModelContainer.builder()
                .sloid(getCsvModel().getSloid())
                .csvModels(List.of(getCsvModel()))
                .build();
    }

    public static ContactPointCsvModel getCsvModel() {
        return ContactPointCsvModel.builder()
                .sloid("ch:1:sloid:294:787306")
                .dsSloid("ch:1:sloid:294")
                .didokCode(85002949)
                .status(1)
                .infos("Additional Info Example")
                .description("Description")
                .inductionLoop(1)
                .openHours("openHours")
                .status(1)
                .wheelChairAccess(1)
                .type(ContactPointType.INFORMATION_DESK)
                .validFrom(LocalDate.of(2020, 8, 25))
                .validTo(LocalDate.of(2025, 12, 31))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
