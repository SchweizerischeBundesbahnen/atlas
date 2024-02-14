package ch.sbb.importservice.migration.toilet;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModel;
import ch.sbb.importservice.migration.MigrationUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ToiletMappingEquality(ToiletCsvModel didokCsvLine, ToiletVersionCsvModel atlasCsvLine) {

    public void performCheck() {
        assertThat(atlasCsvLine.getParentNumberServicePoint()).isEqualTo(MigrationUtil.removeCheckDigit(didokCsvLine.getDidokCode()));
        assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
        if (atlasCsvLine.getParentSloidServicePoint() != null && didokCsvLine.getDsSloid() != null) {
            assertThat(atlasCsvLine.getParentSloidServicePoint()).isEqualTo(
                    didokCsvLine.getDsSloid());
        }
        assertThat(atlasCsvLine.getDesignation()).isEqualTo(didokCsvLine.getDescription());
        if (atlasCsvLine.getDesignation() != null && didokCsvLine.getDescription() != null) {
            assertThat(atlasCsvLine.getDesignation()).isEqualTo((didokCsvLine.getDescription()));
        }
        if(atlasCsvLine.getAdditionalInformation() != null && didokCsvLine.getInfo() != null){
            String didokInfos = didokCsvLine.getInfo().replaceAll("\r\n|\r|\n", " ");
            assertThat(atlasCsvLine.getAdditionalInformation()).isEqualTo(didokInfos);
        }
        if (atlasCsvLine.getWheelchairToilet() != null && didokCsvLine.getWheelchairToilet() != null) {
            assertThat(atlasCsvLine.getWheelchairToilet()).isEqualTo(StandardAttributeType.from(didokCsvLine.getWheelchairToilet()).name());
        }
        assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
        assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());

    }

    public LocalDateTime localDateFromString(String string) {
        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    }
}
