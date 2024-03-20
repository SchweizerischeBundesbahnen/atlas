package ch.sbb.importservice.migration.contactpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.model.prm.ContactPointVersionCsvModel;
import ch.sbb.atlas.export.utils.StringUtils;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvToModelMapper;
import ch.sbb.atlas.model.Status;
import ch.sbb.importservice.migration.MigrationUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ContactPointMappingEquality(ContactPointCsvModel didokCsvLine, ContactPointVersionCsvModel atlasCsvLine) {

    public void performCheck() {
        assertThat(atlasCsvLine.getParentNumberServicePoint()).isEqualTo(MigrationUtil
            .removeCheckDigit(didokCsvLine.getDidokCode()));
        assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
        assertThat(atlasCsvLine.getWheelchairAccess()).isEqualTo(ContactPointCsvToModelMapper
            .mapStandardAttributeType(didokCsvLine.getWheelChairAccess()).toString());
        assertThat(atlasCsvLine.getInductionLoop()).isEqualTo(ContactPointCsvToModelMapper
            .mapStandardAttributeType(didokCsvLine.getInductionLoop()).toString());
        assertThat(atlasCsvLine.getOpeningHours()).isEqualTo(StringUtils.removeNewLine(didokCsvLine.getOpenHours()));
        assertThat(atlasCsvLine.getAdditionalInformation()).isEqualTo(StringUtils.removeNewLine(didokCsvLine.getInfos()));
        if (atlasCsvLine.getParentSloidServicePoint() != null && didokCsvLine.getDsSloid() != null) {
            assertThat(atlasCsvLine.getParentSloidServicePoint()).isEqualTo(
                    didokCsvLine.getDsSloid());
        }
        if (atlasCsvLine.getDesignation() != null && didokCsvLine.getDescription() != null) {
            assertThat(atlasCsvLine.getDesignation()).isEqualTo((didokCsvLine.getDescription()));
        }

        assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
        if (didokCsvLine.getModifiedAt().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            assertThat(localDateFromString(atlasCsvLine.getEditionDate()).toLocalDate())
                .isEqualTo(ContactPointMigrationActualDateIntegrationTest.ACTUAL_DATE);
        } else {
            assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());
        }
        assertThat(atlasCsvLine.getStatus()).isEqualTo(Status.VALIDATED);
    }

    public LocalDateTime localDateFromString(String string) {
        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    }
}
