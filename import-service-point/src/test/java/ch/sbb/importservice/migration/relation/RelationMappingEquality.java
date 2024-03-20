package ch.sbb.importservice.migration.relation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.importservice.migration.MigrationUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record RelationMappingEquality(RelationCsvModel didokCsvLine, RelationVersionCsvModel atlasCsvLine) {

    public void performCheck() {
        assertThat(atlasCsvLine.getParentNumberServicePoint()).isEqualTo(MigrationUtil.removeCheckDigit(didokCsvLine.getDidokCode()));
        assertThat(atlasCsvLine.getParentSloidServicePoint()).isEqualTo(didokCsvLine.getDsSloid());

        assertThat(atlasCsvLine.getElementSloid()).isEqualTo(didokCsvLine.getSloid());
        assertThat(atlasCsvLine.getReferencePointSloid()).isEqualTo(didokCsvLine.getRpSloid());

        assertThat(atlasCsvLine.getTactileVisualMarks().getRank()).isEqualTo(didokCsvLine.getTactVisualMarks());
        assertThat(atlasCsvLine.getContrastingAreas().getRank()).isEqualTo(didokCsvLine.getContrastingAreas());
        assertThat(atlasCsvLine.getStepFreeAccess().getRank()).isEqualTo(didokCsvLine.getStepFreeAccess());


        assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
        if (didokCsvLine.getModifiedAt().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            assertThat(localDateFromString(atlasCsvLine.getEditionDate()).toLocalDate())
                .isEqualTo(RelationMigrationIntegrationTest.ACTUAL_DATE);
        } else {
            assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());
        }
    }

    public LocalDateTime localDateFromString(String string) {
        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    }
}
