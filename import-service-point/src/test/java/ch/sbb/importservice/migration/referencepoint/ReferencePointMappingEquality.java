package ch.sbb.importservice.migration.referencepoint;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.importservice.migration.MigrationUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public record ReferencePointMappingEquality(ReferencePointCsvModel didokCsvLine, ReferencePointVersionCsvModel atlasCsvLine) {

    public void performCheck() {
        assertThat(atlasCsvLine.getParentNumberServicePoint()).isEqualTo(MigrationUtil.removeCheckDigit(didokCsvLine.getDidokCode()));
        assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
        if (atlasCsvLine.getParentSloidServicePoint() != null && didokCsvLine.getDsSloid() != null) {
            assertThat(atlasCsvLine.getParentSloidServicePoint()).isEqualTo(
                    didokCsvLine.getDsSloid());
        }
        if (atlasCsvLine.getDesignation() != null && didokCsvLine.getDescription() != null) {
            assertThat(atlasCsvLine.getDesignation()).isEqualTo((didokCsvLine.getDescription()));
        }
        assertThat(atlasCsvLine.isMainReferencePoint()).isEqualTo((didokCsvLine.getExport() == 1));
        if(atlasCsvLine.getAdditionalInformation() != null && didokCsvLine.getInfos() != null){
            String didokInfos = didokCsvLine.getInfos().replaceAll("\r\n|\r|\n", " ");
            assertThat(atlasCsvLine.getAdditionalInformation()).isEqualTo(didokInfos);
        }
        if (atlasCsvLine.getRpType() != null && didokCsvLine.getRpType() != null) {
            assertThat(atlasCsvLine.getRpType()).isEqualTo(ReferencePointAttributeType.of(didokCsvLine.getRpType()).name());
        }
        assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
        assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());

    }

    public LocalDateTime localDateFromString(String string) {
        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    }
}
